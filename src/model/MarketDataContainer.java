package model;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MarketDataContainer
{
	private String gameName;
	private String marketName;
	private String marketId;
	private LocalTime marketOpenDate;
	private List<MarketRunnerDataContainer> runnerMarketData;
	private List<String> generalMarketData;
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss");
	
	public MarketDataContainer(String gamesName, String marketsName, String marketsId, Date marketsOpenDate)
	{
		gameName = gamesName;
		marketName = marketsName;
		marketId = marketsId;
		
		Instant instant = Instant.ofEpochMilli(marketsOpenDate.getTime());
		marketOpenDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
		
		runnerMarketData = new ArrayList<MarketRunnerDataContainer>();
		generalMarketData = new ArrayList<String>();

	}
	
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
		runnerMarketData.add(new MarketRunnerDataContainer(runnerName));
	}
	
	public List<MarketRunnerDataContainer> getAllRunnerData()
	{
		return runnerMarketData;
	}
	
	public MarketRunnerDataContainer getContainerForRunner(String runnerName)
	{
		for(MarketRunnerDataContainer runnerContainer : runnerMarketData)
		{
			if(runnerContainer.getName().equals(runnerName))
			{
				return runnerContainer;
			}
		}
		return null;
	}
}
