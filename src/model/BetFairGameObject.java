package model;

import java.util.Date;

import betFairGSONClasses.Event;

/**
 * Object representing the required information for games, extracted from the Betfair API.
 * @author Craig Thomson
 *
 */
public class BetFairGameObject implements BetFairDataObject
{
	private String countryCode;
	private String gameName;
	private String gameId;
	private Date gameStartTime;
	
	/**
	 * Create a new BetFairGameObject object for the given Event
	 * @param gameObject An Event object which this class will extract required information from
	 */
	public BetFairGameObject(Event gameObject)
	{
		gameId = gameObject.getId();
		gameName = gameObject.getName();
		countryCode = gameObject.getCountryCode();
		gameStartTime = gameObject.getOpenDate();
	}
	
	public String getMarketId()
	{
		return gameId;
	}
	
	public String getCountryCode()
	{
		return countryCode;
	}
	
	public String getName()
	{
		return gameName;
	}
	
	public Date getStartTime()
	{
		return gameStartTime;
	}
	
	@Override
	public String toString()
	{
		return "NAME: " + getName() +". ID: " + getMarketId() + ". COUNTRYCODE: " + getCountryCode() +". TIME: " + getStartTime();
	}
}