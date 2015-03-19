package model;

import java.util.ArrayList;
import java.util.List;

//Stores probability data, closed market information, game events
public class EventList
{
	private List<BetFairMarketItem> probabilityData;
	private List<String> closedMarkets;
	private List<String> events;
	
	public EventList(List<BetFairMarketItem> data, List<String> closedMarketsList)
	{
		probabilityData = data;
		closedMarketsList = closedMarkets;
		events = new ArrayList<String>();
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