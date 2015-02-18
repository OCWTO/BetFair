package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Exceptions.CryptoException;
import betfairUtils.MarketBook;
import betfairUtils.MarketCatalogue;
import betfairUtils.Runner;
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
	
	//Due to betfair api inconsistancies in data type this is string to string, not long to string
	private Map<String,String> marketIdToName;
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
		generateMarketIdToNameMap();
		initiliseCollections();
	}
	
	/**
	 * Create the map from Market Id to market name, required for storing data, since
	 * marketbook loses all notion of marketname and only knows of id. So we need to know
	 * what id corresponds to what name, because of our method for storing our metadata
	 */
	private void generateMarketIdToNameMap()
	{
		List<MarketCatalogue> catalogue;// = betFair.getMarketCatalogue()
		marketIdToName = new HashMap<String,String>();
		//For each game we track
		for(String gameIDKey : gameToMarkets.keySet())
		{
			List<String> marketIds = gameToMarkets.get(gameIDKey);
			catalogue = betFair.getMarketCatalogue(gameIDKey);
			
			for(int i = 0; i < marketIds.size(); i++)
			{
				for(MarketCatalogue catalogueIndex: catalogue)
				{
					//If we find a match of ids
					if(catalogueIndex.getMarketId().equals(marketIds.get(i)))
					{
						marketIdToName.put(marketIds.get(i), catalogueIndex.getMarketName());
						break;
					}
				}
			}
		}
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
			System.out.println("iter");
			for(int i =0 ; i < gameData.get(j).size(); i++)
			{
				System.out.println("X " + gameData.get(j).get(i));
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
		List<String> trackedMarkets;
		List<MarketBook> marketData;
		List<ArrayList<String>> activeIndex;
		String dataIndex;
		String[] dataIndexTokens;
		String keyword;
		//String secondKeyword;
		for(String gameIDKey : gameToMarkets.keySet())
		{
			//Tracked market list
			trackedMarkets = gameToMarkets.get(gameIDKey);
			
			//Market book list for tracked markets
			marketData = betFair.getMarketBook(trackedMarkets);
			
			//Match gameData list to marketData Item
			System.out.println(gameData.size());
			for(int i = 0; i < gameData.size(); i++)
			{
				//Store array we're currently looking for
				activeIndex = gameData.get(i);
				
				//Get 1st array item (metadata with gamename, marketname, time etc)
				dataIndex = gameData.get(i).get(0).get(0);
				dataIndexTokens = dataIndex.split("_");
				
				//Grab the market name from the stored data
				keyword = dataIndexTokens[1];
				//Find the marketids name (if we track it)
				for(int j = 0; j < marketData.size(); j++)
				{
					//Get the markets name
					String secondKeyword = marketIdToName.get(marketData.get(j).getMarketId());
					
					//Got match and we know the list it maps to so we get data and add to it
					if(secondKeyword != null)
					{
						List<MarketBook> book = betFair.getMarketBook(marketData.get(j).getMarketId());
						List<Runner> runners = book.get(0).getRunners();
						
						gatherData(runners, activeIndex);
						//from here we have a list of lists? if so we add all to 0, runner1 to 1...
						//gameData.get(i) is the match, so 0,0 is all 
						//get 0 is market1
						//get 1 is market2
						//get 0 0 is market 1 all
						//get 0 1 is market 1 runner 1
						//		dont forget the last index means shit here
						break;
					}
					//Match up the secondKeyword to a token 
					
					//if we get a match then we grab data and add to the array with the match
				}	
			}	
		}
		//Identify the gameData list that lines up to the marketData list index
	}
	
	private void gatherData(List<Runner> runners, List<ArrayList<String>> activeIndex)
	{
		//If split on activeindex = 2 then its all data 
		
		//otherwise its a runners so we only look for that
		//String[] tokens = activeIndex.get(0).split("_");
		System.out.println("size " + activeIndex.size());
		
		//For each inner list we have
		for(int i = 0 ; i < activeIndex.size(); i++)
		{
			//Tokenize index 0 (metadata entry)
			String[] tokens = activeIndex.get(i).get(0).split("_");
			
			//3 Tokens are in the field we tokenize iff its the list for recording ALL game data
			if(tokens.length == 3)
			{
				//recordalldata
			}
			//4 Tokens are for a single runner so we only care about certain data and get probability instead of all data
			else
			{
				//match our last token to a runner name and only care about their shit.
			}
		}
	}

	private List<String> locateDataArray(MarketBook marketBook)
	{
		List<String> index;
		String[] indexTokens;
		String marketToken = marketBook.getMarketId();
		
		
		//For each market we track
		for(int i = 0; i < gameData.size(); i++)
		{
			index = gameData.get(i).get(0);
			//indexTokens = index.split("_");
		}
		
		
		
		//for each market we are tracking
		for(int i = 0; i < gameData.get(0).size(); i++)
		{
			//get the list of lists for a market (so information for that market)
			index = gameData.get(0).get(i);
			
			//if index 0 (metadata line, which has the id)
			indexTokens = index.get(0).split("_");
			for(String token : indexTokens)
			{
				//this list has the same marketid as the marketbook passed in
				if(token.equals(marketToken))
				{
					return index;
				}
			}
		}
		//assertion here that it never gets here
		return null;
	}

	public static void main(String[] args)
	{
		BetFairCore core = new BetFairCore(false);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
		} 
		catch (CryptoException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> marketList = new ArrayList<String>();
		marketList.add("27361322,1.117192952");
		marketList.add("27361322,1.117352806");
		marketList.add("27361322,1.117192958");
		GameRecorder rec = new GameRecorder(core, marketList);
		Timer time = new Timer();
		time.schedule(rec,5000);
		/////////
		
//		BetFairCore core = new BetFairCore(false);
//		try
//		{
//			core.login("0ocwto0", "2014Project", "project");
//		} 
//		catch (CryptoException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<String> marketList = new ArrayList<String>();
//		marketList.add("1.117354053");
//		marketList.add("1.117354055");
//		marketList.add("1.117354061");
//		List<MarketBook> xy = core.getMarketBook(marketList);
//		System.out.println(xy.size());
//		for(int i = 0; i < xy.size(); i++)
//		{
//			System.out.println(xy.get(i).getMarketId());	//comes out same order it goes in
//			System.out.println(xy.get(i).toString());
//		}
	}
}
