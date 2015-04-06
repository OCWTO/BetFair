package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import betfairGSONClasses.RunnerCatalog;


/**
 * This class is used to manage data regarding BetFairs responses. The BetFair API
 * regards markets, runners and games differently at different levels of the API.
 * For example Initially a market is referred to with its english name and then deeper
 * in the API (such as calling for the list of catalogues for it) it's only referred to
 * with an ID. Request replys for runners with one request come back as english names and
 * at another only Ids. This class managers the mapping of names to Ids, the list of markets
 * we're tracking, when they start etc. It works purely with data and performs no direct
 * calls to the API.
 * @author Craig
 *
 */
public class DataManager
{
	//Sets are used here because no duplicates should exist. 
	private ProgramOptions options;
	private Map<String,Set<String>> gameToMarketMap;
	private Map<String,String> marketIdToNameMap;
	private Map<Long, String> runnerIdToNameMap;
	private long timeToStart;
	private long actualStartTime;
	private String gameId;
	
	/**
	 * Constructor for DataManager. Requests a non null ProgramOptions object and
	 * a list of BetFairMarketObjects representing all markets available for the game
	 * stored by ProgramOptions.
	 * @param options
	 * @param allMarkets
	 */
	public DataManager(ProgramOptions options, List<BetfairMarketObject> allMarkets)
	{
		this.options = options;
		generateGameToMarketMap();
		generateMarketIdToNameMap(allMarkets);
	}
	
	/**
	 * Generates a map from a Game Id to the list of all Market Ids that
	 * are initially open for betting. This is represented as a map so that
	 * if desired, there would be less change required to be able to track 
	 * multiple games at once (just add more key-value pairs).
	 */
	private void generateGameToMarketMap()
	{
		gameToMarketMap = new HashMap<String, Set<String>>();
		gameToMarketMap.put(options.getEventId(), new HashSet<String>(options.getMarketIds()));
	}
	
	/**
	 * Get the currently tracked games Id
	 * @return the game Id thats being tracked
	 */
	public String getGameId()
	{		
		//Currently only one game can be tracked at once so only 1 key in the list
		if(gameId == null)
		{
			for(String key: gameToMarketMap.keySet())
				gameId = key;
		}
		return gameId;
	}
	
	/**
	 * Stop tracking a market id so that no further requests are made for it
	 * @param marketId The id you want to stop tracking
	 */
	public void stopTrackingMarketId(String marketId)
	{
		//Grab the set of tracked ids, remove the one we dont want and replace the mapping
		Set<String> trackedMarketIds = gameToMarketMap.get(getGameId());
		trackedMarketIds.remove(marketId);
		gameToMarketMap.replace(getGameId(), trackedMarketIds);

		//Also make the change in the list of all market ids so only active ids remain
		marketIdToNameMap.remove(marketId);
	}
	
	/**
	 * Populate a map of market Ids which map to the market names
	 * @param gameMarkets
	 */
	private void generateMarketIdToNameMap(List<BetfairMarketObject> gameMarkets)
	{
		//Create the map
		marketIdToNameMap = new HashMap<String, String>();
		
		//Generate the runner map from the initial data
		generateRunnerIdToNameMap(gameMarkets);
		
		//Find the earlier start time (so we know when the game starts)
		//which is later required to schedule market tracking
		findStartTime(gameMarkets);
		
		//Populate the map
		for(BetfairMarketObject marketObj : gameMarkets)
		{
			marketIdToNameMap.put(marketObj.getMarketId(), marketObj.getName());
		}
	}
	
	/**
	 * Get all markets ids
	 * @return List of strings where each index is a market id e.g '1.23232323'
	 */
	public List<String> getAllMarketIds()
	{
		return new ArrayList<String>(marketIdToNameMap.keySet());
	}
	
	/**
	 * Get all tracked markets ids
	 * @return The list of all market Ids that the user has selected to track
	 */
	public List<String> getTrackedMarketIds()
	{
		return new ArrayList<String>(gameToMarketMap.get(getGameId()));
	}
	
	/**
	 * Find the earliest start time of all markets in gameMarkets
	 * @param gameMarkets A list of BetFairMarketObjects
	 */
	private void findStartTime(List<BetfairMarketObject> gameMarkets)
	{
		//timeToStart is the time in ms till the market starts
		timeToStart = Long.MAX_VALUE;
		
		for(BetfairMarketObject marketObj: gameMarkets)
		{
			if(marketObj.getOpenDate().getTime() < timeToStart)
				timeToStart = marketObj.getOpenDate().getTime();
		}
		
		//Actualstarttime is the time in ms from epoch the earliest market starts
		actualStartTime = timeToStart;
		
		//If already started then we just return 0
		if(timeToStart - System.currentTimeMillis() < 0)
			timeToStart = 0;
	}
	
	/**
	 * Return the time in ms from epoch that the game starts at
	 * @return time in ms that the game starts (earliest market in the game opens)
	 */
	public long getStartTime()
	{
		return actualStartTime;
	}
	
	public ProgramOptions getOptions()
	{
		return options;
	}

	/**
	 * Returns the time in ms from the request time that 
	 * @return
	 */
	public long getStartDelay()
	{
		System.out.println("Starting in: " + (timeToStart - System.currentTimeMillis()) + " ms" + "(" + ((timeToStart - System.currentTimeMillis())/1000.0/60) + " mins)");
		
		if(timeToStart == 0)
		{
			return timeToStart;
		}
		else
		{
			//This is because there could be a delay between timeToStart init and requested so the time is inaccurate
			//So purely timeToStart - ms time could actually be -ve
			if(timeToStart - System.currentTimeMillis() < 0)
				return 0;
			else
				return timeToStart - System.currentTimeMillis();
		}
	}

	/**
	 * Creates map of runner Ids to their english names
	 */
	private void generateRunnerIdToNameMap(List<BetfairMarketObject> gameMarkets)
	{
		runnerIdToNameMap = new HashMap<Long, String>();
		
		for(BetfairMarketObject gameMarket: gameMarkets)
		{
			for(RunnerCatalog runner: gameMarket.getRunners())
			{
				runnerIdToNameMap.put(runner.getSelectionId(), runner.getRunnerName());
			}
		}
	}
	
	/**
	 * 
	 * @param runnerId A runners select id
	 * @return The name of the runner with the given id
	 */
	public String getRunnerName(Long runnerId)
	{
		return runnerIdToNameMap.get(runnerId);
	}

	/**
	 * 
	 * @param marketId A markets id
	 * @return The name of the market with the given id
	 */
	public String getMarketName(String marketId)
	{
		return marketIdToNameMap.get(marketId);
	}
}