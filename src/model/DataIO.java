package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import betfairGSONClasses.PriceSize;
import betfairGSONClasses.Runner;
import betfairGSONClasses.RunnerCatalog;
import betfairUtils.JsonConverter;
import enums.BetfairMarketStatus;

/**
 * This class receives live Betfair data and it collects and formats it. Once markets close
 * it then saves its data collections for references.
 * If the program is in test mode then it will run as usual except it won't produce any log files
 * If run in normal mode (testMode is false) then it will run and produce log files
 * @author Craig
 *
 */
public class DataIO
{
	//Stored market data, each MarketDataContainer is an individual runner and this list makes up the game
	private List<BetfairMarketDataContainer> storedMarketData;
	//Stored JSON, used for producing test files
	private List<String> jsonAPIReplies;
	//Storing the amount of money available/matched on markets
	private List<String> marketCatalogueActivity; 
	private List<String> recentlyClosedMarkets;
	//DataManager used to hold required additional data (names that ids map to, list of markets etc)
	private DataManager manager;
	private String separator = File.separator;
	//Base file directory for the game thats made when data starts getting saved
	private File baseDirectory;
	private int counter;
	private boolean testMode;
	//The most recent time that data was received
	private long lastReceivedTime;
	
	/**
	 * Create a dataIO object
	 * @param manager DataManager object containing metadata required by DataIO.
	 * @param testMode true if testmode is on, false otherwise.
	 */
	public DataIO(DataManager manager, boolean testMode)
	{
		this.testMode = testMode;
		this.manager = manager;
		counter = 1;
		storedMarketData = new ArrayList<BetfairMarketDataContainer>();
		jsonAPIReplies = new ArrayList<String>();
		marketCatalogueActivity = new ArrayList<String>();
		recentlyClosedMarkets = new ArrayList<String>();
	}

	/**
	 * Initilise this object, it initially creates objects which will store the live data
	 * @param initialMarketObjects The BetFairMarket objects that will be used to initialise
	 */
	public void initilise(List<BetfairMarketObject> initialMarketObjects)
	{
		//If not test mode then prepare json array for production of logs used for testing
		if(!testMode)
		{
			//Save the initial state of options so it can be used for running the program on old data
			ProgramOptions customOptions = (ProgramOptions) manager.getOptions().clone();
			//Remove the betfair object since it can't be serialised
			customOptions.addBetFair(null);
			//Add it to the front of the json array so its at the start of the test file
			storeAsJson(customOptions);
			storeAsJson(initialMarketObjects);
		}
		
		//Get the list of markets that we're tracking
		List<String> storedMarketList = manager.getTrackedMarketIds();

		// For each market that's going to be tracked
		for (String storedMarketId : storedMarketList)
		{
			//Loop through our received market data
			for (BetfairMarketObject receivedMarket : initialMarketObjects)
			{
				//If the received id matches the tracked id
				if (receivedMarket.getMarketId().equals(storedMarketId))
				{
					String marketName = receivedMarket.getName();
					String marketId = receivedMarket.getMarketId();
					String gameName = receivedMarket.getGamesName();
					Date marketOpenDate = receivedMarket.getOpenDate();
					
					//Create a new data container for this market
					BetfairMarketDataContainer currentMarketContainer = new BetfairMarketDataContainer(gameName, marketName, marketId, marketOpenDate);

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
	
	/**
	 * Get BetFairMarketData objects for only those markets that are being tracked
	 * @param allMarketData a list of BetFairMarketData objects
	 * @return a filtered list of BetFairMarketData objects, only for the tracked markets.
	 */
	private List<BetfairMarketData> getTrackedMarketData(List<BetfairMarketData> allMarketData)
	{
		//Get the list of ids that we want to track
		List<String> trackedMarkets = manager.getTrackedMarketIds();
		
		List<BetfairMarketData> matchedMarketData = new ArrayList<BetfairMarketData>();
		
		//Iterate through all data received and store the desired markets data.
		for(BetfairMarketData marketObj : allMarketData)
		{
			if(trackedMarkets.contains(marketObj.getMarketId()))
			{
				matchedMarketData.add(marketObj);
			}
		}
		return matchedMarketData;
	}

	/**
	 * Add new data received from the Betfair API to this classes collections
	 * @param liveMarketData A list of BetFairMarketData objects representing the received data.
	 */
	public void addData(List<BetfairMarketData> liveMarketData)
	{	
		//Make a new object for the closed market list every time data is added
		recentlyClosedMarkets = new ArrayList<String>();
		
		//Store the new timestamp
		lastReceivedTime = liveMarketData.get(0).getReceivedTime();
		
		//Filter out the market data we want from what we receive (all market data)
		List<BetfairMarketData> trackedMarketData = getTrackedMarketData(liveMarketData);
		
		//Only store data if not in test mode
		if(!testMode)
			storeAsJson(trackedMarketData);

		BetfairMarketData currentBook;

		//Assert that we filtered correctly and we initially got the expected number of results.
		assert liveMarketData.size() == manager.getAllMarketIds().size();
		assert trackedMarketData.size() == manager.getTrackedMarketIds().size();
		
		//For each market we're tracking
		for(int j = 0; j < storedMarketData.size(); j++)
		{
			//Loop through the list of market data we received
			for(int i = 0; i < trackedMarketData.size(); i++)
			{
				//If the live id matches the tracked id
				if(trackedMarketData.get(i).getMarketId().equalsIgnoreCase(storedMarketData.get(j).getMarketId()))
				{
					//Grab the data
					currentBook = trackedMarketData.get(i);
					
					//If this markets status is closed
					if(currentBook.getStatus().equals(BetfairMarketStatus.CLOSED_MARKET.toString()))
					{
						//Create the base directory if it doesn't exist and this isn't test mode
						if(!testMode)
						{
							if(baseDirectory == null)
								makeBaseDirectory(storedMarketData.get(j).getGameName());
						}	
						//Grab our list of ids we query for and remove the closed market from it
						recentlyClosedMarkets.add(manager.getMarketName(currentBook.getMarketId()) + "," + lastReceivedTime);
						manager.stopTrackingMarketId(currentBook.getMarketId());
						
						//If all markets are closed then we shut down.
						if(manager.getTrackedMarketIds().size() == 0)
						{
							//Call method to store all of the JSON and financial data
							if(!testMode)
								storeFinalData();
						}
						//Send our data container to be saved, if necessary
						if(!testMode)
						{
							saveData(storedMarketData.remove(j));
						}
						else
						{
							storedMarketData.remove(j);
						}
						//decrement here so markets aren't missed out
						j--;
						break;
					}
					//Market isn't shut so we add more data
					else
					{
						gatherData(currentBook.getRunners(), storedMarketData.get(j));
					}
				}			
			}
		}
		System.out.println("Add interation " + counter + " completed!");
		counter++;
	}
	
	/**
	 * Make base directory for all of this games market data to go in.
	 * @param gameName The name of the game that the directory is for.
	 */
	private void makeBaseDirectory(String gameName)
	{
		//Get the current path and create a directory containing the game name
		String currentDir = System.getProperty("user.dir");
		String destination = separator + "logs" + separator + "gamelogs" + separator;
		String folderName = gameName;
		baseDirectory = new File(currentDir + destination + folderName);
		baseDirectory.mkdir();
	}

	/**
	 * Save the recorded data to a set of files.
	 */
	private void saveData(BetfairMarketDataContainer closedMarketData)
	{
		//Regex for removing characters than can't go into file names
		String fileNameRegex = "[^\\p{Alnum}]+";

		//File for the directory of this market (which is inside baseDirectory)
		File closedMarketDir;
		BufferedWriter outputWriter;
		String folderName = closedMarketData.getMarketName().replaceAll(fileNameRegex, "_");

		//Create a folder for the market name, inside the game folder
		closedMarketDir = new File(baseDirectory.getPath() + separator + folderName);
		closedMarketDir.mkdir();
		
		//Grab metadata line that will go in all saved files
		String metaDataLine = closedMarketData.getGameName() + " " + closedMarketData.getMarketName() + " OPEN DATE: " + closedMarketData.getMarketOpenDate();
		try
		{
			//Initially write the alldata array first and then each runners individual data.
			List<String> allMarketData = closedMarketData.getAllMarketDataContainer();
			outputWriter = new BufferedWriter(new FileWriter(closedMarketDir.getPath() + separator + "ALLDATA" + ".txt"));
			
			//Writing the meta data line, then all of its contents
			outputWriter.write(metaDataLine + "\n\n");
			for(int i = 0; i < allMarketData.size(); i++)
			{
				outputWriter.write(allMarketData.get(i) +  "\n");
			}
			outputWriter.close();
			
			//For each runner, write their data to a file.
			for(BetfairMarketRunnerDataContainer runner: closedMarketData.getAllRunnerData())
			{	
				String fileName = runner.getName().replaceAll(fileNameRegex, "_");
				outputWriter = new BufferedWriter(new FileWriter(closedMarketDir.getPath() + separator
						+ fileName + ".csv"));
				outputWriter.write(metaDataLine + " " + runner.getName() + "\n"); 
				
				
				List<String> runnerData = runner.getRunnerDataList();
				
				for(int i = 0; i < runnerData.size(); i++)
				{
					//runnerdata is a set of comma separated strings (ideally would be a wrapper class)
					//ind 0 = timestamp, 1 = value
					String[] tokens = runnerData.get(i).split(",");		
					DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss").withZone(ZoneId.systemDefault());
					Instant instant = Instant.ofEpochMilli(new Long(tokens[0]));	
					format.format(instant);
					outputWriter.write(format.format(instant) + "," + tokens[1] +  "\n");
				}
				outputWriter.close();
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Writes jsonAPIReplies and marketCatalogueActivity to files
	 */
	private void storeFinalData()
	{
		saveMarketMoneyData();
		storeRawJson();
	}

	/**
	 * Write the jsonAPIReplies collection to a file. The file is usable for later testing.
	 */
	private void storeRawJson()
	{
		String filePath = baseDirectory.getPath() + separator + "rawjson.txt";

		BufferedWriter outputWriter;
		try
		{
			outputWriter = new BufferedWriter(new FileWriter(filePath));
			for (int a = 0; a < jsonAPIReplies.size(); a++)
			{
				outputWriter.write(jsonAPIReplies.get(a) + "\n");
			}
			outputWriter.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Store all data for money matched during the game for every market
	 */
	private void saveMarketMoneyData()
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
			outputWriter.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Get the list of markets that have just closed
	 * @return List of markets that have closed in the form of 'market name, time closed' where closing time is the ms from epoch
	 */
	public List<String> getRecentlyClosedMarkets()
	{
		List<String> closedList = new ArrayList<String>(recentlyClosedMarkets);
		return closedList;
	}
	
	/**
	 * Store the financial activity for all active markets
	 * @param allMarkets MarketData objects representing active markets
	 */
	public void storeCatalogueActivity(List<BetfairMarketData> allMarkets)
	{
		//Grab the list of all markets
		List<String> marketIds = manager.getAllMarketIds();

		//Horrible way of converting epoch time to java 8 LocalDateTime object
		StringBuilder catalogueInformationBuilder = new StringBuilder();
		catalogueInformationBuilder.append("TIMESTAMP: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(lastReceivedTime), ZoneId.systemDefault()) + "\n");

		//For all markets
		for (BetfairMarketData bookItem : allMarkets)
		{
			//Loop through the set of all ids
			for (int i = 0; i < marketIds.size(); i++)
			{
				//If the market matches up to the id
				if (bookItem.getMarketId().equals(marketIds.get(i)))
				{
					//If a closed market is found. This stops future requests for non 'tracked' (used for predictions) markets
					if(bookItem.getStatus().equals(BetfairMarketStatus.CLOSED_MARKET.toString()))
					{
						//Stop tracking the market...
						manager.stopTrackingMarketId(bookItem.getMarketId());
						break;
					}
					else
					{	
						//If its not closed then store the money matched/unmatched data.
						catalogueInformationBuilder.append("\t{" + manager.getMarketName(marketIds.get(i)) + "," + bookItem.getMarketId() + ", matched: "
						+ bookItem.getMatchedAmount() + ", available: " + bookItem.getUnmatchedAmount() + ", TOTAL "
						+ bookItem.getTotalAmount() + "}\n");
						break;
					}
				}
			}	
		}
		//Store the built string.
		marketCatalogueActivity.add(catalogueInformationBuilder.toString());
	}

	/**
	 * Convert the given obj to json and store it in jsonAPliReplies
	 * @param obj The object to be stored as JSON
	 */
	private void storeAsJson(Object obj)
	{
		jsonAPIReplies.add(JsonConverter.convertToJson(obj));
	}

	/**
	 * Gather data from the given parameters and store in relevant collections 
	 * @param runners The list of runners for the market that currentMarketDataContainer holds data for
	 * @param currentMarketDataContainer Collection of data for the runner
	 */
	private void gatherData(List<Runner> runners, BetfairMarketDataContainer currentMarketDataContainer)
	{
		if(!testMode)
		{
			//Store the raw odds data, only needed if logs are produced
			storeAllGameData(runners, currentMarketDataContainer.getAllMarketDataContainer());
			//Store each runners data, and get probabilities
		}
		//Selective runner data always needs stored since that is what this outputs on recentdata calls
		storeSelectiveRunnerData(runners, currentMarketDataContainer);
		
	}

	/**
	 * @param runners list of the runners for the market container (contain new unstored data)
	 * @param currentMarketDataContainer The data container which has older data for the given runners
	 */
	private void storeSelectiveRunnerData(List<Runner> runners, BetfairMarketDataContainer currentMarketDataContainer)
	{
		//For each runner, get the list for that runner and add data to it
		for(Runner marketRunner : runners)
		{
			//Resolve the runners id to its actual name
			String runnerName = manager.getRunnerName(marketRunner.getSelectionId());
			//Get the data container for the single runner
			BetfairMarketRunnerDataContainer marketRunnerContainer = currentMarketDataContainer.getContainerForRunner(runnerName);
			//Add the new data to the existing container
			storeRunnerData(marketRunner, marketRunnerContainer);
		}
	}

	/**
	 * Extract and store probability data from trackedRunner to runnerDataContainer
	 * @param trackedRunner Data container for the runner that will be added to runnerDataContainer
	 * @param runnerDataContainer Data container that has older data for the runner
	 */
	private void storeRunnerData(Runner trackedRunner, BetfairMarketRunnerDataContainer runnerDataContainer)
	{
		double workingBack = Double.MIN_VALUE;
		double workingLay = Double.MAX_VALUE;

		//If the runner has some back and lay options available
		if (trackedRunner.getEx().getAvailableToBack().size() > 0 && trackedRunner.getEx().getAvailableToLay().size() > 0)
		{
			//Find the "best" back value (biggest back)
			for (PriceSize backOption : trackedRunner.getEx().getAvailableToBack())
			{
				if (backOption.getPrice() > workingBack)
				{
					workingBack = backOption.getPrice();
				}
			}
			//Find the "best" lay value (lowest lay)
			for (PriceSize layOption : trackedRunner.getEx().getAvailableToLay())
			{
				if (layOption.getPrice() < workingLay)
				{
					workingLay = layOption.getPrice();
				}
			}
			//Store the probability values
			runnerDataContainer.addData(lastReceivedTime, (1/((workingBack + workingLay)/2)));
		}
		else
		{
			//When there's not enough data then 0 is the value, this is filtered out later when used for predictions
			runnerDataContainer.addData(lastReceivedTime, 0);
		}
	}
	
	/**
	 * Get the most recently added probability values for each markets runners
	 * @return List of BetFairMarket items for each tracked market containing 1 index of data for each
	 * runner inside them with probabilities and timestamps
	 */
	public List<BetfairMarketItem> getRecentData()
	{
		//Collection for all markets data
		List<BetfairMarketItem> marketInformation = new ArrayList<BetfairMarketItem>();
		
		//Go through each markets container
		for(BetfairMarketDataContainer marketData : storedMarketData)
		{
			//Make new objects for the markets
			BetfairMarketItem marketItem = new BetfairMarketItem(marketData.getMarketName());
			List<BetfairMarketRunnerDataContainer> runnerData = marketData.getAllRunnerData();
			
			//Grab all recent runner data (just added)
			for(BetfairMarketRunnerDataContainer runnerDataContainer : runnerData)
			{
				String timeStamp = runnerDataContainer.getMostRecentTimeStamp();
				String probability = runnerDataContainer.getMostRecentProbability();
				String runnerName = runnerDataContainer.getName();
				marketItem.addRunnerProbability(new BetfairProbabilityItem(Long.valueOf(timeStamp), runnerName, Double.valueOf(probability)));
			}
			marketInformation.add(marketItem);
		}
		return marketInformation;
	}

	/**
	 * Store raw odds data that has been collected, from the runner parameter into the index paramter
	 * @param runners new runner data
	 * @param activeIndex List of strings that the new runner data will be added to
	 */
	private void storeAllGameData(List<Runner> runners, List<String> activeIndex)
	{
		activeIndex.add("ENTRY: " + counter + "\n");
		activeIndex.add("\tTIME: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(lastReceivedTime), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("hh:mm:ss")) + "\n");
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