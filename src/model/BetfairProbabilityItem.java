package model;

/**
 * This class represents probabilitiy objects, which are composed of timestamps, values and the name of the runner they are for
 * @author Craig Thomson
 *
 */
public class BetfairProbabilityItem
{
	private long timeStamp;
	private double probability;
	private String runnerName;
	
	/**
	 * Create a new BetFairProbabilityItem
	 * @param time The timestamp that the values were received (in the form of ms from epoch)
	 * @param runner The runners name
	 * @param prob The probability value
	 */
	public BetfairProbabilityItem(long time, String runner, double prob)
	{
		timeStamp = time;
		runnerName = runner;
		probability = prob;
	}
	
	/**
	 * @return Get the timestamp in the form of ms from epoch
	 */
	public long getTimeStamp()
	{
		return timeStamp;
	}
	
	/**
	 * @return get the probability value
	 */
	public double getProbability()
	{
		return probability;
	}
	
	public String getRunnerName()
	{
		return runnerName;
	}
}