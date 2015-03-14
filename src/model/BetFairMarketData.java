package model;

import java.util.List;

import betFairGSONClasses.MarketBook;
import betFairGSONClasses.Runner;

public class BetFairMarketData implements BetFairDataObject
{
	private String id;
	private double matchedAmount;
	private double unmatchedAmount;
	private MarketBook rawBook;
	
	public BetFairMarketData(MarketBook book)
	{
		id = book.getMarketId();
		matchedAmount = book.getTotalAvailable();
		unmatchedAmount = book.getTotalMatched();
		rawBook = book;
	}
	
	public MarketBook getRawBook()
	{
		return rawBook;
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
		return rawBook.getStatus();
	}
	
	public List<Runner> getRunners()
	{
		return rawBook.getRunners();
	}
	
	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public String getId()
	{
		return id;
	}

}
