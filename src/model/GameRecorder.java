package model;

import java.io.File;
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
	private boolean testMode;
	
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
	
	public GameRecorder(ProgramOptions options, File testFile)
	{
		finished = false;
		testMode = true;
		observers = new ArrayList<Observer>();
		betFair = options.getBetFair();
		//here we just swap that call for 1st element from the storage
		manager = new DataManager(options, betFair.getMarketsForGame(options.getEventId()));
		System.out.println(options.getEventId() + " OPTS");
		io = new DataIO(manager);
		marketList = new ArrayList<String>(manager.getMarkets());
		
		//we swap this data out too
		System.out.println(manager.getGameId() + " MAN");
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
		int marketSizeLimit = requestLimit/requestWeight;
		
		List<ArrayList<String>> splitQueryMarketIds = new ArrayList<ArrayList<String>>();
		//If the request weight is higher than what betfair allows
		if(allIds.size() * requestWeight > 200)
		{
			//We create a list to contain the split ids that we use for the query
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
	
//	TODO if test mode then lets just not save the data
	@Override
	public void run()
	{
		if(testMode)
		{
			System.out.println("test mode on");
			io.addData(betFair.getMarketInformation(manager.getMarkets()));
			System.out.println("added all new data");
			//io.storeCatalogueActivity(getAllGamesMarketData());		dont want this
			System.out.println("storing activity");
			//Get the relevant information from our utilised objects
			List<BetFairMarketItem> mostRecentData = io.getRecentData(); 
			System.out.println("getting data");
			List<String> closedMarketList = checkForClosedMarkets(mostRecentData);
			
			EventList gameEvents = new EventList(mostRecentData, closedMarketList, getStartTime());	
			notifyObservers(gameEvents);
			System.out.println("finished loop");
		}
		else
		{
			runNormalMode();
		}
		
		if(finished)
		{
			System.out.println("cancelling");
			cancel();
		}
	}

	private void runNormalMode()
	{
		//Add new data to the IO class, that's just been taken from API
		io.addData(betFair.getMarketInformation(manager.getMarkets()));
		System.out.println("added all new data");
		//Store catalogue activity for purely history purposes
		io.storeCatalogueActivity(getAllGamesMarketData());			//????????
		System.out.println("storing activity");
		/*Get the relevant new data from the IO class, essentially a 
		 *parsed version of what we passed in earlier
		 */
		List<BetFairMarketItem> mostRecentData = io.getRecentData(); 
		System.out.println("getting data");
		
		//Check to see if any markets have closed this iteration
		List<String> closedMarketList = checkForClosedMarkets(mostRecentData);
		
		EventList gameEvents = new EventList(mostRecentData, closedMarketList, getStartTime());	
		notifyObservers(gameEvents);
		System.out.println("finished loop");
	}
	
	public boolean isRunning()
	{
		return finished;
	}

	/**
	 * This method checks the received data to see if any markets have been closed.
	 * @param mostRecentData
	 * @return A list of the market names that have been closed this iteration
	 */
	private List<String> checkForClosedMarkets(List<BetFairMarketItem> mostRecentData)
	{
		System.out.println(mostRecentData.size() + " RECEIVED SIZE");
		System.out.println(marketList.size() + " MARKETLIST SIZE");
		
		//If there's a mismatch between the number of markets we got data for and what we expect
		if(marketList.size() != mostRecentData.size())
		{
			//If we received nothing then that means the game is over
			if(mostRecentData.size() == 0)
			{
				System.out.println("All markets finished so shutting down.");
				finished = true;
			}
				//Resolve our list of market ids to their names.
				List<String> marketNames = new ArrayList<String>();
				for(String marketId: marketList)
				{
					marketNames.add(manager.getMarketName(marketId));
				}
				
				//Convert the received List of BetFairMarketItems to a list of their marketNames
				List<String> recentMarketIds = mostRecentData.stream().map(BetFairMarketItem::getMarketName).collect(Collectors.toList());
				
				//Remove all common elements, thus what's left is market names we received no data for
				marketNames.removeAll(recentMarketIds);
				
				//Stop tracking those we received no data for.
				//I think we need ids to remove from so either store indexes or convert
				
				for(String x : marketNames)
				{
					System.out.println("trying a removal");
					System.out.println("x is " + x + ". " + manager.getMarketIdForName(x));
					if(marketList.indexOf(manager.getMarketIdForName(x)) >= 0)
					{
						System.out.println(marketList.remove(marketList.indexOf(manager.getMarketIdForName(x))));
						
					}
				}
				//marketList.removeAll(marketNames);	//TODO problems might come from here
				//If there's markets we received no data for
				System.out.println("markets we got no data for " + marketNames.size());

				return marketNames;
		}
		return new ArrayList<String>();
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