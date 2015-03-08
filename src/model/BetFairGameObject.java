package model;

import betFairGSONClasses.Event;

public class BetFairGameObject
{
	private Event gameObj;
	
	public BetFairGameObject(Event gameObject)
	{
		gameObj = gameObject;
	}
	
	public String getId()
	{
		return gameObj.getId();
	}
	
	public String getCountryCode()
	{
		return gameObj.getCountryCode();
	}
	
	public String getName()
	{
		return gameObj.getName();
	}
	
	public String getStartTime()
	{
		return gameObj.getOpenDate().toString();
	}
	
	@Override
	public String toString()
	{
		return "NAME: " + getName() +". ID: " + getId() + ". COUNTRYCODE: " + getCountryCode() +". TIME: " + getStartTime();
	}
}