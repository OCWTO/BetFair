package model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used to hold probablity data. It represents a market and it holds probabilty data for its runners and other useful information
 * @author Craig
 *
 */
public class BetfairMarketDataContainer
{
	private String gameName;
	private String marketName;
	private String marketId;
	private LocalTime marketOpenDate;
	private List<BetfairMarketRunnerDataContainer> runnerMarketData;
	private List<String> generalMarketData;
	
	/**
	 * Create a BetFairMarketDataContainer for the given game/market
	 * @param gamesName The name of the game that this market is for
	 * @param marketsName The name of the market
	 * @param marketsId The markets id
	 * @param marketsOpenDate The date that the market opens for betting
	 */
	public BetfairMarketDataContainer(String gamesName, String marketsName, String marketsId, Date marketsOpenDate)
	{
		gameName = gamesName;
		marketName = marketsName;
		marketId = marketsId;
		
		//Converting it to java 8 stuff
		Instant instant = Instant.ofEpochMilli(marketsOpenDate.getTime());
		marketOpenDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
		
		runnerMarketData = new ArrayList<BetfairMarketRunnerDataContainer>();
		generalMarketData = new ArrayList<String>();

	}
	
	/**
	 * Get the open date of the market in the form of hh:mm:ss
	 * @return The market open date in the form of a string in the above format
	 */
	public String getMarketOpenDate()
	{  
		return marketOpenDate.format(DateTimeFormatter.ofPattern("hh:mm:ss"));
	}
	
	public String getMarketName()
	{
		return marketName;
	}
	
	public String getGameName()
	{
		return gameName;
	}
	
	/**
	 * @return The list of all collected data (odds etc) for the market
	 */
	public List<String> getAllMarketDataContainer()
	{
		return generalMarketData;
	}
	
	public String getMarketId()
	{
		return marketId;
	}
	
	public void addNewRunner(String runnerName)
	{
		runnerMarketData.add(new BetfairMarketRunnerDataContainer(runnerName));
	}
	
	/**
	 * 
	 * @return Get lists of MarketRunnerDataContainer, each represents probability data for a runner in the market
	 */
	public List<BetfairMarketRunnerDataContainer> getAllRunnerData()
	{
		return runnerMarketData;
	}
	
	/**
	 * 
	 * @param runnerName The name of the runner that the container is requested for
	 * @return The relevant MarketRunnerDataContainer for the runner if it exists, null otherwise
	 */
	public BetfairMarketRunnerDataContainer getContainerForRunner(String runnerName)
	{
		for(BetfairMarketRunnerDataContainer runnerContainer : runnerMarketData)
		{
			if(runnerContainer.getName().equals(runnerName))
			{
				return runnerContainer;
			}
		}
		return null;
	}
}
