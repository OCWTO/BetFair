package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import betFairGSONClasses.MarketBook;
import betFairGSONClasses.PriceSize;
import betFairGSONClasses.Runner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import enums.BetFairMarketStatus;


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
	private List<ArrayList<ArrayList<String>>> gameData; // put in manager
	private ISimpleBetFair betFair;
	private LocalTime timer;
	private int counter;
	private File baseDirectory;
	private String separator = File.separator;
	private List<String> jsonMarketBookReplies; // put in manager
	private List<String> marketCatalogueActivity; // put in manager
	private List<Observer> observers;

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
		counter = 1;
		betFair = options.getBetFair();
		gameData = new ArrayList<ArrayList<ArrayList<String>>>();
		jsonMarketBookReplies = new ArrayList<String>();
		marketCatalogueActivity = new ArrayList<String>();
		initiliseCollections();
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
	private void initiliseCollections()
	{
		List<String> markets;
		List<BetFairMarketObject> marketCatalogue;
		List<Long> gameStartTimes = new ArrayList<Long>();
		// runnerIds = new HashMap<Long, String>();
		// For each individual game
		String gameId = manager.getGameId();
		// Grab the market catalogue, which contains list of all markets for
		// that game
		marketCatalogue = betFair.getMarketsForGame(gameId);

		// Get the List of marketIds we are tracking
		markets = manager.getMarkets();


		// For each market we track in that game
		for (int i = 0; i < markets.size(); i++)
		{
			ArrayList<ArrayList<String>> marketData = new ArrayList<ArrayList<String>>();

			// Loop through the market catalogue
			for (int j = 0; j < marketCatalogue.size(); j++)
			{
				// If the market ids match
				if (marketCatalogue.get(j).getId().equals(markets.get(i)))
				{
					gameStartTimes.add(marketCatalogue.get(j).getOpenDate().getTime());
					// addToRunnerMap(marketCatalogue.get(j).getRunners());

					// Index 0 of all market data lists are
					// gamename_marketname_marketstarttime (marketstarttime
					// should be gamestarttime)
					ArrayList<String> singleMarketData = new ArrayList<String>();
					singleMarketData.add(marketCatalogue.get(j).getMarketEvent().getName() + "_" + marketCatalogue.get(j).getName() + "_"
							+ marketCatalogue.get(j).getOpenDate().getTime());
					marketData.add(singleMarketData);
					// Generate Index 0 data for each runner we track that's
					// informative...ish
					for (int k = 0; k < marketCatalogue.get(j).getRunners().size(); k++)
					{
						ArrayList<String> singleMarketData2 = new ArrayList<String>();
						singleMarketData2.add((singleMarketData.get(0)) + "_" + marketCatalogue.get(j).getRunners().get(k).getRunnerName());
						marketData.add(singleMarketData2);
					}
				}
			}
			gameData.add(marketData);
			//NOTIFY HERE
		}
	}

	/**
	 * 
	 * @param runners
	 */

	/**
	 * Breaks up the contents of the given list and populates the gameToMarkets
	 * Map from the contents.
	 * 
	 * @param gameAndMarkets
	 *            A list of String which are in the form of {gameId,marketId}
	 */

	private void makeBaseDirectory(ArrayList<ArrayList<String>> closedMarketData)
	{
		if (baseDirectory != null)
			return;

		String currentDir = System.getProperty("user.dir");
		String destination = separator + "logs" + separator + "gamelogs" + separator;
		String metaDataTokens[] = closedMarketData.get(0).get(0).split("_");
		baseDirectory = new File(currentDir + destination + metaDataTokens[0]);
		baseDirectory.mkdir();
	}

	/**
	 * Save the recorded data to a set of files.
	 */
	private void saveData(ArrayList<ArrayList<String>> closedMarketData)
	{
		// Market names/runners can sometimes have invalid characters for
		// filenames so we have to remove them
		String fileNameRegex = "[^\\p{Alpha}]+";
		makeBaseDirectory(closedMarketData);

		File closedMarketDir;
		String[] marketMetaDataTokens;
		String[] individualMarketMetaDataTokens;
		BufferedWriter outputWriter;

		marketMetaDataTokens = closedMarketData.get(0).get(0).split("_");

		String folderName = marketMetaDataTokens[1].replaceAll(fileNameRegex, "");

		closedMarketDir = new File(baseDirectory.getPath() + separator + folderName);
		closedMarketDir.mkdir();

		try
		{
			// Deal with inner lists
			for (int j = 0; j < closedMarketData.size(); j++)
			{
				individualMarketMetaDataTokens = closedMarketData.get(j).get(0).split("_");

				if (individualMarketMetaDataTokens.length == 3)
				{
					outputWriter = new BufferedWriter(new FileWriter(closedMarketDir.getPath() + separator + "ALLDATA" + ".txt"));
				} else
				{
					outputWriter = new BufferedWriter(new FileWriter(closedMarketDir.getPath() + separator
							+ individualMarketMetaDataTokens[individualMarketMetaDataTokens.length - 1].replaceAll(fileNameRegex, "") + ".csv"));
				}
				for (int a = 0; a < closedMarketData.get(j).size(); a++)
				{
					outputWriter.write(closedMarketData.get(j).get(a));
				}
				outputWriter.flush();
				outputWriter.close();
			}
		} catch (IOException e)
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
		List<BetFairMarketData> marketData;
		BetFairMarketData currentBook;
		String[] metaDataTokens;

		//String gameId = manager.getGameId();
		trackedMarkets = manager.getMarkets();
		marketData = betFair.getMarketInformation(trackedMarkets);
		// TODO modify this method to avoid too much data responses.
		// storeCatalogueActivity(betFair.getMarketsForGame(gameId));
		storeJsonResults(marketData);

		// Each index returned represents one market.
		assert marketData.size() == trackedMarkets.size();

		// For each of of our lists of lists of game data
		for (int i = 0; i < gameData.size(); i++)
		{
			// Get metadata line from the ith markets (all data list index).
			metaDataTokens = gameData.get(i).get(0).get(0).split("_");

			// For each index of the list of data from the API
			for (int j = 0; j < marketData.size(); j++)
			{
				// If its market id matches our current lists id
				if (manager.getMarketName(marketData.get(j).getId()).equalsIgnoreCase(metaDataTokens[1]))
				{
					currentBook = marketData.get(j);

					// If the market is closed then we can stop tracking it
					if (currentBook.getStatus().equalsIgnoreCase(BetFairMarketStatus.CLOSED_MARKET.toString()))
					{
						// Grab our market list for the current game from the
						// map
						List<String> currentMarketList = manager.getMarkets();

						// Remove the market id from its list
						currentMarketList.remove(currentMarketList.indexOf(currentBook.getId()));
						// TODO throw event here instead
						System.out.println("Market: " + manager.getMarketName(currentBook.getId()) + " has closed." + currentMarketList.size()
								+ " markets left.");

						// If all the markets we've been tracking shuts then we
						// stop
						if (currentMarketList.isEmpty())
						{
							System.out.println("Last market closed. Shutting down.");
							saveData(gameData.remove(i));
							storeFinalData();
							this.cancel();
						} else
						{
							// Otherwise pop our new list back on and save the
							// current markets data
							manager.setMarkets(currentMarketList);
							// gameToMarkets.put(gameIDKey, currentMarketList);
							saveData(gameData.remove(i));
						}
						break;
						//NOTIFY HERE
					} else
					{
						// Look for a match to the markets name, resolved by
						// the map, to the market name in metadata
						if (manager.getMarketName(currentBook.getId()).equalsIgnoreCase(metaDataTokens[1]))
						{
							System.out.println("Adding data for market: " + metaDataTokens[1]);
							gatherData(currentBook.getRunners(), gameData.get(i));
							System.out.println();
						}
					}
				}
			}

		}
		//NOTIFY HERE
		//Need to pass up probabilities + runner names + timestamps for each market
		//store runner data does probabilities so i need to decouple areas again
		//So List of Market object with runner names + values
		//get io to do all the saving, have a method there that returns a list of market objects
		//so it passes that data in, calls the method to get an object out, once it has that then
		//it notifys observers with that object
		////Need start time, home and away, market objects
		//market objects can get runner objects of name and probabilities
		System.out.println("Iteration " + counter + " complete!");
		counter++;
	}

	private void storeFinalData()
	{
		storeMarketData();
		storeRawJson();
	}

	private void storeRawJson()
	{
		String filePath = baseDirectory.getPath() + separator + "rawjson.txt";

		BufferedWriter outputWriter;
		try
		{
			outputWriter = new BufferedWriter(new FileWriter(filePath));
			for (int a = 0; a < jsonMarketBookReplies.size(); a++)
			{
				outputWriter.write(jsonMarketBookReplies.get(a));
			}
			outputWriter.flush();
			outputWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void storeMarketData()
	{
		String filePath = baseDirectory.getPath() + separator + "marketMatchedData.txt";

		BufferedWriter outputWriter;
		try
		{
			outputWriter = new BufferedWriter(new FileWriter(filePath));
			for (int a = 0; a < marketCatalogueActivity.size(); a++)
			{
				outputWriter.write(marketCatalogueActivity.get(a));
			}
			outputWriter.flush();
			outputWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates strings of timestamp and market info for games
	 * 
	 * @param list
	 */
	private void storeCatalogueActivity(List<BetFairMarketObject> list)
	{
		List<String> marketIds = new ArrayList<String>();
		for (BetFairMarketObject catalogueItem : list)
			marketIds.add(catalogueItem.getId());

		// marketbook
		List<BetFairMarketData> bookDetails = betFair.getMarketInformation(marketIds);

		StringBuilder catalogueInformationBuilder = new StringBuilder();

		catalogueInformationBuilder.append("TIMESTAMP:" + LocalTime.now() + "\n");

		for (BetFairMarketData bookItem : bookDetails)
		{
			for (int i = 0; i < list.size(); i++)
			{
				if (bookItem.getId().equals(list.get(i).getId()))
				{
					catalogueInformationBuilder.append("\t{" + list.get(i).getName() + "," + bookItem.getId() + ", matched: "
							+ bookItem.getMatchedAmount() + ", available: " + bookItem.getUnmatchedAmount() + ", TOTAL " + bookItem.getTotalAmount()
							+ ",\n");
				}
			}
		}
		System.out.println("Adding " + catalogueInformationBuilder.toString());
		marketCatalogueActivity.add(catalogueInformationBuilder.toString());
	}

	private void storeJsonResults(List<BetFairMarketData> marketData)
	{
		String gsonNotationResults = convertToJson(marketData);
		jsonMarketBookReplies.add(gsonNotationResults);
	}

	/**
	 * Takes in a list of MarketBook objects and converts them back into a comma
	 * separated json string, identical to what the program initially received
	 * converts it pretty much the same except the notation for time is
	 * different from the betfair api.
	 * 
	 * @param marketData
	 * @return
	 */
	private String convertToJson(List<BetFairMarketData> marketData)
	{
		Gson gsonConvertor = new Gson();
		Type sourceType = new TypeToken<MarketBook>()
		{
		}.getType();

		StringBuilder jsonStringBuilder = new StringBuilder();
		jsonStringBuilder.append("{\"jsonrpc\":\"2.0\",\"result\":[");
		for (int i = 0; i < marketData.size(); i++)
		{
			jsonStringBuilder.append(gsonConvertor.toJson(marketData.get(i).getRawBook(), sourceType));

			if (i == marketData.size() - 1)
			{
				jsonStringBuilder.append("],\"id\":\"1\"}");
			} else
			{
				jsonStringBuilder.append(",");
			}
		}
		return jsonStringBuilder.toString();
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
	private void gatherData(List<Runner> runners, List<ArrayList<String>> currentList)
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
				storeSelectiveRunnerData(runners, currentList.get(i), metaDataTokens[metaDataTokens.length - 1]);
			}
		}
	}

	/**
	 * 
	 * @param runners
	 * @param activeIndex
	 * @param token
	 */
	private void storeSelectiveRunnerData(List<Runner> runners, List<String> activeIndex, String token)
	{
		Runner trackedRunner;

		for (int i = 0; i < runners.size(); i++)
		{
			if (manager.getRunnerName(runners.get(i).getSelectionId()).equalsIgnoreCase(token))
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

		if (trackedRunner.getEx().getAvailableToBack().size() > 0 && trackedRunner.getEx().getAvailableToLay().size() > 0)
		{
			// for each individual back option
			for (PriceSize backOption : trackedRunner.getEx().getAvailableToBack())
			{
				// find the biggest back value
				if (backOption.getPrice() > workingBack)
				{
					workingBack = backOption.getPrice();
				}
			}
			// for each individual lay option
			for (PriceSize layOption : trackedRunner.getEx().getAvailableToLay())
			{
				// find the smallest lay value
				if (layOption.getPrice() < workingLay)
				{
					workingLay = layOption.getPrice();
				}
			}
			timer = LocalTime.now();
			activeIndex.add(timer + " , " + ((workingBack + workingLay) / 2) + "\n");
			// TODO code to track probabilitiy goes here
			// need timestamp,runner name, market name and value
			System.out.println("Writing " + timer + " , " + ((workingBack + workingLay) / 2)); // +"\n"
		}
	}

	private void storeAllGameData(List<Runner> runners, List<String> activeIndex)
	{
		activeIndex.add("ENTRY: " + counter + "\n");
		activeIndex.add("\tTIME: " + LocalTime.now().toString() + "\n");

		for (Runner individual : runners)
		{
			activeIndex.add("\t\tRUNNER: " + manager.getRunnerName(individual.getSelectionId()) + "\n");

			for (int i = 0; i < individual.getEx().getAvailableToBack().size(); i++)
			{
				activeIndex.add("\t\t\tBACK: (PRICE:" + individual.getEx().getAvailableToBack().get(i).getPrice() + ",SIZE:"
						+ individual.getEx().getAvailableToBack().get(i).getSize() + ")\n");
			}
			for (int i = 0; i < individual.getEx().getAvailableToLay().size(); i++)
			{
				activeIndex.add("\t\t\tLAY: (PRICE:" + individual.getEx().getAvailableToLay().get(i).getPrice() + ",SIZE:"
						+ individual.getEx().getAvailableToLay().get(i).getSize() + ")\n");
			}
		}
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