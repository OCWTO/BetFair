package model;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


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
		io.initilise(betFair.getMarketsForGame(manager.getGameId()));
	}

	public long getStartDelayInMS()
	{
		return manager.getStartTime();
	}

	/**
	 * Create the map from Market Id to market name, required for storing data,
	 * since marketbook loses all notion of marketname and only knows of id. So
	 * we need to know what id corresponds to what name, because of our method
	 * for storing our metadata
	 */

	/**
	 * This method is used to find the time (in ms from 1st January 1970) that
	 * the next game to be tracked starts.
	 * 
	 * @return A value in ms representing how long until the next game to be
	 *         tracked starts
	 */

	/**
	 * Set the initial state of collections holding data, required for
	 * meaningful data outputs.
	 */

	
	/**
	 * Method overwritten from TimerTask. In this case it starts method calls
	 * that adds new data to the collections from the BetFair API.
	 */
	@Override
	public void run()
	{
		io.addData(betFair.getMarketInformation(manager.getMarkets()));
		
		//from here we throw our data out (markets closing, probabilities, initial values).
		
		
		//We need objects out of market name, runner name + probabiltities + timestamp for all
		//Also events such as markets shutting, can this be detected? Need to look into the data
		//I output for market shutting and market having not enough data.
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