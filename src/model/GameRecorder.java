package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import Exceptions.CryptoException;
import betfairUtils.MarketCatalogue;
import betfairUtils.RunnerCatalog;

//Can support recording multiple games at once and multiple markets in those games

//Needs to be started with no delay, it will check all of its games to record and sleep until one starts, if any gaps between them are found then it sleeps

//List of String lists, one for each market tracked and the first for everything. Each needs the first element to give meaningful data

//Creates its own directory inside ./logs/gamelogs/
//Directory is called gamename + time
//Inner files are gamename + markets
//say if i track game odds, yellow and penalty
//1 general file per market with all stats with the name market_allstats
//1 with date,probability per market with the name 

/**
 * Class responsible for tracking games and storing the data from their play to files.
 * Only currently supports 1 game with multiple tracked markets
 * @author Craig Thomson
 *
 */
public class GameRecorder extends TimerTask
{
	/*
	 * Game IDS mapped to the market IDS (those being tracked)
	 */
	private Map<String,List<String>> gameToMarkets;

	/*
	 * Top level is just the game, so the lists inside the game list are markets
	 * List 1 is the games markets being tracked
	 * List 2 is the market data, each market being recorded has 4 collections of data.
	 */
	private List<ArrayList<ArrayList<String>>> gameData;
	
	private Map<Long, String> runnerIds;
	
	private BetFairCore betFair;
	private List<Long> gameStartTimes;
	/**
	 * 
	 * @param gameAndMarkets An array of game IDs and market IDs in the form of {gameId,marketId}, with possible repeats.
	 * @param betFairCore A reference to an initialised and logged in BetFairCore object
	 */
	public GameRecorder(BetFairCore betFairCore, List<String> gameAndMarkets)
	{
		betFair = betFairCore;
		gameToMarkets = new HashMap<String,List<String>>();
		gameData = new ArrayList<ArrayList<ArrayList<String>>>();
		generateGameToMarketMap(gameAndMarkets);
		initiliseCollections();
	}
	
	/**
	 * This method is used to find the time (in ms from 1st January 1970) that the next game to be tracked starts.
	 * @return A value in ms representing how long until the next game to be tracked starts
	 */
	public long getNextGameStartTime()
	{
		long nextGame = Long.MAX_VALUE;
		for(int i = 0; i < gameStartTimes.size(); i++)
		{
			if(nextGame > gameStartTimes.get(i))
			{
				nextGame = gameStartTimes.get(i);
			}
		}
		return nextGame;
	}
	
	//TODO make it loop through indexes to support many games
	/**
	 * Set the initial state of collections holding data, required for meaningful data outputs.
	 */
	private void initiliseCollections()
	{
		List<String> markets;
		List<MarketCatalogue> marketCatalogue;
		List<Long> gameStartTimes = new ArrayList<Long>();
		runnerIds = new HashMap<Long,String>();
		//For each individual game
		for(String gameIDKey : gameToMarkets.keySet())
		{	
			//Grab the market catalogue, which contains list of all markets for that game
			marketCatalogue = betFair.getMarketCatalogue(gameIDKey);
			
			//Get the List of marketIds we are tracking
			markets = gameToMarkets.get(gameIDKey);
			
			//For each market we track in that game
			for(int i = 0 ; i < markets.size(); i++)
			{	
				ArrayList<ArrayList<String>> marketData = new ArrayList<ArrayList<String>>();
				
				//Loop through the market catalogue
				for(int j = 0; j < marketCatalogue.size(); j++)
				{
					//If the market ids match
					if(marketCatalogue.get(j).getMarketId().equals(markets.get(i)))
					{
						gameStartTimes.add(marketCatalogue.get(j).getEvent().getOpenDate().getTime());
						addToRunnerMap(marketCatalogue.get(j).getRunners());
						
						//Index 0 of all market data lists are gamename_marketname_marketstarttime (marketstarttime should be gamestarttime)
						ArrayList<String> singleMarketData = new ArrayList<String>();
						singleMarketData.add(marketCatalogue.get(j).getEvent().getName()+"_"+marketCatalogue.get(j).getMarketName()+"_"+marketCatalogue.get(j).getEvent().getOpenDate().getTime());
						marketData.add(singleMarketData);
						//Generate Index 0 data for each runner we track that's informative...ish
						for(int k = 0; k < marketCatalogue.get(j).getRunners().size(); k++)
						{
							ArrayList<String> singleMarketData2 = new ArrayList<String>();
							singleMarketData2.add((singleMarketData.get(0))+"_"+marketCatalogue.get(j).getRunners().get(k).getRunnerName());
							marketData.add(singleMarketData2);
						}
					}
				}
				gameData.add(marketData);
			}
		}
		for(int j =0 ; j < gameData.size(); j++)
		{
			for(int i =0 ; i < gameData.get(j).size(); i++)
			{
				System.out.println(gameData.get(j).get(i));
			}
		}
	}

	private void addToRunnerMap(List<RunnerCatalog> runners)
	{
		for(int i = 0; i < runners.size(); i++)
		{
			runnerIds.put(runners.get(i).getSelectionId(),runners.get(i).getRunnerName());
		}
	}

	/**
	 * Breaks up the contents of the given list and populates the gameToMarkets Map from the contents.
	 * 
	 * @param gameAndMarkets A list of String which are in the form of {gameId,marketId}
	 */
	private void generateGameToMarketMap(List<String> gameAndMarkets)
	{
		String[] tokenHolder;
		
		for(String listEntry : gameAndMarkets)
		{
			tokenHolder = listEntry.split(",");
			
			//If no mapping exists for the current gameId
			if(gameToMarkets.get(tokenHolder[0]) == null)
			{
				//Generate the List we map to, populate it and then add the mapping
				List<String> tempList = new ArrayList<String>();
				tempList.add(tokenHolder[1]);
				gameToMarkets.put(tokenHolder[0], tempList);
			}
			//An entry for this game already exists, so we add the marketId to be tracked to its List
			else
			{
				List<String> marketList = gameToMarkets.get(tokenHolder[0]);
				marketList.add(tokenHolder[1]);
			}
		}	
	}
	
	/**
	 * Save the recorded data to a set of files.
	 */
	private void saveData()
	{
		//save all normal data and separate csvs for markets
		//for each game make a dir
		//for each market in the game make a dir
		//for each market data for the market make a file
	}

	/**
	 * Method overwritten from TimerTask. In this case it starts method calls that adds new data
	 * to the collections from the BetFair API.
	 */
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		//for each market
			//inner index are overall then runner 1,2,...,n
		
		//strictly put probability in runners and all data in the other
	}
	
	public static void main(String[] args)
	{
		BetFairCore core = new BetFairCore(false);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
		} catch (CryptoException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> marketList = new ArrayList<String>();
		marketList.add("27371349,1.117354053");
		marketList.add("27371349,1.117354055");
		marketList.add("27371349,1.117354061");
		
		GameRecorder rec = new GameRecorder(core, marketList);
	}
}
