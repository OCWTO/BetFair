package model;

import java.util.ArrayList;
import java.util.List;
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
	
	/**
	 * Gui exists, observes predictionmodel. Predictionmodel is referred to by the gui and gamerecorder
	 * gamerecorder feeds it data, if events pop gui gets told
	 * 
	 * maybe make a new class to feed data
	 * feed it events every timer tick? so it knows game time and when stuff occurs
	 */
	public PredictionModel(String marketName)
	{
		this.marketName = marketName;
		timeStamps = new ArrayList<String[]>();
		probabilities = new ArrayList<double[]>();
	}
	
	//if val is 0 then we just ignore
	public void addData(List<BetFairProbabilityItem> probabilites)
	{
		//Add new probability data, if this is 0 then it means themarket is shut
		for(BetFairProbabilityItem probability : probabilites)
		{
			
		}
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
	}
}
//Maintain a number of arrays for each runner. All data is always passed in the same order so no need to remmeber
//names?

