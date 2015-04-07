package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import enums.MarketNames;

/**
 * This class is used to predict game events for football from given probabilities and football.
 * Each instance of a FootballPredictionModel represents a market whose data is getting the prediction
 * algorithm applied
 * @author Craig
 *
 */
public class FootballPredictionModel implements PredictionModel
{
	private static final int halfLength = 45;	//Football game half length in mins
	private static final int breakDuration = 15; //Half time duration in mins 
	private static final int previousPointTrackCount = 8; //Number of previous data points to store
	private static final int timeout = 6; //Number of points to not check for predictions after one is detected (stops false positives)
	private static long secondHalfStartTime = 0; //Used to decide when to change time from 0-45 to 45-90
	
	private String marketName;
	private List<LinkedList<String>> timeStamps;	//Each runner has a list of timestamps
	private List<LinkedList<Double>> probabilities; //Each runner has a list of probabilities
	private String[] runnerNames;					
	private Date marketStartTime;
	
	private List<PredictionModelUtil> inProgressPredictions;	//Predictions that pass phase 1 are in here, waiting for phase 2
	private boolean ignoreData = false;	//If prob value = 0 then this is used to stop timestamp/data being added
	private boolean closedMarket = false; 
	
	private String mostRecentTime;	//Last update time

	private List<Integer> runnerTimeouts;	//Timeout times for each runner (timeout = 6 is next 6 iterations no predictions for runner)


	/**
	 * Create a new prediction model for football
	 * @param marketName The markets name (e.g. match odds)
	 * @param startTime The time the market started/opens
	 */
	public FootballPredictionModel(String marketName, Date startTime)
	{
		this.marketName = marketName;
		marketStartTime = startTime;
		runnerTimeouts = new ArrayList<Integer>();
		inProgressPredictions = new ArrayList<PredictionModelUtil>();
		probabilities = new ArrayList<LinkedList<Double>>();
		timeStamps = new ArrayList<LinkedList<String>>();
	}
	
	/**
	 * @param runnerName The name of the runner the probabilities are for
	 * @return The list of probabilities for the requested runner
	 */
	private LinkedList<Double> getProbsForRunner(String runnerName)
	{
		for(int i = 0; i < runnerNames.length; i++)
		{
			if(runnerNames[i].equals(runnerName))
			{
				return probabilities.get(i);
			}
		}
		return null;
	}
	
	/**
	 * @param runnerName The name of the runner the timestamps are for
	 * @return The list of v for the requested runner
	 */
	private LinkedList<String> getTimeStampForRunner(String runnerName)
	{
		for(int i = 0; i < runnerNames.length; i++)
		{
			if(runnerNames[i].equals(runnerName))
			{
				return timeStamps.get(i);
			}
		}
		return null;
	}
	
	//Interface documents this
	public void addData(List<BetfairProbabilityItem> newProbabilites)
	{
		//For each runners data
		for(BetfairProbabilityItem probability: newProbabilites)
		{
			//Add the timestamps + probabiltiies
			addProbabilityData(probability.getRunnerName(), probability.getProbability());
			addTimeStampRecord(probability.getRunnerName(), Long.valueOf(newProbabilites.get(0).getTimeStamp()));
			
			//If its been market to ignore then turn it off for next iter(ignore is considered in the above 2 methods)
			if(ignoreData)
			{
				ignoreData = false;
			}
		}
	}

	/**
	 * Add the given values the internal collections
	 * @param runnerName Name of the runner 
	 * @param probability Probability value for the runner
	 */
	private void addProbabilityData(String runnerName, double probability)
	{
		LinkedList<Double> runnersProbability = getProbsForRunner(runnerName);
		double probabilityValue = Double.valueOf(probability);
		
		/*
		 * 0.0 is allowed on graphs etc but it's just added to the collections for filler
		 * so it decomplicates the issue of there not actually being a value. Adds 0 instead
		 * of having to deal with lots of conditionals if there's nothing added. If 0 is found
		 * then its not added and the timestamp is told not to add it too.
		 */
		if(probabilityValue == 0.0)
		{
			ignoreData = true;
		}
		else
		{
			//Adding to the back, popping old data from the front, just like visualising a graph
			runnersProbability.addLast(probabilityValue);

			//Keeps the collections to size 8
			if(runnersProbability.size() > previousPointTrackCount)
			{
				runnersProbability.removeFirst();		
			}
		}
	}

	public boolean isClosed()
	{
		return closedMarket;
	}

	public String getMostRecentTime()
	{
		return mostRecentTime;
	}
	
	/**
	 * Add the given values the internal collections
	 * @param runnerName Name of the runner 
	 * @param probability timestamp for the runner
	 */
	private void addTimeStampRecord(String runnerName, Long valueOf)
	{
		//As mentioned earlier, if ignore is true then nothings added so the data is skipped over
		if(!ignoreData)
		{
			LinkedList<String> timeStampsForRunner = getTimeStampForRunner(runnerName);
			String time = convertMsTimeToGame(valueOf);
			
			//Null is if its on half time break
			if(time != null)
			{
				timeStampsForRunner.addLast(time);
				mostRecentTime = time;
				
				if(timeStampsForRunner.size() > previousPointTrackCount)
				{
					timeStampsForRunner.removeFirst();
				}
			}
		}
	}

	/**
	 * Utility to convert ms from epoch time to actual game times like 34:03
	 * @param timeInMsFromEpoch
	 * @return The time in game e.g. 34:03
	 */
	private String convertMsTimeToGame(Long timeInMsFromEpoch)
	{
		return getGameTime(timeInMsFromEpoch, secondHalfStartTime == 0);
	}
	
	private String getGameTime(Long timeInMsFromEpoch, boolean firstHalfOver)
	{
		//If we know 1st half is over, so we know when 2nd half starts. If this is between 1st and 2nd halves
		if(timeInMsFromEpoch < secondHalfStartTime)
		{
			return null;
		}
		
		//Start time is game start time or 2nd half start
		long startTime = (secondHalfStartTime == 0) ? marketStartTime.getTime() : secondHalfStartTime;
		long minCount = (secondHalfStartTime == 0) ? 0 : 45;
		String extraTimeStartsAt = (secondHalfStartTime == 0) ? "45" : "90";
		long currentHalfDuration = timeInMsFromEpoch - startTime;
		long timeInSeconds = currentHalfDuration/1000;
		long secondCount;
		String gameTime = "";
		
		//If in extra time (half length = 45*60 = num of seconds then * 1000 for ms)
		if(currentHalfDuration > (1000 * (halfLength * 60)))
		{
			timeInSeconds = timeInSeconds - (halfLength * 60);			
			gameTime = extraTimeStartsAt + " + ";
			minCount = 0;
		}
		minCount+= timeInSeconds/60;
		secondCount = timeInSeconds%60;
		String conditional = (secondCount >= 10) ? "" : "0";
		gameTime = gameTime + minCount + ":" + conditional + secondCount;
		return gameTime;
	}
	
	/**
	 * Tell the class that the first half has ended. Allows calculation of 2nd half start time
	 * @param halfTimeEnd Time that the 1st half ended at in ms from epoch
	 */
	public static void setFirstHalfTimeEnd(long halfTimeEnd)
	{
		secondHalfStartTime = halfTimeEnd + (breakDuration * 60 * 1000);
	}

	public String getMarketName()
	{
		return marketName;
	}
	
	public void initialize(List<BetfairProbabilityItem> newProbabilities)
	{
		//Define the size of runner array from the number of indexes
		runnerNames =  new String[newProbabilities.size()];
		BetfairProbabilityItem runnerData;
		
		
		//For each runners data
		for(int i = 0; i < newProbabilities.size(); i ++)
		{
			//init the timeout list to 0 for all
			runnerTimeouts.add(0);
			
			runnerData = newProbabilities.get(i);
			
			runnerNames[i] = runnerData.getRunnerName();
			
			//Init the collections for each runner with the data
			LinkedList<Double> probValues = new LinkedList<Double>();
			probValues.add(runnerData.getProbability());
			probabilities.add(probValues);
			
			LinkedList<String> timeValues = new LinkedList<String>();
			timeValues.add(convertMsTimeToGame(runnerData.getTimeStamp()));
			timeStamps.add(timeValues);
			mostRecentTime = convertMsTimeToGame(runnerData.getTimeStamp());
		}	
	}
	
	//doc in interface
	public List<String> getPredictions()
	{
		//if the market is active
		if(!closedMarket)
		{
			checkForProbabilitySpikes();
			if(inProgressPredictions.size() > 0)
			{
				
				return verifyProbabilitySpikes();
			}
			return new ArrayList<String>();
		}
		//If the market is closed (there's a limitation in DataAnalysis that allows this to be called only once
		else
		{
			//If this market shuts then find out if its premature e.g. sending off market shut because sending off or just game over
			List<String> prediction = new ArrayList<String>();
			prediction.add(mostRecentTime + "," + marketName + ",CLOSED");
			
			//If its our special tracked market
			if(marketName.equals(MarketNames.SENDING_OFF.toString()) || marketName.equals(MarketNames.PENALTY_TAKEN.toString()))
			{
				String strGameMins = mostRecentTime.charAt(0) + "" + mostRecentTime.charAt(1);
				int gameTime = Integer.parseInt(strGameMins);
				if(gameTime <= 90)
				{
					prediction.add(mostRecentTime + "," + marketName + " OCCURRED," + " UNKNOWN");
				}
			}
			return prediction;
		}
	}

	/**
	 * This method is used to verify possible events that have been recognised (data is in inProgressPredictions)
	 * @return List of predicitions
	 */
	private List<String> verifyProbabilitySpikes()
	{
		List<String> predictions = new ArrayList<String>();
		
		PredictionModelUtil possibleEvent;
		
		//For each of the possible predicitions
		for(int i = 0; i < inProgressPredictions.size(); i++)
		{
			possibleEvent = inProgressPredictions.get(i);
			
			//If its in index 3 (so 4 more values after the spike have been stored)
			if(timeStamps.get(possibleEvent.getRunnerIndex()).indexOf(possibleEvent.getTimeStamp()) == 3)
			{
				double avgAfterSpike = 0;
				double maxAfterSpike = Double.MIN_VALUE;
				double minAfterSpike = Double.MAX_VALUE;
				
				//Loop through the 4 new values and get avg, max and min
				for(int j = 4; j < 8; j++)
				{
					if(probabilities.get(possibleEvent.getRunnerIndex()).get(j) > maxAfterSpike)
					{
						maxAfterSpike = probabilities.get(possibleEvent.getRunnerIndex()).get(j);
					}
					if(probabilities.get(possibleEvent.getRunnerIndex()).get(j) < minAfterSpike)
					{
						minAfterSpike = probabilities.get(possibleEvent.getRunnerIndex()).get(j);
					}
					avgAfterSpike += probabilities.get(possibleEvent.getRunnerIndex()).get(j);
				}
				avgAfterSpike = avgAfterSpike / 4;

				//Make sure the max before is less than the max after
				if(possibleEvent.getMaxBeforeSpike() < minAfterSpike)
				{
					if(marketName.equals(MarketNames.MATCH_ODDS.toString()))
					{
						predictions.add(possibleEvent.getTimeStamp() + "," + "GOAL," + runnerNames[possibleEvent.getRunnerIndex()]);
					}
					//Remove old timeout value for this runner (0) and set the new (6)
					runnerTimeouts.remove(i);
					runnerTimeouts.add(i, timeout);
				}
				inProgressPredictions.remove(i);
				i--;
				
				//Remove any other predicitons in progress for this runner, to stop false +ves. This occurs when increase keeps happening after spike
				for(int j  = 0; j < inProgressPredictions.size() ; j++)
				{
					if(inProgressPredictions.get(j).getRunnerIndex() == possibleEvent.getRunnerIndex())
					{
						inProgressPredictions.remove(j);
						j--;
					}
				}
			}
		}
		//No in progress predictions are ready or none pass 
		return predictions;
	}

	//Interface has docs
	public List<String> getRecentValues()
	{
		List<String> runnersAndValues = new ArrayList<String>();
		
		for(int i = 0; i < runnerNames.length; i++)
		{
			String runnerName = runnerNames[i];
			String runnerTimeStamp = timeStamps.get(i).get(timeStamps.get(i).size() - 1 );
			double runnerProbability = probabilities.get(i).get(probabilities.get(i).size() - 1);
			runnersAndValues.add(runnerName + "," + runnerTimeStamp + "," + runnerProbability);
		}
		return runnersAndValues;
	}

	//Interface has docs
	public String getFavouredRunner()
	{
		int runnerInd = 0;
		double bestProb = 0.0;
			
		for(int i = 0; i < probabilities.size(); i++)
		{
			if(probabilities.get(i).get(0) > bestProb)
			{
				bestProb = probabilities.get(i).get(0);
				runnerInd = i;
			}
		}
		return runnerNames[runnerInd];
	}
	
	/**
	 * Analyse the currently existing probability data and look for spikes in the value
	 */
	private void checkForProbabilitySpikes()
	{		
		double mult;
		double add;
		
		//For each runner
		for(int i = 0; i < probabilities.size(); i++)
		{
			//We ignore draw because other runners have info thats reflected there. Ignore those timed out too
			if(!runnerNames[i].contains("The Draw") && runnerTimeouts.get(i) == 0)
			{
				//If we have a full index of points then we have enough to check
				if(probabilities.get(i).size() == previousPointTrackCount)
				{
					double olderValue = probabilities.get(i).get(6);
					double mostRecentValue = probabilities.get(i).get(7);
	
					//If the value before spike was less than .2 (typically a underdog so their increase is smaller). Expect spike to be 2*val before
					if(olderValue < 0.2)
					{
						mult = 2;
						add = 0;
					}
					//Possibly even teams, so increase of prob on events is higher. Expect spike to be 1*val before + .075
					else
					{
						mult = 1;
						add = 0.075;
					}
					
					//If the spike is big enough to register
					if((olderValue * mult) + add <= mostRecentValue)
					{
						double meanBeforeSpike = 0;
						double maxBeforeSpike = Double.MIN_VALUE;
						
						//Get mean before spike and the max value
						for(int j = 0; j < 6; j++)
						{
							if(probabilities.get(i).get(j) > maxBeforeSpike)
							{
								maxBeforeSpike = probabilities.get(i).get(j);
							}
							meanBeforeSpike+=probabilities.get(i).get(j);
						}
						meanBeforeSpike = meanBeforeSpike/6;
							
						//Spike should be 5% increase on the avg before and 5% increase on the max before
						if(getPercentageChange(meanBeforeSpike, mostRecentValue) > 5.0 && getPercentageChange(maxBeforeSpike, mostRecentValue) > 5.0)
						{
							//Add it to list for 2nd stage of verification of event
							inProgressPredictions.add(new PredictionModelUtil(i, mostRecentValue, meanBeforeSpike, maxBeforeSpike, timeStamps.get(i).get(7)));
						}	
					}
				}
			}
			//If the draw or timed out
			else
			{
				//If its timed out then lower the counter
				if(runnerTimeouts.get(i) > 0)
				{
					int timeoutleft = runnerTimeouts.remove(i);
					timeoutleft--;
					runnerTimeouts.add(i,timeoutleft);
				}
			}
		}
	}


	/**
	 * @param oldVal The first point
	 * @param newVal The second point
	 * @return The percentage change from oldVal to newVal
	 */
	private double getPercentageChange(double oldVal, Double newVal)
	{
		double changeInVal = newVal - oldVal;
		double pctChange = (changeInVal/oldVal)*100;
		return pctChange;
	}

	public void closeMarket()
	{
		closedMarket = true;
	}
}