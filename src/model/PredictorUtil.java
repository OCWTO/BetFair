package model;

public class PredictorUtil
{
	private int runnerIndex;
	private double spikeValue;
	private double avgBeforeSpike;
	private double maxBeforeSpike;
	private String timeStamp;
	
	public PredictorUtil(int index, double spike, double avgBefore, double maxBefore, String time)
	{
		runnerIndex = index;
		spikeValue = spike;
		avgBeforeSpike = avgBefore;
		maxBeforeSpike = maxBefore;
		timeStamp = time;
	}
	
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
