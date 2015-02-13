package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import betfairUtils.MarketCatalogue;

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
 * @author Craig Thomson
 *
 */
public class GameRecorder extends TimerTask
{
	//Need to store markets to ids, going to have a map of gameid to markets, where markets is a list we dont directly store references to
	private Map<String,List<String>> gameToMarkets;
	
	// A list of lists, which contain lists of data for individual market data
	private List<List<List<String>>> gameData;
	//Gets a list of the game ids and their market ids to track.
	
	private BetFairCore betFair;
	
	/**
	 * 
	 * @param gameAndMarkets An array of game IDs and market IDs in the form of {gameId,marketId}
	 */
	public GameRecorder(BetFairCore betFairCore, List<String> gameAndMarkets)
	{
		betFair = betFairCore;
		gameToMarkets = new HashMap<String,List<String>>();
		generateGameToMarketMap(gameAndMarkets);
		initiliseCollections();
	}

	/**
	 * Set the initial state of collections holding data, required for meaningful data outputs.
	 */
	private void initiliseCollections()
	{
		//For each individual game
		for(String gameIDKey : gameToMarkets.keySet())
		{
			//Grab the market catalogue
			List<MarketCatalogue> marketCatalogue = betFair.getMarketCatalogue(gameIDKey);
			List<String> markets = gameToMarkets.get(gameIDKey);
			
			//For each market we track in that game
			for(int i = 0 ; i < markets.size(); i++)
			{
				//get its name from marketCatalogue
				//need to get open date so this knows when to start
				//add runner names to certain ones, list
				
				//generate initial entries
				//need to get the markets name and append it with gamename
			}
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
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
}
