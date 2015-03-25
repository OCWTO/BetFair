package model;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;


/**
 * Class responsible for tracking games and storing the data from their play to
 * files. Only currently supports 1 game with tracked markets. For each tracked
 * market it will create a number of files. One containing all time stamped raw
 * data from all runners, and other files, for each runner. These contain time
 * stamps with comma separated probability, which is calculated from raw data.
 * 
 * @author Craig Thomson
 */
// TODO extend this so it stores or throws or both depending on whats passed

// TODO
// What does this need
// market id to name map
// game start times
// takes in program options only, extracts everything from it
// creates programsettings object
// contains market id to names map
// create class for io
// create class to manage data
// DataManager



public class GameRecorder extends TimerTask implements Observable
{
	// Game IDS mapped to the market IDS (those being tracked)
	// private Map<String, List<String>> gameToMarkets;
	// TODO add code to store the new methods (moneymatched and jsonstrings at
	// the end).
	/*
	 * Top level is just the game, so the lists inside the game list are markets
	 * List 1 is the games markets being tracked List 2 is the market data, each
	 * market being recorded has 4 collections of data.
	 */
	private DataManager manager;
	private ISimpleBetFair betFair;
	private List<Observer> observers;
	private DataIO io;
	private List<String> marketList;
	private boolean finished;
	/**
	 * @param gameAndMarkets
	 *            An array of game IDs and market IDs in the form of
	 *            {gameId,marketId}, with possible repeats.
	 * @param betFairCore
	 *            A reference to an initialised and logged in BetFairCore object
	 */

	// receives programoptions with market names, event ids etc
	// create new datamanger object with the event ids and game id
	// that does all the initial requests

	public GameRecorder(ProgramOptions options)
	{
		finished = false;
		observers = new ArrayList<Observer>();
		betFair = options.getBetFair();
		manager = new DataManager(options, betFair.getMarketsForGame(options.getEventId()));
		io = new DataIO(manager);
		marketList = new ArrayList<String>(manager.getMarkets());
		io.initilise(betFair.getMarketsForGame(manager.getGameId()));
	}
	
	public long getStartTime()
	{
		return manager.getStartTime();
	}

	public long getStartDelayInMS()
	{
		return manager.getStartDelay();
	}

	/**
	 * BetFair only serves requests under a certain weight so if necessary this class breaks up the requests, performs then and then
	 * returns their concatenated results.
	 * @return
	 */
	private List<BetFairMarketData> getAllGamesMarketData()
	{
		List<String> allIds = manager.getListOfAllMarketIds();
		int requestWeight = 5;
		int requestLimit = 200;
		int singleRequestMaxSize = requestLimit/requestWeight;
		
		List<ArrayList<String>> splitQueryMarketIds = new ArrayList<ArrayList<String>>();
		//If the request weight is higher than what betfair allows
		if(allIds.size() * requestWeight > 200)
		{
			//We create a list to contain the split ids that we use for the query
			
			int marketSizeLimit = requestLimit/requestWeight;
			System.out.println(marketSizeLimit);
			//While the number of ids processed is less than the total
			while(marketSizeLimit < allIds.size())
			{
				ArrayList<String> temporaryIds = new ArrayList<String>();
				
				for(int i = marketSizeLimit-40; i < marketSizeLimit; i++)
				{
					temporaryIds.add(allIds.get(i));
				}
				splitQueryMarketIds.add(temporaryIds);
				marketSizeLimit+=40;
			}
			marketSizeLimit-=40;
			
			ArrayList<String> temporaryIds = new ArrayList<String>();
			
			//Get the remainder thats %40
			for(int i = marketSizeLimit; i < allIds.size(); i++)
			{
				temporaryIds.add(allIds.get(i));
			}
			splitQueryMarketIds.add(temporaryIds);

			List<BetFairMarketData> allDataContainer = new ArrayList<BetFairMarketData>();
			
			//Execute the queries
			for(int i = 0; i < splitQueryMarketIds.size(); i++)
			{
				allDataContainer.addAll(betFair.getMarketInformation(splitQueryMarketIds.get(i)));
			}
			return allDataContainer;
		}
		else
		{
			return betFair.getMarketInformation(allIds);
		}
	}
	
	@Override
	public void run()
	{
		io.addData(betFair.getMarketInformation(manager.getMarkets()));
		System.out.println("added all new data");
		io.storeCatalogueActivity(getAllGamesMarketData());
		System.out.println("storing activity");
		//Get the relevant information from our utilised objects
		List<BetFairMarketItem> mostRecentData = io.getRecentData(); 
		System.out.println("getting data");
		List<String> closedMarketList = checkForClosedMarkets(mostRecentData);
		
		EventList gameEvents = new EventList(mostRecentData, closedMarketList, getStartTime());	
		notifyObservers(gameEvents);
		System.out.println("finished loop");
		
		
		if(finished)
		{
			System.out.println("canelling");
			cancel();
		}
	}

	public boolean isRunning()
	{
		return finished;
	}

	private List<String> checkForClosedMarkets(List<BetFairMarketItem> mostRecentData)
	{
		//If we received less data for markets than expected
		System.out.println(mostRecentData.size() + " MOST RECET");
		System.out.println(marketList.size() + " MARKETLIST");
		
		//If there's a mismatch between the number of markets we got data for and what we expect
		if(marketList.size() != mostRecentData.size())
		{
			//If we received nothing then that means the game is over
			if(mostRecentData.size() == 0)
			{
				System.out.println("All markets finished so shutting down.@@@@@");
				
				//Cancel timertask so data requests stop
				//System.out.println(this.cancel());
				finished = true;
			}
				//Need to throw custom stuff up?
				//Resolve the list of market ids to their names
				List<String> marketNames = new ArrayList<String>();
				for(String marketId: marketList)
				{
					marketNames.add(manager.getMarketName(marketId));
				}
				
				//Convert List of BetFairMarketItems to a list of their marketNames
				List<String> recentMarketIds = mostRecentData.stream().map(BetFairMarketItem::getMarketName).collect(Collectors.toList());
				
				//Remove all common elements, thus whats left is market names we received no data for
				marketNames.removeAll(recentMarketIds);
	
				//If there's markets we received no data for
				System.out.println("markets we got no data for " + marketNames.size());
				if(marketNames.size() != 0)
				{
					return marketNames;
				}
			
		}
		return null;
	}

	private List<BetFairMarketData> getAllGameMarketInformation()
	{
		List<BetFairMarketData> allGameMarkets = new ArrayList<BetFairMarketData>();
		
		//Get lists of ids to request due to data request limitations
		//List<List<String>> allGameMarketIds = getMarketIdList(manager.getGameId());
		
//		for(int i = 0; i < allGameMarketIds.size(); i++)
//		{
//			allGameMarkets.addAll(betFair.getMarketInformation(allGameMarketIds.get(i)));
//			//betFair.g
//		}
		
		return allGameMarkets;
	}

	@Override
	public void addObserver(Observer obs)
	{
		observers.add(obs);
	}

	@Override
	public void removeObserver(Observer obs)
	{
		observers.remove(obs);
	}

	@Override
	public void notifyObservers(Object event)
	{
		for(Observer obs: observers)
			obs.update(event);
	}
}