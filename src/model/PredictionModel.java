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
	private static final int halfLength = 45;
	private static final int breakDuration = 15;
	private static final int previousPointTrackCount = 8;

	private static long secondHalfStartTime = 0;
	
	private String marketName;
	private List<LinkedList<String>> timeStamps;
	private List<LinkedList<Double>> probabilities;
	private String[] runnerNames;
	private Date marketStartTime;
	private boolean ignoreVal = false;
	private static double pctChangeThreshold = 5.0;
	
	private List<PredictorUtil> inProgressPredictions;
	double spikeProbability;
	String spikeTimeStamp;
	private boolean currentlyChecking = false;
	
	

	private List<Double> checkedProbabilities;
	private List<String> checkedTimeStamps;
	
	
	private int spikeRunnerIndex;
	private int iterationCount;
	private String spikeRunnerName;
	/**
	 * Gui exists, observes predictionmodel. Predictionmodel is referred to by the gui and gamerecorder
	 * gamerecorder feeds it data, if events pop gui gets told
	 * 
	 * maybe make a new class to feed data
	 * feed it events every timer tick? so it knows game time and when stuff occurs
	 */
	public PredictionModel(String marketName, Date startTime)
	{
		inProgressPredictions = new ArrayList<PredictorUtil>();
		this.marketName = marketName;
		marketStartTime = startTime;
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
				//iterationCount++;
			}
		}
		iterationCount++;
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
			runnersProbability.addLast(probabilityValue);
//			if(marketName.equals("Match Odds"))
//					{
//				System.out.println("ADDING PROB " + probabilityValue  + " FOR RUNNER " + runnerName);
//					}
			if(runnersProbability.size() > previousPointTrackCount)
			{
				double x = runnersProbability.removeFirst();		
				//System.out.println("      POPPING OFF " + x);
			}
		}
	}

	private void addTimeStampRecord(String runnerName, Long valueOf)
	{
		if(!ignoreVal)
		{
			LinkedList<String> timeStampsForRunner = getTimeStampForRunner(runnerName);
			String time = convertMsTimeToGame(valueOf);
			if(time != null)
			{
//				if(marketName.equals("Match Odds"))
//			{
//				System.out.println("ADDING TIMESTAMP " + time + " FOR RUNNER " + runnerName);
//			}
				timeStampsForRunner.addLast(time);
				//System.out.println("ADDING");
				
				if(timeStampsForRunner.size() > previousPointTrackCount)
				{
					String x = timeStampsForRunner.removeFirst();
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
		if(inProgressPredictions.size() > 0)
		{
			//System.out.println("TRYING TO VERIFY");
			return verifyProbabilitySpikes();
		}
			//System.out.println("CHECKING");
			checkForProbabilitySpikes();
			return new ArrayList<String>();
	}

	private List<String> verifyProbabilitySpikes()
	{
		//System.out.println("TRYING TO VERIFY");
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
		
		//Split up the 8 indexes, we want the spike to be ind 0, so 3 before 4 after
		//System.out.println(timeStamps.indexOf((spikeTimeStamp)));
		//System.out.println("SPIKE TS IS " + spikeTimeStamp);
		PredictorUtil possibleEvent;
		
		for(int i = 0; i < inProgressPredictions.size(); i++)
		{
			possibleEvent = inProgressPredictions.get(i);
			
			if(timeStamps.get(possibleEvent.getRunnerIndex()).indexOf(possibleEvent.getTimeStamp()) == 3)
			{
				System.out.println("IN PLACE CHECK");
				
				//find avg after from ind 4 to 7
				double avgAfterSpike = 0;
				for(int j = 4; j < 8; j++)
				{
					avgAfterSpike += probabilities.get(possibleEvent.getRunnerIndex()).get(j);
				}
				avgAfterSpike = avgAfterSpike / 4;
				
				if((avgAfterSpike - possibleEvent.getAvgBeforeSpike()) > 0.1)
				{
					System.out.println("WINNER WINNER CHICKEN DINNER");
					System.out.println("PREDICTION FOR " + marketName + " RUNNER " + runnerNames[possibleEvent.getRunnerIndex()] + " TIME " + possibleEvent.getTimeStamp());
				}
				inProgressPredictions.remove(i);
				i--;
			}
		}
		
		
		if(timeStamps.get(spikeRunnerIndex).indexOf(spikeTimeStamp) == 3)
		{
			System.out.println("IN PLACE");
			//Checking that the change is consistant (all values after the spike (avg) are greater than the avg before
			
			double avgBeforeSpike = 0;
			double avgAfterSpike = 0;
			
			//Get the avg of the first 3 values
			for(int i = 0; i < 3; i++)
			{
				avgBeforeSpike+=probabilities.get(spikeRunnerIndex).get(i);
			}
			avgBeforeSpike = avgBeforeSpike / 3;
			
			//Get the avg of the last 4
			for(int i = 4; i < 8; i++)
			{
				avgAfterSpike+=probabilities.get(spikeRunnerIndex).get(i);
			}
			avgAfterSpike = avgAfterSpike / 4;
			
			//if the change from before to after is bigger than 3% event it thrown
			if(getPercentageChange(avgBeforeSpike, avgAfterSpike) > 3)
			{
				predicted.add(marketName + " PREDCTION FOR " + runnerNames[spikeRunnerIndex]);
			}
			System.out.println("PRED FALE");
			currentlyChecking = false;
			return predicted;
		}
		return new ArrayList<String>();
		
		
		
		
		
		
		
		
//		
//		//If theres 2 new probabilities entered after the spike
//		if((iterationCount - 3) == spikeIterationCount)
//		{
//			boolean holdingCondition = true;
//			//For the last 3 elements after the spike
//			for(int i = probabilities.get(spikeRunnerIndex).size() - 3; i < probabilities.get(spikeRunnerIndex).size(); i++)
//			{
//				double diffFromSpikeToNext = Math.abs(spikeValue - probabilities.get(spikeRunnerIndex).get(i));
//				double diffFromBeforeToNext = Math.abs(valueBeforeSpike - probabilities.get(spikeRunnerIndex).get(i));
//				
//				if(diffFromSpikeToNext > diffFromBeforeToNext)
//				{
//					holdingCondition = false;
//				}
//			}
//			
//			//Correct prediction so we go on to blacklist 
//			if(holdingCondition)
//			{
//				System.out.println("VALUE BEFORE SPIKE WAS " + valueBeforeSpike);
//				System.out.println("SPIKE VALUE IS " + spikeValue);
//				System.out.println("TIMESTAMPED SPIKE AT " + spikeTimeStamp);
//				predicted.add(marketName + " PREDCTION FOR " + runnerNames[spikeRunnerIndex]);
//				
//			}
//			currentlyChecking = false;
//			checkedProbabilities.add(spikeValue);
//			checkedTimeStamps.add(spikeTimeStamp);
			
	//	}
		//return predicted;
	}

	//returns true if the value before the spike is closer to the spikevalue than the mean before spike
	private boolean closerTo(double valueBeforeSpike, double spikeValue, double meanBeforeSpike)
	{
		double changeFromBeforeToSpike = getPercentageChange(valueBeforeSpike, spikeValue);
		double changeFromAverageToBefore = getPercentageChange(meanBeforeSpike, valueBeforeSpike);
		
		if(changeFromBeforeToSpike > changeFromAverageToBefore)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Analyse the currently existing probability data and look for spikes in the value
	 */
	private void checkForProbabilitySpikes()
	{		
		//For each runner
		for(int i = 0; i < probabilities.size(); i++)
		{
			//If we have a full index of points then we have enough to check
			if(probabilities.get(i).size() == previousPointTrackCount)
			{
				double olderValue = probabilities.get(i).get(6);
				double mostRecentValue = probabilities.get(i).get(7);
				
//				if(olderValue == 0.013150973)
//				{
//					System.out.println("FOUND IT TRUE");
//				}
				//If the change between the last 2 values is greater than the threshold 
				if(getPercentageChange(olderValue, mostRecentValue) > pctChangeThreshold)
				{
					//System.out.println("THRESHOLD MET " + marketName + " " + runnerNames[i] + " " + timeStamps.get(i).get(7) + " BEFORE " + olderValue + " AFTER " + mostRecentValue);
					double meanBeforeSpikeValues = 0;
					double maxValueBeforeSpike = Double.MIN_VALUE;
					
					for(int j = 0; j < 6; j++)
					{
						if(probabilities.get(i).get(j) > maxValueBeforeSpike)
						{
							maxValueBeforeSpike = probabilities.get(i).get(j);
						}
						meanBeforeSpikeValues+=probabilities.get(i).get(j);
					}
					meanBeforeSpikeValues = meanBeforeSpikeValues/6;
					
					//System.out.println("IM HERE");
					//If the spike value is 5% more than the average values before
					
					if(getPercentageChange(meanBeforeSpikeValues, mostRecentValue) > pctChangeThreshold/2)
					{
						if(getPercentageChange(maxValueBeforeSpike, mostRecentValue) > 5)
						{
							
						
						System.out.println("DETECTED FROM VAL " + olderValue + " TO " + mostRecentValue + " AT TIME " + timeStamps.get(i).get(7));
						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + marketName + " " + runnerNames[i]);
						//currentlyChecking = true;
						//public PredictorUtil(int index, double spike, double avgBefore, double maxBefore, String time)
						
						inProgressPredictions.add(new PredictorUtil(i, mostRecentValue, meanBeforeSpikeValues, maxValueBeforeSpike, timeStamps.get(i).get(7)));
						//spikeTimeStamp = timeStamps.get(i).get(7);
						//spikeRunnerName = runnerNames[i];
						//spikeRunnerIndex = i;
					}
					}
					
					//If the older value is closer to the mean than the spike then nothing happened
					//Ohterwise we think somethings happening
//					if(closerTo(olderValue, mostRecentValue, meanBeforeSpikeValues))
//					{
//						currentlyChecking = true;
//						spikeTimeStamp = timeStamps.get(i).get(7);
//						spikeRunnerName = runnerNames[i];
//						spikeRunnerIndex = i;
//					}
//					else
//					{
//						currentlyChecking = false;
//					}
				}
			}
		}
	}


	private double getPercentageChange(double oldVal, Double newVal)
	{
		double changeInVal = newVal - oldVal;
		double pctChange = (changeInVal/oldVal)*100;
//		if(marketName.equals("Match Odds") && iterationCount < 400  && iterationCount > 300)
//		{
//		
//			System.out.println("COMPARING " + oldVal + " TO " + newVal + " FOR MARKET " + marketName);
//			System.out.println("VAL 1 " + oldVal);
//			System.out.println("VAL 2 " + newVal);
//			System.out.println("CHANGE IN VAL " + changeInVal);
//			System.out.println("PCT IS " + pctChange);
//		
//		}
		return pctChange;
	}

	
}