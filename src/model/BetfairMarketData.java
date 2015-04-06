package model;

import java.util.List;

import betfairGSONClasses.MarketBook;
import betfairGSONClasses.Runner;

/**
 * Object representing the information extracted from Betfair markets
 * @author Craig
 *
 */
public class BetfairMarketData implements BetfairDataObject
{
	private String marketId;
	private String marketStatus;
	private double matchedAmount;
	private double unmatchedAmount;
	private List<Runner> marketRunners;

	private long receivedTimeStamp;
	
	/**
	 * Create a new BetFairMarketData object
	 * @param book The MarketBook that information will be extracted from
	 */
	public BetfairMarketData(MarketBook book)
	{
		marketId = book.getMarketId();
		marketStatus = book.getStatus();
		matchedAmount = book.getTotalAvailable();
		unmatchedAmount = book.getTotalMatched();
		marketRunners = book.getRunners();
		receivedTimeStamp = System.currentTimeMillis();
	}
	
	public long getReceivedTime()
	{
		return receivedTimeStamp;
	}
	
	public double getMatchedAmount()
	{
		return matchedAmount;
	}
	
	public double getUnmatchedAmount()
	{
		return unmatchedAmount;
	}
	
	public double getTotalAmount()
	{
		return matchedAmount + unmatchedAmount;
		
	}
	
	public String getStatus()
	{
		return marketStatus;
	}
	
	public List<Runner> getRunners()
	{
		return marketRunners;
	}
	
	/**
	 * Not supported for this object
	 * @return null
	 */
	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public String getMarketId()
	{
		return marketId;
	}
}