package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used for passing events from DataRecorder to DataAnalysis
 * @author Craig
 *
 */
public class EventList
{
	private List<BetfairMarketItem> probabilityData;
	private List<String> closedMarkets;
	private Date startTime;
	
	/**
	 * Creates a new EventList object
	 * @param data A list of markets with probability data and timestamps for each of their runners
	 * @param closedMarketsList The list of all closed markets (names not ids)
	 * @param marketStartDate the time in ms from epoch that the market opens
	 */
	public EventList(List<BetfairMarketItem> data, List<String> closedMarketsList, long marketStartDate)
	{
		probabilityData = data;
		closedMarkets = closedMarketsList;
		startTime = new Date(marketStartDate);
	}
	
	public Date getStartTime()
	{
		return startTime;
	}

	/**
	 * 
	 * @return The set of probabilities for each market and its runners
	 */
	public List<BetfairMarketItem> getProbabilites()
	{
		return probabilityData;
	}
	
	/**
	 * @return The list markets that have closed (na
	 */
	public List<String> getClosedMarkets()
	{
		return closedMarkets;
	}
}