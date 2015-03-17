package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
//need to check if started,
//start the thing, it will init if not already then has a method for adddata
//method for save (marketname)
//needs to recognise when to stop tracking

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import enums.BetFairMarketStatus;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.PriceSize;
import betFairGSONClasses.Runner;
import betFairGSONClasses.RunnerCatalog;

//this class will only receive data and deal with it
public class DataIO
{
	private List<ArrayList<ArrayList<String>>> gameData; // put in manager
	private LocalTime timer;
	private File baseDirectory;
	private List<String> jsonMarketBookReplies; // put in manager
	private List<String> marketCatalogueActivity; // put in manager
	private int counter;
	private DataManager manager;
	private String separator = File.separator;

	public DataIO(DataManager manager)
	{
		this.manager = manager;
		counter = 1;
		gameData = new ArrayList<ArrayList<ArrayList<String>>>();
		jsonMarketBookReplies = new ArrayList<String>();
		marketCatalogueActivity = new ArrayList<String>();
	}

	public void initilise(List<BetFairMarketObject> receivedMarketObjects)
	{
		// String gameId = manager.getGameId();
		List<String> storedMarketList = manager.getMarkets();

		// For each market that's going to be tracked
		for (String marketId : storedMarketList)
		{
			ArrayList<ArrayList<String>> marketData = new ArrayList<ArrayList<String>>();

			for (BetFairMarketObject receivedMarket : receivedMarketObjects)
			{
				if (receivedMarket.getId().equals(marketId))
				{
					ArrayList<String> individualMarketData = new ArrayList<String>();
					individualMarketData.add(receivedMarket.getMarketEvent().getName() + "_" + receivedMarket.getName() + "_"
							+ receivedMarket.getOpenDate().getTime());
					marketData.add(individualMarketData);

					for (RunnerCatalog runner : receivedMarket.getRunners())
					{
						ArrayList<String> runnerData = new ArrayList<String>();
						runnerData.add(individualMarketData.get(0) + "_" + runner.getRunnerName());
						marketData.add(runnerData);
					}
				}
			}
			gameData.add(marketData);
		}
		// TODO event here, init event so starttime, hometeam, awayteam
	}

	public void addData(List<BetFairMarketData> liveMarketData)
	{
		List<String> trackedMarkets;
		BetFairMarketData currentBook;
		String[] metaDataTokens;

		trackedMarkets = manager.getMarkets();
		storeJsonResults(liveMarketData);
		//storeCatalogueActivity(liveAllMarkets);

		// Each index returned represents one market.
		assert liveMarketData.size() == trackedMarkets.size();

		// For each of of our lists of lists of game data
		for (int i = 0; i < gameData.size(); i++)
		{
			// Get metadata line from the ith markets (all data list index).
			metaDataTokens = gameData.get(i).get(0).get(0).split("_");

			// For each index of the list of data from the API
			for (int j = 0; j < liveMarketData.size(); j++)
			{
				// If its market id matches our current lists id
				if (manager.getMarketName(liveMarketData.get(j).getId()).equalsIgnoreCase(metaDataTokens[1]))
				{
					currentBook = liveMarketData.get(j);

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
							
							
							//TODO throw event that cancels thread
							// this.cancel();
						} else
						{
							//TODO throw event to show closed market
							manager.setMarkets(currentMarketList);
							saveData(gameData.remove(i));
						}
						break;
						// NOTIFY HERE
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
		// NOTIFY HERE
		// Need to pass up probabilities + runner names + timestamps for each
		// market
		// store runner data does probabilities so i need to decouple areas
		// again
		// So List of Market object with runner names + values
		// get io to do all the saving, have a method there that returns a list
		// of market objects
		// so it passes that data in, calls the method to get an object out,
		// once it has that then
		// it notifys observers with that object
		// //Need start time, home and away, market objects
		// market objects can get runner objects of name and probabilities
		System.out.println("Iteration " + counter + " complete!");
		counter++;
	}

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
	
	//Need a list of marketdata for all markets
	//need list of marketobject for all markets
	private void storeCatalogueActivity(List<BetFairMarketObject> list, List<BetFairMarketData> allMarkets)
	{
		List<String> marketIds = new ArrayList<String>();
		for (BetFairMarketObject catalogueItem : list)
			marketIds.add(catalogueItem.getId());

		StringBuilder catalogueInformationBuilder = new StringBuilder();

		catalogueInformationBuilder.append("TIMESTAMP:" + LocalTime.now() + "\n");


			for (BetFairMarketData bookItem : allMarkets)
			{
				for (int i = 0; i < list.size(); i++)
				{
					if (bookItem.getId().equals(list.get(i).getId()))
					{
						catalogueInformationBuilder.append("\t{" + list.get(i).getName() + "," + bookItem.getId() + ", matched: "
								+ bookItem.getMatchedAmount() + ", available: " + bookItem.getUnmatchedAmount() + ", TOTAL "
								+ bookItem.getTotalAmount() + ",\n");
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
}