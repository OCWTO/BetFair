package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import betFairGSONClasses.RunnerCatalog;

public class DataManager
{
	private ProgramOptions options;
	private Map<String,List<String>> gameToMarketList;
	private Map<String,String> marketIdToName;
	private Map<Long, String> runnerIdToName;
	private long timeToStart;
	private long actualStartTime;
	
	public DataManager(ProgramOptions options)
	{
		this.options = options;
		generateGameToMarketMap();
		generateMarketIdToNameMap();
	}
	
	private void generateGameToMarketMap()
	{
		gameToMarketList = new HashMap<String, List<String>>();
		gameToMarketList.put(options.getEventId(), options.getMarketIds());
	}
	
	public String getGameId()
	{		
		for(String key: gameToMarketList.keySet())
			return key;
		return null;
	}
	
	public List<String> getMarkets()
	{
		for(String key: gameToMarketList.keySet())
		{
			return gameToMarketList.get(key);
		}
		return null;
	}
	
	private void generateMarketIdToNameMap()
	{
		marketIdToName = new HashMap<String, String>();
		List<String> gameMarketIds = options.getMarketIds();
		List<BetFairMarketObject> gameMarkets = options.getBetFair().getMarketsForGame(options.getEventId());
		
		generateRunnerIdToNameMap(gameMarkets);
		findStartTime(gameMarkets);
		
		for(BetFairMarketObject marketObj : gameMarkets)
		{
			for(String marketId: gameMarketIds)
			{
				if(marketId.equals(marketObj.getId()))
				{
					marketIdToName.put(marketId, marketObj.getName());
					break;
				}
			}
		}
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