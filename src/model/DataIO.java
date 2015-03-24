package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//need to check if started,
//start the thing, it will init if not already then has a method for adddata
//method for save (marketname)
//needs to recognise when to stop tracking


import betFairGSONClasses.MarketBook;
import betFairGSONClasses.PriceSize;
import betFairGSONClasses.Runner;
import betFairGSONClasses.RunnerCatalog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import enums.BetFairMarketStatus;

//this class will only receive data and deal with it
public class DataIO
{
	private List<MarketDataContainer> storedMarketData;
	private File baseDirectory;
	private List<String> jsonMarketBookReplies;
	private List<String> marketCatalogueActivity; 
	private int counter;
	private DataManager manager;
	private String separator = File.separator;

	public DataIO(DataManager manager)
	{
		storedMarketData = new ArrayList<MarketDataContainer>();
		this.manager = manager;
		counter = 1;
		jsonMarketBookReplies = new ArrayList<String>();
		marketCatalogueActivity = new ArrayList<String>();
	}

	public void initilise(List<BetFairMarketObject> receivedMarketObjects)
	{
		//Get the list of markets that we're tracking
		List<String> storedMarketList = manager.getMarkets();

		// For each market that's going to be tracked
		for (String storedMarketId : storedMarketList)
		{
			//Loop through our received market data
			for (BetFairMarketObject receivedMarket : receivedMarketObjects)
			{
				//If the received id matches the tracked id
				if (receivedMarket.getMarketId().equals(storedMarketId))
				{
					String marketName = receivedMarket.getName();
					String marketId = receivedMarket.getMarketId();
					String gameName = receivedMarket.getGamesName();
					Date marketOpenDate = receivedMarket.getOpenDate();
					
					//Create a new data container for this market
					MarketDataContainer currentMarketContainer = new MarketDataContainer(gameName, marketName, marketId, marketOpenDate);

					//Loop through all of its runners and create collections for them. 
					for (RunnerCatalog runner : receivedMarket.getRunners())
					{
						currentMarketContainer.addNewRunner(runner.getRunnerName());
					}
					//Add it to our list of market data containers
					storedMarketData.add(currentMarketContainer);
				}
			}
		}
	}

	public void addData(List<BetFairMarketData> liveMarketData)
	{
		List<String> trackedMarkets;
		BetFairMarketData currentBook;

		trackedMarkets = manager.getMarkets();
		storeJsonResults(liveMarketData);
		//storeCatalogueActivity(liveAllMarkets);

		// Each index returned represents one market.
		assert liveMarketData.size() == trackedMarkets.size();

		//For each market we're tracking
		for(MarketDataContainer currentMarketDataContainer : storedMarketData)
		{
			//Loop through the list of market data we received
			for(int i = 0; i < liveMarketData.size(); i++)
			{
				System.out.println("comparing ids");
				System.out.println(liveMarketData.get(i).getMarketId() + " VS " + currentMarketDataContainer.getMarketId());
				if(liveMarketData.get(i).getMarketId().equalsIgnoreCase(currentMarketDataContainer.getMarketId()))
				{
					System.out.println("id match");
					currentBook = liveMarketData.get(i);
					
					//If this markets status is closed
					if(currentBook.getStatus().equals(BetFairMarketStatus.CLOSED_MARKET.toString()))
					{
						//Grab our list of ids we query for and removed the closed market from it
						List<String> currentMarketList = manager.getMarkets();
						currentMarketList.remove(currentMarketList.indexOf(currentBook.getMarketId()));
						
						//If all markets are closed then we shut down.
						if(currentMarketList.isEmpty())
						{
							storeFinalData();
						}
						else
						{
							manager.setMarkets(currentMarketList);
						}
						int currentContainerIndex = storedMarketData.indexOf(currentMarketDataContainer);
						//Send our data container to be saved
						saveData(storedMarketData.remove(currentContainerIndex));
						break;
					}
					//Market isn't shut so we add more data
					else
					{
						System.out.println("Adding data for market: " + currentMarketDataContainer.getMarketName());
						gatherData(currentBook.getRunners(), currentMarketDataContainer);
					}
				}			
			}
		}
		System.out.println("Iteration " + counter + " complete!");
		counter++;
	}
	

	private void makeBaseDirectory(MarketDataContainer closedMarketData)
	{
		if (baseDirectory != null)
			return;

		String currentDir = System.getProperty("user.dir");
		String destination = separator + "logs" + separator + "gamelogs" + separator;
		String folderName = closedMarketData.getGameName();
		baseDirectory = new File(currentDir + destination + folderName);
		baseDirectory.mkdir();
	}

	/**
	 * Save the recorded data to a set of files.
	 */
	private void saveData(MarketDataContainer closedMarketData)
	{
		//Remove "bad" characters from filename so no issues saving
		String fileNameRegex = "[^\\p{Alnum}]+";
		makeBaseDirectory(closedMarketData);

		File closedMarketDir;
		BufferedWriter outputWriter;

		String folderName = closedMarketData.getMarketName().replaceAll(fileNameRegex, "_");

		closedMarketDir = new File(baseDirectory.getPath() + separator + folderName);
		closedMarketDir.mkdir();
		
		String metaDataLine = closedMarketData.getGameName() + " " + closedMarketData.getMarketName() + " OPEN DATE: " + closedMarketData.getMarketOpenDate();
		try
		{
			List<String> allMarketData = closedMarketData.getAllMarketDataContainer();
			outputWriter = new BufferedWriter(new FileWriter(closedMarketDir.getPath() + separator + "ALLDATA" + ".txt"));
			
			//Writing the meta data line, then all of its contents
			outputWriter.write(metaDataLine + "\n");
			for(int i = 0; i < allMarketData.size(); i++)
			{
				outputWriter.write(allMarketData.get(i) +  "\n");
			}
			
			
			for(MarketRunnerDataContainer runner: closedMarketData.getAllRunnerData())
			{
				String fileName = runner.getName().replaceAll(fileNameRegex, "_");
				outputWriter = new BufferedWriter(new FileWriter(closedMarketDir.getPath() + separator
						+ fileName + ".csv"));
				outputWriter.write(metaDataLine + " " + runner.getName() + "\n"); 
				
				List<String> runnerData = runner.getRunnerDataList();
				for(int i = 0; i < runnerData.size(); i++)
				{
					String[] tokens = runnerData.get(i).split(",");		
					DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss").withZone(ZoneId.systemDefault());
					Instant instant = Instant.ofEpochMilli(new Long(tokens[0]));	
					format.format(instant);
					outputWriter.write(format.format(instant) + "," + tokens[1] +  "\n");
				}
			}
			outputWriter.close();
		} 
		catch (IOException e)
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
			marketIds.add(catalogueItem.getMarketId());

		StringBuilder catalogueInformationBuilder = new StringBuilder();

		catalogueInformationBuilder.append("TIMESTAMP:" + LocalTime.now() + "\n");


			for (BetFairMarketData bookItem : allMarkets)
			{
				for (int i = 0; i < list.size(); i++)
				{
					if (bookItem.getMarketId().equals(list.get(i).getMarketId()))
					{
						catalogueInformationBuilder.append("\t{" + list.get(i).getName() + "," + bookItem.getMarketId() + ", matched: "
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
	private void gatherData(List<Runner> runners, MarketDataContainer currentMarketDataContainer)
	{
		storeAllGameData(runners, currentMarketDataContainer.getAllMarketDataContainer());
		storeSelectiveRunnerData(runners, currentMarketDataContainer);
	}

	/**
	 * 
	 * @param runners
	 * @param activeIndex
	 * @param token
	 */
	private void storeSelectiveRunnerData(List<Runner> runners, MarketDataContainer currentMarketDataContainer)
	{
		//For each runner, get the list for that runner and add data to it
		for(Runner marketRunner : runners)
		{
			//Resolve the runners id to its actual name
			String runnerName = manager.getRunnerName(marketRunner.getSelectionId());
			MarketRunnerDataContainer marketRunnerContainer = currentMarketDataContainer.getContainerForRunner(runnerName);
			storeRunnerData(marketRunner, marketRunnerContainer);
		}
	}

	private void storeRunnerData(Runner trackedRunner, MarketRunnerDataContainer runnerDataContainer)
	{
		double workingBack = Double.MIN_VALUE;
		double workingLay = Double.MAX_VALUE;

		//If the runner has some back and lay options available
		if (trackedRunner.getEx().getAvailableToBack().size() > 0 && trackedRunner.getEx().getAvailableToLay().size() > 0)
		{
			//Calculate the "best" back value (biggest back)
			for (PriceSize backOption : trackedRunner.getEx().getAvailableToBack())
			{
				if (backOption.getPrice() > workingBack)
				{
					workingBack = backOption.getPrice();
				}
			}
			//Calculate the "best" lay value (lowest lay)
			for (PriceSize layOption : trackedRunner.getEx().getAvailableToLay())
			{
				if (layOption.getPrice() < workingLay)
				{
					workingLay = layOption.getPrice();
				}
			}
			long time = System.currentTimeMillis();
			runnerDataContainer.addData(time, ((workingBack + workingLay) /2));
			System.out.println("Writing " + time + " , " + ((workingBack + workingLay) / 2) + " " + manager.getRunnerName(trackedRunner.getSelectionId())); // +"\n"
		}
		else
		{
			//Pass in 0 when there's not enough data, this is handled at a higher level elsewhere.
			runnerDataContainer.addData(System.currentTimeMillis(), 0);
		}
	}
	
	//Working with the idea that home market is always first
	public List<BetFairMarketItem> getRecentData()
	{
		//Create collection to hold all markets information
		List<BetFairMarketItem> marketInformation = new ArrayList<BetFairMarketItem>();
		
		for(MarketDataContainer marketData : storedMarketData)
		{
			BetFairMarketItem marketItem = new BetFairMarketItem(marketData.getMarketName());
			
			List<MarketRunnerDataContainer> runnerData = marketData.getAllRunnerData();
			
			for(MarketRunnerDataContainer runnerDataContainer : runnerData)
			{
				String timeStamp = runnerDataContainer.getMostRecentTimeStamp();
				String probability = runnerDataContainer.getMostRecentProbability();
				String runnerName = runnerDataContainer.getName();
				
				
				marketItem.addRunnerProbability(new BetFairProbabilityItem(timeStamp, runnerName, probability));
			}
			marketInformation.add(marketItem);
		}
		return marketInformation;
	}

	
	private void storeAllGameData(List<Runner> runners, List<String> activeIndex)
	{
		activeIndex.add("ENTRY: " + counter + "\n");
		activeIndex.add("\tTIME: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) + "\n");

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