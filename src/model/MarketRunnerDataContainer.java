package model;

import java.util.ArrayList;
import java.util.List;

public class MarketRunnerDataContainer
{
	private String runnerName;
	private List<String> runnersData;
	
	public MarketRunnerDataContainer(String name)
	{
		runnerName = name;
		runnersData = new ArrayList<String>();
	}
	
	public String getName()
	{
		return runnerName;
	}
	
	public void addData(long timeStampInMs, double probabilityValue)
	{
		runnersData.add(timeStampInMs + "," + probabilityValue);
	}
	
	public List<String> getRunnerDataList()
	{
		return runnersData;
	}
	
	public String getMostRecentProbability()
	{
		return runnersData.get(runnersData.size()-1).split(",")[1];
	}
	
	public String getMostRecentTimeStamp()
	{
		return runnersData.get(runnersData.size()-1).split(",")[0];
	}
}