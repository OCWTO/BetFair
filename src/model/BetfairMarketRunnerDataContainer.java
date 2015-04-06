package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a collection of a runners probability data
 * @author Craig
 *
 */
public class BetfairMarketRunnerDataContainer
{
	private String runnerName;
	private List<String> runnersData;
	
	/**
	 * Create a BetfairMarketRunnerDataContainer for the given runner name
	 * @param name the name of the runner
	 */
	public BetfairMarketRunnerDataContainer(String name)
	{
		runnerName = name;
		runnersData = new ArrayList<String>();
	}
	
	public String getName()
	{
		return runnerName;
	}
	
	/**
	 * Add new runner data
	 * @param timeStampInMs time in ms from epoch that the data was acquired
	 * @param probabilityValue The probability at that timestamp
	 */
	public void addData(long timeStampInMs, double probabilityValue)
	{
		runnersData.add(timeStampInMs + "," + probabilityValue);
	}
	
	public List<String> getRunnerDataList()
	{
		return runnersData;
	}
	
	/**
	 * @return Get the most decent probability data that has been received for this runner e.g. 0.00232345
	 */
	public String getMostRecentProbability()
	{
		return runnersData.get(runnersData.size()-1).split(",")[1];
	}
	
	/**
	 * @return Get the most recent timestamp that this runner received probability data for.
	 */
	public String getMostRecentTimeStamp()
	{
		return runnersData.get(runnersData.size()-1).split(",")[0];
	}
}