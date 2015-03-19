package model;

import java.util.ArrayList;
import java.util.List;

//This represents a market so...
public class BetFairMarketItem
{
	private List<BetFairProbabilityItem> runnerProbability;
	private String marketName;
	
	public BetFairMarketItem(String marketName)
	{
		this.marketName = marketName;
		runnerProbability = new ArrayList<BetFairProbabilityItem>();
	}
	
	public void addRunnerProbability(BetFairProbabilityItem value)
	{
		runnerProbability.add(value);
	}
	
	public String getMarketName()
	{
		return marketName;
	}
	
}
