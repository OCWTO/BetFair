package model;

public class BetFairProbabilityItem
{
	private String timeStamp;
	private String probability;
	private String runnerName;
	
	public BetFairProbabilityItem(String time, String runner, String prob)
	{
		timeStamp = time;
		runnerName = runner;
		probability = prob;
	}
	
	public String getTimeStamp()
	{
		return timeStamp;
	}
	
	public String getProbability()
	{
		return probability;
	}
	
	public String getRunnerName()
	{
		return runnerName;
	}
}