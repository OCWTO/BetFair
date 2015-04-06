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
public class GameRecorder extends TimerTask implements Observable
{
	private DataManager manager;
	private ISimpleBetFair betFair;
	private List<Observer> observers;
	private DataIO io;
	private TestFile tester;
	private boolean finished;
	private boolean testMode;
	
	/**
	 * Create a new GameRecorder object
	 * @param options ProgramOptions that has values set for market ids, game id etc to track
	 */
	public GameRecorder(ProgramOptions options)
	{
		observers = new ArrayList<Observer>();
		betFair = options.getBetFair();
		List<BetfairMarketObject> allMarketsForGame = betFair.getMarketsForGame(options.getEventId());	
		manager = new DataManager(options, allMarketsForGame);
		io = new DataIO(manager, false);
		//Init all io so collections are initilized with data
		io.initilise(allMarketsForGame);
	}
	
	/**
	 * Create a new GameRecorder object
	 * @param testFile The testFile that the program is to be simulated on
	 */
	public GameRecorder(TestFile testFile)
	{
		//Extracted first because it's at the front of the test file, but depends on allMarketsForGame
		//to be init
		//All calls to betfair for data are replaced with calls to testFile for data.
		tester = testFile;
		ProgramOptions options = tester.getOptions();
		testMode = true;
		observers = new ArrayList<Observer>();
		List<BetfairMarketObject> allMarketsForGame = tester.getMarketList();
		manager = new DataManager(options, allMarketsForGame);
		io = new DataIO(manager, true);
		
		//Init all io so collections are initilized with data
		io.initilise(allMarketsForGame);
	}
	
	/**
	 * @return The time that the game starts at in ms from epoch
	 */
	public long getStartTime()
	{
		return manager.getStartTime();
	}

	/**
	 * @return The amount of time in ms from epoch until the game starts
	 */
	public long getStartDelayInMS()
	{
		return manager.getStartDelay();
	}

	/**
	 * BetFair only serves requests under a certain weight so if necessary this class breaks up the requests, performs then and then
	 * returns their concatenated results.
	 * @return Results from requests for all market ids
	 */
	private List<BetfairMarketData> getAllGamesMarketData()
	{
		List<String> allIds = manager.getAllMarketIds();

		//Max request weight = 200. Each request performed below is 5, so max 40 markets a request and there can be 90+ in total
		int requestWeight = 5;
		int requestLimit = 200;
		int marketSizeLimit = requestLimit/requestWeight;
		
		//Make list for smaller lists of market ids that requests will be performed for
		List<ArrayList<String>> splitQueryMarketIds = new ArrayList<ArrayList<String>>();
		
		//If the request weight is higher than what betfair allows
		if(allIds.size() * requestWeight > 200)
		{
			//While the number of ids processed is less than the total
			while(marketSizeLimit < allIds.size())
			{
				ArrayList<String> temporaryIds = new ArrayList<String>();
				
				//For every set of 40 ids. 0 to 39, 40 to 79 etc.
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

			List<BetfairMarketData> allDataContainer = new ArrayList<BetfairMarketData>();
			
			//Execute the queries
			for(int i = 0; i < splitQueryMarketIds.size(); i++)
			{
				allDataContainer.addAll(betFair.getMarketInformation(splitQueryMarketIds.get(i)));
			}
			return allDataContainer;
		}
		else
		{
			//Single request if not many markets 
			return betFair.getMarketInformation(allIds);
		}
	}
	

	@Override
	public void run()
	{
		if(testMode)
		{
			runTestMode();
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
	
	/**
	 * Run the GameRecorder in test mode
	 */
	private void runTestMode()
	{
		//Flow of exeuction is pass new data to get, get probabilities from io + closed markets, check if over then notify
		io.addData(tester.getNextData());
		List<BetfairMarketItem> mostRecentData = io.getRecentData(); 
		List<String> closedMarketList = io.getRecentlyClosedMarkets();
		checkForShutDown();
		EventList gameEvents = new EventList(mostRecentData, closedMarketList, getStartTime());	
		//Notify observers with the event list
		notifyObservers(gameEvents);
	}

	/**
	 * Run the GameRecorder in normal mode
	 */
	private void runNormalMode()
	{
		//Flow of exeuction is get new data, pass new data to get, get probabilities from io + closed markets,
		//store money bet for markets, check if over then notify
		List<BetfairMarketData> allActiveData = getAllGamesMarketData();
		io.addData(allActiveData);
		io.storeCatalogueActivity(allActiveData);	
		List<BetfairMarketItem> mostRecentData = io.getRecentData(); 
		List<String> closedMarketList = io.getRecentlyClosedMarkets();
		checkForShutDown();
		EventList gameEvents = new EventList(mostRecentData, closedMarketList, getStartTime());	
		//Notify observers with the event list
		notifyObservers(gameEvents);
	}
	
	/**
	 * If no markets are left then it shuts down
	 */
	private void checkForShutDown()
	{
		if(manager.getTrackedMarketIds().size() == 0)
			finished = true;
	}

	/**
	 * @return true if markets are still active and the GameRecorder is still working, false otherwise
	 */
	public boolean isRunning()
	{
		return finished;
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