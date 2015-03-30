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
	private static long secondHalfStartTime = 0;
	private static long firstHalfExtraTime;
	
	private String marketName;
	private LinkedList<String> timeStamps;
	private List<LinkedList<Double>> probabilities;
	private String[] runnerNames;
	private Date marketStartTime;
	private boolean marketClosed;
	
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
		timeStamps = new LinkedList<String>();
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
	
	//Add data to our existing collections
	public void addData(List<BetFairProbabilityItem> newProbabilites)
	{
		//Add one timestamp for this call, all runners data comes in at
		//the same time so one collective timestamp is suitable.
		
		addTimeStampRecord(Long.valueOf(newProbabilites.get(0).getTimeStamp()));

		//For each runners data
		for(BetFairProbabilityItem probability: newProbabilites)
		{
			addProbabilityData(probability.getRunnerName(), probability.getProbability());
		}
	}

	private void addProbabilityData(String runnerName, String probability)
	{
		LinkedList<Double> runnersProbability = getProbsForRunner(runnerName);
		double probabilityValue = Double.valueOf(probability);
		
		runnersProbability.addFirst(probabilityValue);
		
		if(runnersProbability.size() > previousPointTrackCount)
			runnersProbability.removeLast();		
	}

	private void addTimeStampRecord(Long valueOf)
	{
		String time = convertMsTimeToGame(valueOf);
		
		timeStamps.addFirst(time);
		
		if(timeStamps.size() > previousPointTrackCount)
			timeStamps.removeLast();
	}

	private String convertMsTimeToGame(Long timeInMsFromEpoch)
	{
		//If its the first half
		if(secondHalfStartTime == 0)
		{
			return getFirstHalfTime(timeInMsFromEpoch);
		}
		//if its the second half
		else
		{
			return getSecondHalfTime(timeInMsFromEpoch);
		}
	}
	
	private String getSecondHalfTime(Long timeInMsFromEpoch)
	{
		if(timeInMsFromEpoch < secondHalfStartTime)
		{
			System.out.println("NOT STARTED HALF YET");
			return null;
		}
		else
		{
		System.out.println("PRODUCING");
		long secondHalfStart = secondHalfStartTime;
		long currentHalfDuration = timeInMsFromEpoch - secondHalfStart;
		long timeInSeconds = currentHalfDuration/1000;
		long minCount = 45;;
		long secondCount;
		String gameTime = "";
		
		//If in extra time
		if(currentHalfDuration > (1000 * (halfLength * 60)))
		{
			timeInSeconds = timeInSeconds - (halfLength * 60);			
			gameTime = "90:00 + ";
			minCount = 0;
		}
		System.out.println("SECOND TIME " + timeInSeconds);
		minCount+= timeInSeconds/60;
		secondCount = timeInSeconds%60;
		System.out.println("MINS " + minCount);
		System.out.println("SECS " + secondCount);
		String conditional = (secondCount >= 10) ? "" : "0";
		gameTime = gameTime + minCount + ":" + conditional + secondCount;
		System.out.println(gameTime);
		return gameTime;
		}
	}

	public String getFirstHalfTime(Long timeInMsFromEpoch)
	{
		long startTime = marketStartTime.getTime();
		long currentGameDuration = timeInMsFromEpoch - startTime;
		long timeInSeconds = currentGameDuration/1000;
		long minCount;
		String gameTime = "";
		
		//if its greater than 45min from start then we adjust second count
		if(currentGameDuration > (1000 * (halfLength * 60)))
		{
			timeInSeconds = timeInSeconds - (halfLength * 60);			
			gameTime = "45:00 + ";
		}
		
		minCount = timeInSeconds/60;
		long secondCount = timeInSeconds%60;
		String conditional = (secondCount >= 10) ? "" : "0";
		gameTime = gameTime + minCount + ":" + conditional + (timeInSeconds % 60);
		System.out.println(gameTime);
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
	
	public void init(List<BetFairProbabilityItem> newProbabilities)
	{
		//Define the size of runner array from the number of indexes
		runnerNames =  new String[newProbabilities.size()];
		System.out.println(marketName + " ADDING DATA FOR RUNNERS");
		
		BetFairProbabilityItem runnerData;
		
		//For each runners data
		for(int i = 0; i < newProbabilities.size(); i ++)
		{
			runnerData = newProbabilities.get(i);
			System.out.println("RUNNER " + runnerData.getRunnerName());
			System.out.println("VAL " + runnerData.getProbability() + " TIME " + runnerData.getTimeStamp());
			
			
			runnerNames[i] = runnerData.getRunnerName();
			
			LinkedList<Double> probValues = new LinkedList<Double>();
			probValues.add(Double.parseDouble(runnerData.getProbability()));
			probabilities.add(probValues);
			
			timeStamps.add(runnerData.getTimeStamp());
		}	
	}

	public List<String> getPredictions()
	{
		//we can tell that this is the first iter to give out special info by looking at timestamps size
		//if its size 1 and this is match odds then we know favoured team
		//if its 1 and this is first goalscorer we know whos predicted to score
		
		List<String> predictions = new ArrayList<String>();
		//For each runner we analyse our points, if we have less than previousPointTrackCount then we do nothing
		
		return predictions;
	}

}