package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

//If a market is closed then we locate its last data
/*
 * This needs to be passed in closed market list and probability data.
 */

public class PredictionModel
{
	private String marketName;
	private List<String[]> timeStamps;
	private List<double[]> probabilities;
	private static final int previousPointTrackCount = 6;
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
		timeStamps = new ArrayList<String[]>();
		probabilities = new ArrayList<double[]>();
		marketStartTime = startTime;
		marketClosed = false;
	}
	
	//if val is 0 then we just ignore
	public void addData(List<BetFairProbabilityItem> newProbabilites)
	{
		//System.out.println();
		//System.out.println();
		//This  means that the market has closed, so we try make our predictions.
		if(newProbabilites == null)
		{	
			return;
		}
		//Add new probability data, if this is 0 then it means themarket is shut
		//System.out.println("PROB LOOP START");
		//System.out.println(probabilities.size() + " iters expected");
		
		for(BetFairProbabilityItem probability : newProbabilites)
		{	
			for(int i = 0; i < runnerNames.length; i++)
			{
				//System.out.println("RUNNER LENGTH " + runnerNames.length + " NAME " + runnerNames[i]);
				if(runnerNames[i].equals(probability.getRunnerName()))
				{
					double probabilityVal = Double.parseDouble(probability.getProbability());
					double gameTime = convertToDouble(probability.getTimeStamp());
					break;
				}
			}
		}
		//System.out.println("PROP LOOP DONE");
	}

	private double convertToDouble(String timeStamp)
	{
		//System.out.println(timeStamp);
		long retrievedTime = Long.valueOf(timeStamp);
		long gameTimeInSeconds = (new Date(retrievedTime).getTime() - marketStartTime.getTime())/1000;
		//System.out.println("TIME IN S " + gameTimeInSeconds);
		int minCount = 0;
		int secondCount = 0;
		
		//if more than 1min has passed
		if(gameTimeInSeconds/60 > 1)
		{
			minCount = (int) (gameTimeInSeconds/60);
			secondCount = (int) (gameTimeInSeconds % 60);
			
		}
		else
		{
			secondCount = (int) (gameTimeInSeconds % 60);
		}
		double minsAndSeconds = minCount + ((double) secondCount/100);
		//System.out.println("TimeStamp " + minsAndSeconds);
		
		return 0;
	}

	public String getMarketName()
	{
		return marketName;
	}
	
	public void init(List<BetFairProbabilityItem> probabilities2)
	{
		runnerNames =  new String[probabilities2.size()];
		
		for(int i = 0; i < probabilities2.size(); i ++)
		{
			double[] probabilitiesArr = new double[previousPointTrackCount];
			probabilitiesArr[0] = Double.parseDouble(probabilities2.get(i).getProbability());
			probabilities.add(probabilitiesArr);
			
			String[] timeStampsArr = new String[previousPointTrackCount];
			timeStampsArr[0] = probabilities2.get(i).getTimeStamp();
			timeStamps.add(timeStampsArr);
			
			runnerNames[i] = probabilities2.get(i).getRunnerName();
		}	
		//TODO here we can throw out favoured team event and game start event
	}

	public List<String> getPredictions()
	{
		List<String> predictions = new ArrayList<String>();
		//For each runner we analyse our points, if we have less than previousPointTrackCount then we do nothing
		
		// TODO Auto-generated method stub
		return predictions;
	}

}
//Maintain a number of arrays for each runner. All data is always passed in the same order so no need to remmeber
//names?

