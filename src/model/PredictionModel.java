package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//If a market is closed then we locate its last data
/*
 * This needs to be passed in closed market list and probability data.
 */

public class PredictionModel
{
	private static boolean firstHalfOver = false;
	private static final int halfLength = 45;
	private static final int breakDuration = 15;
	private static final int previousPointTrackCount = 8;
	private static final int trendActive = 4;
	private static long secondHalfStartTime = 0;
	private static long firstHalfExtraTime;
	
	private String marketName;
	private List<LinkedList<String>> timeStamps;
	private List<LinkedList<Double>> probabilities;
	private String[] runnerNames;
	private Date marketStartTime;
	private boolean marketClosed;
	private boolean ignoreVal = false;
	private static double pctChangeThreshold = 5.0;
	
	
	double spikeProbability;
	String spikeTimeStamp;
	
	
	
	private boolean currentlyChecking = false;
	
	
	private int checkedRunnerIndex;
	private double valueBeforeSpike;
	
	private List<Double> checkedProbabilities;
	private List<String> checkedTimeStamps;
	
	private double spikeValue;
	private double spikeIndex;
	//private String spikeTime
	private int spikeIterationCount;
	private int spikeRunnerIndex;
	private int iterationCount;
	/**
	 * Gui exists, observes predictionmodel. Predictionmodel is referred to by the gui and gamerecorder
	 * gamerecorder feeds it data, if events pop gui gets told
	 * 
	 * maybe make a new class to feed data
	 * feed it events every timer tick? so it knows game time and when stuff occurs
	 */
	public PredictionModel(String marketName, Date startTime)
	{
		this.marketName = marketName;
		marketStartTime = startTime;
		marketClosed = false;
		probabilities = new ArrayList<LinkedList<Double>>();
		timeStamps = new ArrayList<LinkedList<String>>();
		spikeTimeStamp = "";
		//spikeProbability = new ArrayList<String>();
		
		
		checkedProbabilities = new ArrayList<Double>();
		checkedTimeStamps = new ArrayList<String>();
	}
	
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
	//Add data to our existing collections
	public void addData(List<BetFairProbabilityItem> newProbabilites)
	{
		//Add one timestamp for this call, all runners data comes in at
		//the same time so one collective timestamp is suitable.
		

		//For each runners data
		for(BetFairProbabilityItem probability: newProbabilites)
		{
			addProbabilityData(probability.getRunnerName(), probability.getProbability());
			addTimeStampRecord(probability.getRunnerName(), Long.valueOf(newProbabilites.get(0).getTimeStamp()));
			
			if(ignoreVal)
			{
				ignoreVal = false;
			}
			else
			{
				iterationCount++;
			}
		}
	}

	private void addProbabilityData(String runnerName, String probability)
	{
		LinkedList<Double> runnersProbability = getProbsForRunner(runnerName);
		double probabilityValue = Double.valueOf(probability);
		
		if(probabilityValue == 0.0)
		{
			ignoreVal = true;
		}
		else
		{
			runnersProbability.addFirst(probabilityValue);
			
			if(runnersProbability.size() > previousPointTrackCount)
			{
				double x = runnersProbability.removeLast();		
				//System.out.println("      POPPING OFF " + x);
			}
		}
	}

	private void addTimeStampRecord(String runnerName, Long valueOf)
	{
		if(!ignoreVal)
		{
			LinkedList<String> runnersProbability = getTimeStampForRunner(runnerName);
			String time = convertMsTimeToGame(valueOf);
			if(time != null)
			{
				runnersProbability.addFirst(time);
				
				if(runnersProbability.size() > previousPointTrackCount)
				{
					String x = runnersProbability.removeLast();
					//System.out.print("POPPING OFF " + x);
				}
			}
		}
	}

	private String convertMsTimeToGame(Long timeInMsFromEpoch)
	{
		return getGameTime(timeInMsFromEpoch, secondHalfStartTime == 0);
	}
	
	private String getGameTime(Long timeInMsFromEpoch, boolean firstHalfOver)
	{
		if(timeInMsFromEpoch < secondHalfStartTime)
		{
			//System.out.println("NOT STARTED HALF YET");
			return null;
		}
		
		long startTime = (secondHalfStartTime == 0) ? marketStartTime.getTime() : secondHalfStartTime;
		long minCount = (secondHalfStartTime == 0) ? 0 : 45;
		String extraTimeStartsAt = (secondHalfStartTime == 0) ? "45" : "90";
		long currentHalfDuration = timeInMsFromEpoch - startTime;
		long timeInSeconds = currentHalfDuration/1000;
		
		long secondCount;
		String gameTime = "";
		
		//If in extra time
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
		//System.out.println(gameTime);
		return gameTime;
	}
	
	
	

	
	//We get told when the first half ended, allows us to know when 2nd half starts
	public static void setFirstHalfTimeEnd(long halfTimeEnd)
	{
		secondHalfStartTime = halfTimeEnd + (breakDuration * 60 * 1000);
	}

	public String getMarketName()
	{
		return marketName;
	}
	
	//public void 
	
	
	public void init(List<BetFairProbabilityItem> newProbabilities)
	{
		//Define the size of runner array from the number of indexes
		runnerNames =  new String[newProbabilities.size()];
		//System.out.println(marketName + " ADDING DATA FOR RUNNERS");
		
		BetFairProbabilityItem runnerData;
		
		
		//addTimeStampRecord(Long.valueOf(newProbabilities.get(0).getTimeStamp()));
		//For each runners data
		for(int i = 0; i < newProbabilities.size(); i ++)
		{
			runnerData = newProbabilities.get(i);
			
			runnerNames[i] = runnerData.getRunnerName();
			
			LinkedList<Double> probValues = new LinkedList<Double>();
			probValues.add(Double.parseDouble(runnerData.getProbability()));
			probabilities.add(probValues);
			
			LinkedList<String> timeValues = new LinkedList<String>();
			timeValues.add(convertMsTimeToGame(Long.valueOf(runnerData.getTimeStamp())));
			timeStamps.add(timeValues);
		}	
	}
	
	public List<String> getPreds()
	{
		//List<String> predictions = new ArrayList<String>();
		
		if(currentlyChecking)
		{
			//System.out.println("IS CHECKING");
			return verifyProbabilitySpikes();
		}
		else
		{
			checkForProbabilitySpikes();
			return new ArrayList<String>();
		}
	}

	private List<String> verifyProbabilitySpikes()
	{
		List<String> predicted = new ArrayList<String>();
		// TODO Auto-generated method stub
		/*
		 * spikeIterationCount = iterationCount;
		valueBeforeSpike = previousProb;
		spikeValue = spikeProb;
		spikeTimeStamp = spikeTime;
		spikeRunnerIndex = runnerIndex;
		currentlyChecking = true;
		 */
		
		//If theres 2 new probabilities entered after the spike
		if((iterationCount - 3) == spikeIterationCount)
		{
			boolean holdingCondition = true;
			//For the last 3 elements after the spike
			for(int i = probabilities.get(spikeRunnerIndex).size() - 3; i < probabilities.get(spikeRunnerIndex).size(); i++)
			{
				double diffFromSpikeToNext = Math.abs(spikeValue - probabilities.get(spikeRunnerIndex).get(i));
				double diffFromBeforeToNext = Math.abs(valueBeforeSpike - probabilities.get(spikeRunnerIndex).get(i));
				
				if(diffFromSpikeToNext > diffFromBeforeToNext)
				{
					holdingCondition = false;
				}
			}
			
			//Correct prediction so we go on to blacklist 
			if(holdingCondition)
			{
				predicted.add(marketName + " PREDCTION FOR " + runnerNames[spikeRunnerIndex]);
				
			}
			currentlyChecking = false;
			checkedProbabilities.add(spikeValue);
			checkedTimeStamps.add(spikeTimeStamp);
			
		}
		return predicted;
	}

	/**
	 * Analyse the currently existing probability data and look for spikes in the value
	 */
	private void checkForProbabilitySpikes()
	{
		double previousProb = 0.0;
		
		//For each runners probability set
		for(int i = 0; i < probabilities.size(); i++)
		{
			//If we have full list of probability data then proceeed
			if(probabilities.get(i).size() == previousPointTrackCount)
			{
				//Loop through the individual data
				for(int j = 0; j < probabilities.get(i).size(); j++)
				{
					//0th index is our base point
					if(j == 0)
					{
						previousProb = probabilities.get(i).get(j);
					}
					else
					{
						double pctChange = getPercentageChange(previousProb, probabilities.get(i).get(j));
						
						//If the change is high enough
						if(pctChange > pctChangeThreshold && !isBlackListed(probabilities.get(i).get(j), timeStamps.get(i).get(j)))
						{
							startChecking(previousProb, probabilities.get(i).get(j), timeStamps.get(i).get(j), i);
							return;
						}
					}
				}
			}
		}
	}

	private boolean isBlackListed(Double double1, String string)
	{
		if(checkedProbabilities.contains(double1) || checkedTimeStamps.contains(string))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void startChecking(double previousProb, double spikeProb,
			String spikeTime, int runnerIndex)
	{
		spikeIterationCount = iterationCount;
		valueBeforeSpike = previousProb;
		spikeValue = spikeProb;
		spikeTimeStamp = spikeTime;
		spikeRunnerIndex = runnerIndex;
		currentlyChecking = true;
	}

	//i is runner, j is index
	public List<String> getPredictions()
	{
		List<String> predictions = new ArrayList<String>();
		
		double previousPoint = 0;
		
		//For each runner
		for(int i = 0; i < probabilities.size(); i++)
		{
			//If we're not at the threshhold yet then do nothing
			if(probabilities.get(i).size() != previousPointTrackCount)
			{
				//break;
			}
			else
			{
				//For each probabilitiy value
				for(int j = 0; j < probabilities.get(i).size(); j++)
				{
					if(j == 0)
					{
						previousPoint = probabilities.get(i).get(j);
					}
					//Non 0th point
					else
					{
						if(currentlyChecking)
						{
							int ind = probabilities.get(checkedRunnerIndex).indexOf(spikeTimeStamp);
							
							//If we have 3 points ahead of the spike then we can see if its a true condition
							if(ind == 4)
							{
								boolean condHolds = true;
								//For future points
								for(int a = 5; a < probabilities.get(checkedRunnerIndex).size(); a++)
								{
									double diffBeforeSpikeToNow = probabilities.get(checkedRunnerIndex).get(a) - valueBeforeSpike;
									double diffAfterSpikeToNow = probabilities.get(checkedRunnerIndex).get(a) - spikeProbability;
									
									if(diffAfterSpikeToNow > diffBeforeSpikeToNow)
									{
										condHolds = false;
									}
								}
								
								//if the condition holds
								if(condHolds)
								{
									predictions.add(marketName + " " + runnerNames[checkedRunnerIndex] + " SPIKE");
								}
								else
								{
									//reset all of our values.
								}
								currentlyChecking = false;
								checkedProbabilities.add(spikeProbability);
								checkedTimeStamps.add(spikeTimeStamp);
							}
							
						}
						else
						{
							//System.out.println("HIT THE ELSE");
							double pctChange = getPercentageChange(previousPoint, probabilities.get(i).get(j));
							previousPoint = probabilities.get(i).get(j);
							
							//If its a valid size change
							if(pctChange > pctChangeThreshold && !runnerNames[i].equals("The Draw") && !currentlyChecking)
							{
								//If this is a new calculated spike
								if(!(spikeProbability == previousPoint) && !spikeTimeStamp.equals(timeStamps.get(i).get(j))) 
								{
									valueBeforeSpike = previousPoint;
									checkedRunnerIndex = i;
									System.out.println("SPIKE DETECTED " + marketName);
									spikeTimeStamp = timeStamps.get(i).get(j);
									spikeProbability = previousPoint;
									System.out.println("PCT CHANGES " + pctChange + " FOR MARKET " + marketName + " WITH RUNNER " + runnerNames[i]);
									currentlyChecking = true;
								}
								//this is spike detection, next up is to check 3-4 points after it to make sure that
								//all points are closed to spike than other val
							}
						}
					}
				}
			}
		}
		return predictions;
		
		
		//we can tell that this is the first iter to give out special info by looking at timestamps size
		//if its size 1 and this is match odds then we know favoured team
		//if its 1 and this is first goalscorer we know whos predicted to score
		
	//	List<String> predictions = new ArrayList<String>();
		//For each runner we analyse our points, if we have less than previousPointTrackCount then we do nothing
		
		//return predictions;
	}

	private double getPercentageChange(double previousPoint, Double double1)
	{
		double changeInVal = double1 - previousPoint;
		double pctChange = (changeInVal/previousPoint)*100;
		return pctChange;
	}

	
}