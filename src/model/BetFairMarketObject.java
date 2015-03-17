package model;

import java.util.Date;
import java.util.List;

import betFairGSONClasses.Event;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.RunnerCatalog;

public class BetFairMarketObject implements BetFairDataObject
{
	private String marketName;
	private String marketId;
	private List<RunnerCatalog> marketRunners;
	private Date marketOpenDate;
	private Event marketEvent;
	
	public BetFairMarketObject(MarketCatalogue market)
	{
		marketName = market.getMarketName();
		System.out.println(market.getEvent().getName() + " AXLALXAS " + marketName);
		marketId = market.getMarketId();
		marketRunners = market.getRunners();
		marketOpenDate = market.getEvent().getOpenDate();
		marketEvent = market.getEvent();
	}
	
	public Date getOpenDate()
	{
		return marketOpenDate;
	}

	public List<RunnerCatalog> getRunners()
	{
		return marketRunners;
	}
	
	public String getName()
	{
		return marketName;
	}

	public Event getMarketEvent()
	{
		return marketEvent;
	}
	
	public String getId()
	{
		return marketId;
	}
	
	@Override
	public String toString()
	{
		return marketName + " " + marketId;
	}
}
