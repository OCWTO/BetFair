package model;

import java.util.Date;
import java.util.List;

import betfairGSONClasses.Event;
import betfairGSONClasses.MarketCatalogue;
import betfairGSONClasses.RunnerCatalog;

/**
 * This class is used to represent Markets in the form of its general information
 * @author Craig
 *
 */
public class BetfairMarketObject implements BetfairDataObject
{
	private String marketName;
	private String marketId;
	private List<RunnerCatalog> marketRunners;
	private Date marketOpenDate;
	private Event marketEvent;

	/**
	 * Create a new BetFairMarketObject
	 * @param market A MarketCatalogue that this class will generate an object from
	 */
	public BetfairMarketObject(MarketCatalogue market)
	{
		marketName = market.getMarketName();
		marketId = market.getMarketId();
		marketRunners = market.getRunners();
		marketOpenDate = market.getEvent().getOpenDate();
		marketEvent = market.getEvent();
	}

	/**
	 * @return The date that the market opens
	 */
	public Date getOpenDate()
	{
		return marketOpenDate;
	}

	public List<RunnerCatalog> getRunners()
	{
		return marketRunners;
	}
	
	/**
	 * @return The name of the market
	 */
	public String getName()
	{
		return marketName;
	}

	/**
	 * @return The name of the game
	 */
	public String getGamesName()
	{
		return marketEvent.getName();
	}
	
	public String getMarketId()
	{
		return marketId;
	}
	
	@Override
	public String toString()
	{
		return marketName + " " + marketId;
	}
}
