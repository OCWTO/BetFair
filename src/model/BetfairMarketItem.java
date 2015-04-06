package model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a market and the probabilities that its runners
 * @author Craig
 *
 */
public class BetfairMarketItem
{
	private List<BetfairProbabilityItem> runnerProbability;
	private String marketName;
	
	/**
	 * Create a new BetFairMarketItem
	 * @param marketName the name of the market this object is meant to represent
	 */
	public BetfairMarketItem(String marketName)
	{
		this.marketName = marketName;
		runnerProbability = new ArrayList<BetfairProbabilityItem>();
	}
	
	/**
	 * Add a new ProbabilityItem to the Markets internal list.
	 * @param value The probability to be stored.
	 */
	public void addRunnerProbability(BetfairProbabilityItem value)
	{
		runnerProbability.add(value);
	}
	
	/**
	 * Get the probabilities for the market
	 * @return A list of BetFairProbabilityItems which have probability information such as runner name, value and time.
	 */
	public List<BetfairProbabilityItem> getProbabilities()
	{
		return runnerProbability;
	}
	
	public String getMarketName()
	{
		return marketName;
	}
}