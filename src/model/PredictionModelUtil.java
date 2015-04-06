package model;

/**
 * Utility class used for PredictionModel. It is used to store data that is needed in order to detect if events occur.
 * Each instance is used to store data for one possible predicted event. It's used in the second stage of the prediction algorithm
 * @author Craig
 *
 */
public class PredictionModelUtil
{
	private int runnerIndex;
	private double spikeValue;
	private double avgBeforeSpike;
	private double maxBeforeSpike;
	private String timeStamp;
	
	/**
	 * Create a new PredictorUtil object
	 * @param index The index of the runnerNames collect (in PredictionModel) that the name of the runner is in
	 * @param spike The spike value
	 * @param avgBefore The average value before the spike
	 * @param maxBefore The maximum value that was before the spike
	 * @param time Time of the spike
	 */
	public PredictionModelUtil(int index, double spike, double avgBefore, double maxBefore, String time)
	{
		runnerIndex = index;
		spikeValue = spike;
		avgBeforeSpike = avgBefore;
		maxBeforeSpike = maxBefore;
		timeStamp = time;
	}
	
	/**
	 * 
	 * @return The index in the runnerNames collection that this runners name is in
	 */
	public int getRunnerIndex()
	{
		return runnerIndex;
	}
	
	public double getSpikeValue()
	{
		return spikeValue;
	}
	
	public double getAvgBeforeSpike()
	{
		return avgBeforeSpike;
	}
	
	public double getMaxBeforeSpike()
	{
		return maxBeforeSpike;
	}
	
	public String getTimeStamp()
	{
		return timeStamp;
	}
}
