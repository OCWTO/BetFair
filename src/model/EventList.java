package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Stores probability data, closed market information, game events
public class EventList
{
	private List<BetFairMarketItem> probabilityData;
	private List<String> closedMarkets;
	private List<String> events;
	private Date startTime;
	
	public EventList(List<BetFairMarketItem> data, List<String> closedMarketsList, long marketStartDate)
	{
		probabilityData = data;
		closedMarketsList = closedMarkets;
		events = new ArrayList<String>();
		startTime = new Date(marketStartDate);
	}
	
	public Date getStartTime()
	{
		return startTime;
	}
	
	public void addGameEvents(String event)
	{
		events.add(event);
	}
	
	public List<BetFairMarketItem> getProbabilites()
	{
		return probabilityData;
	}
	
	public List<String> getClosedMarkets()
	{
		return closedMarkets;
	}
}