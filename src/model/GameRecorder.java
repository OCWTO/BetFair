package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import enums.BetFairMarketStatus;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.PriceSize;
import betFairGSONClasses.Runner;
import betFairGSONClasses.RunnerCatalog;

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
 * Class responsible for tracking games and storing the data from their play to
 * files. Only currently supports 1 game with tracked markets. For each tracked
 * market it will create a number of files. One containing all time stamped raw
 * data from all runners, and other files, for each runner. These contain time
 * stamps with comma separated probability, which is calculated from raw data.
 * 
 * @author Craig Thomson
 */
public class GameRecorder extends TimerTask
{
	// Game IDS mapped to the market IDS (those being tracked)
	private Map<String, List<String>> gameToMarkets;

	/*
	 * Top level is just the game, so the lists inside the game list are markets
	 * List 1 is the games markets being tracked List 2 is the market data, each
	 * market being recorded has 4 collections of data.
	 */
	private List<ArrayList<ArrayList<String>>> gameData;
	private Map<Long, String> runnerIds;
	private BetFairCore betFair;
	private List<Long> gameStartTimes;
	private Map<String, String> marketIdToName;
	private LocalTime timer;
	private int counter;
	private long startDelay;
	private File baseDirectory;
	private String separator = File.separator;

	/**
	 * @param gameAndMarkets
	 *            An array of game IDs and market IDs in the form of
	 *            {gameId,marketId}, with possible repeats.
	 * @param betFairCore
	 *            A reference to an initialised and logged in BetFairCore object
	 */
	public GameRecorder(BetFairCore betFairCore, List<String> gameAndMarkets)
	{
		counter = 1;
		betFair = betFairCore;
		gameToMarkets = new HashMap<String, List<String>>();
		gameData = new ArrayList<ArrayList<ArrayList<String>>>();
		generateGameToMarketMap(gameAndMarkets);
		generateMarketIdToNameMap();
		initiliseCollections();
		setStartDelay(gameAndMarkets);
	}

	private void setStartDelay(List<String> gameAndMarkets)
	{
		String[] gameID = gameAndMarkets.get(0).split(",");

		List<MarketCatalogue> catalogueItem = betFair
				.getMarketCatalogue(gameID[0]);
		for (int i = 0; i < catalogueItem.size(); i++)
		{
			if (catalogueItem.get(i).getMarketId().equals(gameID[1]))
			{
				startDelay = catalogueItem.get(i).getEvent().getOpenDate()
						.getTime();
				break;
			}
		}
	}

	public long getStartDelayInMS()
	{
		// If in progress (already started)
		if ((startDelay - System.currentTimeMillis()) < 0)
		{
			System.out.println("Starting now.");
			return 0;
		}
		System.out.println("Waiting for: "
				+ (startDelay - System.currentTimeMillis()) + "MS");
		return startDelay - System.currentTimeMillis();
	}

	/**
	 * Create the map from Market Id to market name, required for storing data,
	 * since marketbook loses all notion of marketname and only knows of id. So
	 * we need to know what id corresponds to what name, because of our method
	 * for storing our metadata
	 */
	private void generateMarketIdToNameMap()
	{
		List<MarketCatalogue> catalogue;
		
		marketIdToName = new HashMap<String, String>();
		// For each game we track
		for (String gameIDKey : gameToMarkets.keySet())
		{
			List<String> marketIds = gameToMarkets.get(gameIDKey);
			catalogue = betFair.getMarketCatalogue(gameIDKey);

			for (int i = 0; i < marketIds.size(); i++)
			{
				for (MarketCatalogue catalogueIndex : catalogue)
				{
					// If we find a match of ids
					if (catalogueIndex.getMarketId().equals(marketIds.get(i)))
					{
						marketIdToName.put(marketIds.get(i),
								catalogueIndex.getMarketName());
						break;
					}
				}
			}
		}
	}

	/**
	 * This method is used to find the time (in ms from 1st January 1970) that
	 * the next game to be tracked starts.
	 * 
	 * @return A value in ms representing how long until the next game to be
	 *         tracked starts
	 */
	public long getNextGameStartTime()
	{
		long nextGame = Long.MAX_VALUE;
		for (int i = 0; i < gameStartTimes.size(); i++)
		{
			if (nextGame > gameStartTimes.get(i))
			{
				nextGame = gameStartTimes.get(i);
			}
		}
		return nextGame;
	}

	/**
	 * Set the initial state of collections holding data, required for
	 * meaningful data outputs.
	 */
	private void initiliseCollections()
	{
		List<String> markets;
		List<MarketCatalogue> marketCatalogue;
		List<Long> gameStartTimes = new ArrayList<Long>();
		runnerIds = new HashMap<Long, String>();
		// For each individual game
		for (String gameIDKey : gameToMarkets.keySet())
		{
			// Grab the market catalogue, which contains list of all markets for
			// that game
			marketCatalogue = betFair.getMarketCatalogue(gameIDKey);

			// Get the List of marketIds we are tracking
			markets = gameToMarkets.get(gameIDKey);

			// For each market we track in that game
			for (int i = 0; i < markets.size(); i++)
			{
				ArrayList<ArrayList<String>> marketData = new ArrayList<ArrayList<String>>();

				// Loop through the market catalogue
				for (int j = 0; j < marketCatalogue.size(); j++)
				{
					// If the market ids match
					if (marketCatalogue.get(j).getMarketId()
							.equals(markets.get(i)))
					{
						gameStartTimes.add(marketCatalogue.get(j).getEvent()
								.getOpenDate().getTime());
						addToRunnerMap(marketCatalogue.get(j).getRunners());

						// Index 0 of all market data lists are
						// gamename_marketname_marketstarttime (marketstarttime
						// should be gamestarttime)
						ArrayList<String> singleMarketData = new ArrayList<String>();
						singleMarketData.add(marketCatalogue.get(j).getEvent()
								.getName()
								+ "_"
								+ marketCatalogue.get(j).getMarketName()
								+ "_"
								+ marketCatalogue.get(j).getEvent()
										.getOpenDate().getTime());
						marketData.add(singleMarketData);
						// Generate Index 0 data for each runner we track that's
						// informative...ish
						for (int k = 0; k < marketCatalogue.get(j).getRunners()
								.size(); k++)
						{
							ArrayList<String> singleMarketData2 = new ArrayList<String>();
							singleMarketData2.add((singleMarketData.get(0))
									+ "_"
									+ marketCatalogue.get(j).getRunners()
											.get(k).getRunnerName()); // todo
																		// add
																		// /n
																		// here
																		// and
																		// look
																		// at
																		// token
																		// size
																		// changes
							marketData.add(singleMarketData2);
						}
					}
				}
				gameData.add(marketData);
			}
		}
		
		
	}

	/**
	 * 
	 * @param runners
	 */
	private void addToRunnerMap(List<RunnerCatalog> runners)
	{
		for (int i = 0; i < runners.size(); i++)
		{
			runnerIds.put(runners.get(i).getSelectionId(), runners.get(i)
					.getRunnerName());
		}
	}

	/**
	 * Breaks up the contents of the given list and populates the gameToMarkets
	 * Map from the contents.
	 * 
	 * @param gameAndMarkets
	 *            A list of String which are in the form of {gameId,marketId}
	 */
	private void generateGameToMarketMap(List<String> gameAndMarkets)
	{
		String[] tokenHolder;

		for (String listEntry : gameAndMarkets)
		{
			tokenHolder = listEntry.split(",");

			// If no mapping exists for the current gameId
			if (gameToMarkets.get(tokenHolder[0]) == null)
			{
				// Generate the List we map to, populate it and then add the
				// mapping
				List<String> tempList = new ArrayList<String>();
				tempList.add(tokenHolder[1]);
				gameToMarkets.put(tokenHolder[0], tempList);
			}
			// An entry for this game already exists, so we add the marketId to
			// be tracked to its List
			else
			{
				List<String> marketList = gameToMarkets.get(tokenHolder[0]);
				marketList.add(tokenHolder[1]);
			}
		}
	}

	private void makeBaseDirectory()
	{
		if (baseDirectory != null)
			return;

		String currentDir = System.getProperty("user.dir");
		String destination = separator + "logs" + separator + "gamelogs" + separator;
		String metaDataTokens[] = gameData.get(0).get(0).get(0).split("_");
		baseDirectory = new File(currentDir + destination + metaDataTokens[0]);
		baseDirectory.mkdir();
	}

	/**
	 * Save the recorded data to a set of files.
	 */
	private void saveData(ArrayList<ArrayList<String>> closedMarketData)
	{
		makeBaseDirectory();

		File closedMarketDir;
		String[] marketMetaDataTokens;
		String[] individualMarketMetaDataTokens;
		BufferedWriter outputWriter;

		marketMetaDataTokens = closedMarketData.get(0).get(0).split("_");

		String folderName = marketMetaDataTokens[1].replaceAll(
				"[^\\p{Alpha}]+", "");

		closedMarketDir = new File(baseDirectory.getPath() + separator + folderName);
		closedMarketDir.mkdir();

		try
		{
			// Deal with inner lists
			for (int j = 0; j < closedMarketData.size(); j++)
			{
				individualMarketMetaDataTokens = closedMarketData.get(j).get(0)
						.split("_");

				if (individualMarketMetaDataTokens.length == 3)
				{
					outputWriter = new BufferedWriter(new FileWriter(
							closedMarketDir.getPath() + separator + "ALLDATA" + ".txt"));
				}
				else
				{
					outputWriter = new BufferedWriter(
							new FileWriter(
									closedMarketDir.getPath()
											+ separator
											+ individualMarketMetaDataTokens[individualMarketMetaDataTokens.length - 1]
											+ ".csv"));
				}
				for (int a = 0; a < closedMarketData.get(j).size(); a++)
				{
					outputWriter.write(closedMarketData.get(j).get(a));
				}
				outputWriter.flush();
				outputWriter.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Method overwritten from TimerTask. In this case it starts method calls
	 * that adds new data to the collections from the BetFair API.
	 */
	@Override
	public void run()
	{
		List<String> trackedMarkets;
		List<MarketBook> marketData;
		MarketBook currentBook;
		String[] metaDataTokens;
		
		/*
		 * For each game we're tracking. (Each game id is mapped
		 * to a list of the market IDs inside it we're tracking).
		 */
		for (String gameIDKey : gameToMarkets.keySet())
		{
			trackedMarkets = gameToMarkets.get(gameIDKey);
			marketData = betFair.getMarketBook(trackedMarkets);
			//TODO send all market data to get stored.
			/*
			 * 
				System.out.println("data size " + gameData.size() + " other size  " + marketData.size());
				//System.out.println(marketData.get(i));
				
				
				Gson x = new Gson();
				Type typeOfSrc = new TypeToken<MarketBook>(){}.getType();
				System.out.println("JSON CON " + x.toJson(marketData.get(i), MarketBook.class));
				//System.out.println(x);
				//Possibly missing: "json {"jsonrpc":"2.0","result":[{
				
			 */
			
			
			//Each index returned represents one market.
			assert marketData.size() == trackedMarkets.size();

			//For each of of our lists of lists of game data
			for (int i = 0; i < gameData.size(); i++)
			{
				//Get metadata line from the ith markets (all data list index).
				metaDataTokens = gameData.get(i).get(0).get(0)
						.split("_");

				//For each index of the list of data from the API
				for (int j = 0; j < marketData.size(); j++)
				{
					//If its market id matches our current lists id
					if (marketIdToName.get(marketData.get(j).getMarketId())
							.equalsIgnoreCase(metaDataTokens[1]))
					{
						currentBook = marketData.get(j);

						// If the market is closed then we can stop tracking it
						if (currentBook.getStatus().equalsIgnoreCase(
								BetFairMarketStatus.CLOSED_MARKET.toString()))
						{
							//Grab our market list for the current game from the map
							List<String> currentMarketList = gameToMarkets
									.remove(gameIDKey);
							
							//Remove the market id from its list
							currentMarketList.remove(currentMarketList.indexOf(currentBook
									.getMarketId()));
							//TODO throw event here instead
							System.out.println("Market: "
									+ marketIdToName.get(currentBook
											.getMarketId()) + " has closed."
									+ currentMarketList.size() + " markets left.");

							//If all the markets we've been tracking shuts then we stop
							if (currentMarketList.isEmpty())
							{
								System.out
										.println("Last market closed. Shutting down.");
								this.cancel();
								saveData(gameData.remove(i));
							}
							else
							{
								//Otherwise pop our new list back on and save the current markets data
								gameToMarkets.put(gameIDKey, currentMarketList);
								saveData(gameData.remove(i));
							}
							break;
						}
						else
						{
							// Look for a match to the markets name, resolved by
							// the map, to the market name in metadata
							if (marketIdToName.get(currentBook.getMarketId())
									.equalsIgnoreCase(metaDataTokens[1]))
							{
								System.out.println("Adding data for market: "
										+ metaDataTokens[1]);
								gatherData(currentBook.getRunners(),
										gameData.get(i));
								System.out.println();
								break;
							}
						}
					}
				}
			}
		}
		System.out.println("Iteration " + counter + " complete!");
		counter++;
	}

	/**
	 * The purpose of this class is to be given in a List of runner data and a
	 * List of Lists which represents the top level list for storing market data
	 * and the lower level lists [0][0] [0][1] are for storing runner specific
	 * data It analyses the sub-lists metadata given and calls methods to
	 * distribute the data as necessary
	 * 
	 * @param runners
	 *            The list of runners in a market
	 * @param currentList
	 *            A List of Lists representing data stored from the market
	 */
	private void gatherData(List<Runner> runners,
			List<ArrayList<String>> currentList)
	{
		// For each sub-list we have
		for (int i = 0; i < currentList.size(); i++)
		{
			// Metadata is in index 0 of the sub-list
			String[] metaDataTokens = currentList.get(i).get(0).split("_");

			// 3 Tokens means all data is tracked
			if (metaDataTokens.length == 3)
			{
				storeAllGameData(runners, currentList.get(i));
			}
			// 4 Tokens is all the information in the all data but with a runner
			// name appended.
			else if (metaDataTokens.length == 4)
			{
				storeSelectiveRunnerData(runners, currentList.get(i),
						metaDataTokens[metaDataTokens.length - 1]);
			}
		}
	}

	/**
	 * 
	 * @param runners
	 * @param activeIndex
	 * @param token
	 */
	private void storeSelectiveRunnerData(List<Runner> runners,
			List<String> activeIndex, String token)
	{
		Runner trackedRunner;

		for (int i = 0; i < runners.size(); i++)
		{
			if (runnerIds.get(runners.get(i).getSelectionId())
					.equalsIgnoreCase(token))
			{
				trackedRunner = runners.get(i);
				storeRunnerData(trackedRunner, activeIndex);
				break;
			}
		}
	}

	private void storeRunnerData(Runner trackedRunner, List<String> activeIndex)
	{
		double workingBack = Double.MIN_VALUE;
		double workingLay = Double.MAX_VALUE;

		if (trackedRunner.getEx().getAvailableToBack().size() > 0
				&& trackedRunner.getEx().getAvailableToLay().size() > 0)
		{
			// for each individual back option
			for (PriceSize backOption : trackedRunner.getEx()
					.getAvailableToBack())
			{
				// find the biggest back value
				if (backOption.getPrice() > workingBack)
				{
					workingBack = backOption.getPrice();
				}
			}
			// for each individual lay option
			for (PriceSize layOption : trackedRunner.getEx()
					.getAvailableToLay())
			{
				// find the smallest lay value
				if (layOption.getPrice() < workingLay)
				{
					workingLay = layOption.getPrice();
				}
			}
			timer = LocalTime.now();
			activeIndex.add(timer + " , " + ((workingBack + workingLay) / 2)
					+ "\n");
			System.out.println("Writing " + timer + " , "
					+ ((workingBack + workingLay) / 2)); // +"\n"
		}
	}

	private void storeAllGameData(List<Runner> runners, List<String> activeIndex)
	{
		activeIndex.add("ENTRY: " + counter + "\n");
		activeIndex.add("\tTIME: " + LocalTime.now().toString() + "\n");

		for (Runner individual : runners)
		{
			activeIndex.add("\t\tRUNNER: "
					+ runnerIds.get(individual.getSelectionId()) + "\n");

			for (int i = 0; i < individual.getEx().getAvailableToBack().size(); i++)
			{
				activeIndex.add("\t\t\tBACK: (PRICE:"
						+ individual.getEx().getAvailableToBack().get(i)
								.getPrice()
						+ ",SIZE:"
						+ individual.getEx().getAvailableToBack().get(i)
								.getSize() + ")\n");
			}
			for (int i = 0; i < individual.getEx().getAvailableToLay().size(); i++)
			{
				activeIndex.add("\t\t\tLAY: (PRICE:"
						+ individual.getEx().getAvailableToLay().get(i)
								.getPrice()
						+ ",SIZE:"
						+ individual.getEx().getAvailableToLay().get(i)
								.getSize() + ")\n");
			}
		}
	}
	
	private void storeJSONReplyString()
	{
		
	}
	
	/**
	 * Store the amount of money matched for each market in the game
	 */
	private void storeMarketMoneyMatchedData()
	{
		//need an arraylist of values for every market present
		//String gameId = gameToMarkets.keySet();
		
		/*
		 * Get game id
		 * get marketbook for that game
		 * 
		 * for each marketbook if our initial index 0 has a name match then add data
		 * else skip
		 */
	}
}
