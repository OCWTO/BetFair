package model;

import java.util.Date;

import betFairGSONClasses.Event;

public class BetFairGameObject implements BetFairDataObject
{
	private String countryCode;
	private String gameName;
	private String gameId;
	private Date gameStartTime;
	
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