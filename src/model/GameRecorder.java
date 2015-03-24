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
		observers = new ArrayList<Observer>();
		manager = new DataManager(options);
		betFair = options.getBetFair();
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

	
	@Override
	public void run()
	{
		io.addData(betFair.getMarketInformation(manager.getMarkets()));
		
		//Get the relevant information from our utilised objects
		List<BetFairMarketItem> mostRecentData = io.getRecentData(); 
		List<String> closedMarketList = checkForClosedMarkets(mostRecentData);
		
		EventList gameEvents = new EventList(mostRecentData, closedMarketList, getStartTime());	
		notifyObservers(gameEvents);
	}


	private List<String> checkForClosedMarkets(List<BetFairMarketItem> mostRecentData)
	{
		//If we received less data for markets than expected
		System.out.println(mostRecentData.size() + " MOST RECET");
		System.out.println(marketList.size() + " MARKETLIST");
		if(marketList.size() != mostRecentData.size())
		{
			if(mostRecentData.size() == 0)
			{
				System.out.println("All markets finished so shutting down.@@@@@");
				this.cancel();
				//Need to throw custom stuff up?
			}
			
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