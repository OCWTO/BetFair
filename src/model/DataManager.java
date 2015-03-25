package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import betFairGSONClasses.RunnerCatalog;
//TODO consider method here to remove markets from both allMarketIds and 
public class DataManager
{
	private ProgramOptions options;
	private Map<String,List<String>> gameToMarketList;
	private Map<String,String> marketIdToName;
	private Map<Long, String> runnerIdToName;
	private List<String> allMarketIds;
	private List<String> trackedMarketIds;
	private long timeToStart;
	private long actualStartTime;
	private String gameId;
	
	public DataManager(ProgramOptions options, List<BetFairMarketObject> allMarkets)
	{
		this.options = options;
		generateGameToMarketMap();
		generateMarketIdToNameMap(allMarkets);
	}
	
	private void generateGameToMarketMap()
	{
		gameToMarketList = new HashMap<String, List<String>>();
		gameToMarketList.put(options.getEventId(), options.getMarketIds());
	}
	
	public String getGameId()
	{		
		if(gameId == null)
		{
			for(String key: gameToMarketList.keySet())
				gameId = key;
		}
		return gameId;
	}
	
	public List<String> getMarkets()
	{
		if(trackedMarketIds == null)
		{
			trackedMarketIds = new ArrayList<String>();
			for(String key: gameToMarketList.keySet())
			{
				trackedMarketIds.addAll(gameToMarketList.get(key));
			}
		}
		return trackedMarketIds;
	}
	
	public int numberOfActiveMarkets()
	{
		return trackedMarketIds.size();
	}
	
	public void stopTrackingMarket(String marketId)
	{
		trackedMarketIds.remove(marketId);
		
		//This is done to stop requests for data of non existing markets and avoiding exceptions
		allMarketIds.remove(marketId);
		
		//Take old markets id from the list of tracked ids
		gameToMarketList.replace(getGameId(), trackedMarketIds);
	}
	
	private void generateMarketIdToNameMap(List<BetFairMarketObject> gameMarkets)
	{
		marketIdToName = new HashMap<String, String>();
		
		generateRunnerIdToNameMap(gameMarkets);
		findStartTime(gameMarkets);
		
		for(BetFairMarketObject marketObj : gameMarkets)
		{
			marketIdToName.put(marketObj.getMarketId(), marketObj.getName());
		}
		System.out.println("generated map");
	}
	
	
	public List<String> getListOfAllMarketIds()
	{
		if(allMarketIds == null)
		{
			allMarketIds = new ArrayList<String>();
			
			for(String id : marketIdToName.keySet())
			{
				allMarketIds.add(id);
			}
		}
			return allMarketIds;
	}
	
	private void findStartTime(List<BetFairMarketObject> gameMarkets)
	{
		timeToStart = Long.MAX_VALUE;
		
		for(BetFairMarketObject marketObj: gameMarkets)
		{
			if(marketObj.getOpenDate().getTime() < timeToStart)
				timeToStart = marketObj.getOpenDate().getTime();
		}
		
		actualStartTime = timeToStart;
		
		//Get difference between start time and current time
		timeToStart = timeToStart - System.currentTimeMillis();
		
		//If already started then we just return 0
		if(timeToStart < 0)
			timeToStart = 0;
	}
	
	public long getStartTime()
	{
		return actualStartTime;
	}
	
	public long getStartDelay()
	{
		System.out.println("Starting in: " + timeToStart + " ms" + "(" + timeToStart/1000.0/60 + " mins)");
		return timeToStart;
	}
	
	public String getRunnerName(Long runnerId)
	{
		return runnerIdToName.get(runnerId);
	}

	private void generateRunnerIdToNameMap(List<BetFairMarketObject> gameMarkets)
	{
		runnerIdToName = new HashMap<Long, String>();
		
		for(BetFairMarketObject gameMarket: gameMarkets)
		{
			for(RunnerCatalog runner: gameMarket.getRunners())
			{
				runnerIdToName.put(runner.getSelectionId(), runner.getRunnerName());
			}
		}
	}

	public String getMarketName(String marketId)
	{
		return marketIdToName.get(marketId);
	}

	public void setMarkets(List<String> currentMarketList)
	{
		String gameIdKey = null;

		for(String key: gameToMarketList.keySet())
		{
			gameIdKey = key;
		}
		
		gameToMarketList.remove(gameIdKey);
		gameToMarketList.put(gameIdKey, currentMarketList);
	}
}