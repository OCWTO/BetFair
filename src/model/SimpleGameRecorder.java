package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import betfairUtils.MarketBook;
import betfairUtils.MarketCatalogue;
import betfairUtils.PriceSize;
import betfairUtils.Runner;
import betfairUtils.RunnerCatalog;

public class SimpleGameRecorder extends TimerTask
{
	private BetFairCore betFair;
	private List<String> collectedData;
	private List<String> formattedCollectedData;
	private List<MarketBook> tempData;
	private List<Runner> runners;
	private Map<Long, String> runnerIds;
	private String gameName;
	private String marketId;
	private long startTimeMS;
	private int counter;
	private String selectedRunner;
	private StringBuilder builder;
	
	private final int simplePrintMode = 1;
	private final int simpleRecordMode = 2;
	private final int recordAllMode = 3;
	private int mode;
	
	public SimpleGameRecorder(BetFairCore betFair, String gameId,
			String marketId, int mode)
	{
		this.betFair = betFair;
		this.marketId = marketId;
		this.mode = mode;
		counter = 1;
		collectedData = new ArrayList<String>();
		formattedCollectedData = new ArrayList<String>();
		runnerIds = new HashMap<Long, String>();
		tempData = new ArrayList<MarketBook>();
		runners = new ArrayList<Runner>();
		builder = new StringBuilder();
		getInitValues(gameId);
		
	}

	private void getInitValues(String gameId)
	{
		String marketName = null;
		// get market catalogue, get runner names and map to runner id, put in
		// initial value saying market, starttime, runners
		List<MarketCatalogue> catalogue = betFair.getMarketCatalogue(gameId);
		for (int i = 0; i < catalogue.size(); i++)
		{
			// find the market id
			if (catalogue.get(i).getMarketId().equals(marketId))
			{
				// store the market name
				marketName = catalogue.get(i).getMarketName();
				startTimeMS = catalogue.get(0).getEvent().getOpenDate()
						.getTime();
				// get our mapping from runner id to runner name
				List<RunnerCatalog> runnerCata = catalogue.get(i).getRunners();
				for (int j = 0; j < runnerCata.size(); j++)
				{
					runnerIds.put(runnerCata.get(j).getSelectionId(),
							runnerCata.get(j).getRunnerName());

					//Select a runner, anything but the draw
					if (!runnerCata.get(j).getRunnerName()
							.equalsIgnoreCase("the draw")
							&& selectedRunner == null)
					{
						selectedRunner = runnerCata.get(j).getRunnerName();
						System.out.println("Selected runner: " + selectedRunner);
						formattedCollectedData.add("Selected runner: "
								+ selectedRunner + ".\n");
					}
				}
				break;
			}
		}
		gameName = catalogue.get(0).getEvent().getName();
		collectedData.add(gameName + ". START DATE: "
				+ catalogue.get(0).getEvent().getOpenDate() + ". Game ID: "
				+ gameId + ". Market NAME: " + marketName + ". Market ID: "
				+ marketId + ".\n");
		formattedCollectedData.add(collectedData.get(0));
	}

	public long getStartDelay()
	{
		// if started
		if ((startTimeMS - System.currentTimeMillis()) < 0)
		{
			System.out.println("STARTING NOW!");
			return 0;
		}
		System.out.println("WAITING FOR "
				+ (startTimeMS - System.currentTimeMillis()) + "MS");
		return startTimeMS - System.currentTimeMillis();
	}

	@Override
	public void run()
	{
		if(mode==simplePrintMode)
		{
			printSingleRunnerProbabilityData();
		}
		else if(mode==simpleRecordMode)
		{
			collectSingleRunnerProbabilityData();
		}
		else if(mode==recordAllMode)
		{
			collectAllRunnersAllData();
		}
	}
	
	private void printSingleRunnerProbabilityData()
	{
		double workingBack = Integer.MIN_VALUE;
		double workingLay = Integer.MAX_VALUE;
		
		if (!betFair.getMarketBook(marketId).get(0).getStatus().equals("CLOSED"))
		{
			//Time stamp
			builder.append(System.currentTimeMillis() + ",");
			
			//Iterate through the runners
			for (Runner individual : runners)
			{
				// if we get to our tracked runner
				if (runnerIds.get(individual.getSelectionId()).equalsIgnoreCase(selectedRunner))
				{
					// if there's still backs and lays available
					if (individual.getEx().getAvailableToBack().size() > 0 && individual.getEx().getAvailableToLay().size() > 0)
					{
						//for each individual back option
						for(PriceSize backOption: individual.getEx().getAvailableToBack())
						{
							//find the biggest back value
							if(backOption.getPrice() > workingBack)
							{
								workingBack = backOption.getPrice();
							}
						}
						//for each individual lay option
						for(PriceSize layOption: individual.getEx().getAvailableToLay())
						{
							//find the smallest lay value
							if(layOption.getPrice() < workingLay)
							{
								workingLay = layOption.getPrice();
							}
						}
						builder.append((workingBack + workingLay)
								/ 2);
					}
					else
					{
						builder.append((" "));
					}
					System.out.println(builder.toString());
					builder.setLength(0);
				}
			}
		}
	}

	// Generates 2 collections, detailed info and stripped TODO refactor this to
	// be readable...
	private void collectSingleRunnerProbabilityData()
	{
		collectAllRunnersAllData();
		double workingBack = Integer.MIN_VALUE;
		double workingLay = Integer.MAX_VALUE;

		tempData = betFair.getMarketBook(marketId);
		runners = tempData.get(0).getRunners();

		if (!tempData.get(0).getStatus().equals("CLOSED"))
		{
			// add timestamp, next up is probability data, highest back+lowest
			// lay/2
			formattedCollectedData.add(System.currentTimeMillis() + ",");
			for (Runner individual : runners)
			{
				// if we get to our tracked runner
				if (runnerIds.get(individual.getSelectionId())
						.equalsIgnoreCase(selectedRunner))
				{
					// get best back, get best lay, get average
					if (individual.getEx().getAvailableToBack().size() > 0
							&& individual.getEx().getAvailableToLay().size() > 0)
					{
						// all is good! gogo
						for (int i = 0; i < individual.getEx()
								.getAvailableToBack().size(); i++)
						{
							// get values
							if (individual.getEx().getAvailableToBack().get(i)
									.getPrice() > workingBack)
								workingBack = individual.getEx()
										.getAvailableToBack().get(i).getPrice();
						}
						for (int i = 0; i < individual.getEx()
								.getAvailableToLay().size(); i++)
						{
							if (individual.getEx().getAvailableToLay().get(i)
									.getPrice() < workingLay)
								workingLay = individual.getEx()
										.getAvailableToLay().get(i).getPrice();
						}
						formattedCollectedData.add((workingBack + workingLay)
								/ 2 + "\n");
					}
					else
					{
						formattedCollectedData.add("\n");
					}
				}
			}
			System.out.println("Simple Iteration: " + (counter - 1)
					+ " complete.");
			System.out
					.println("Added: "
							+ formattedCollectedData.get(formattedCollectedData
									.size() - 1));
		}
		else
		{
			this.cancel();
			writeToFile(".csv");
			System.out.println("Finished, total number of iterations: "
					+ (counter - 1));
		}
	}

	private void collectAllRunnersAllData()
	{
		tempData = betFair.getMarketBook(marketId);
		runners = tempData.get(0).getRunners();

		if (!tempData.get(0).getStatus().equals("CLOSED"))
		{
			collectedData.add("ENTRY: " + counter + "\n");
			collectedData.add("\tTIME: " + LocalDateTime.now().toString()
					+ "\n");

			for (Runner individual : runners)
			{
				collectedData.add("\t\tRUNNER: "
						+ runnerIds.get(individual.getSelectionId()) + "\n");

				for (int i = 0; i < individual.getEx().getAvailableToBack()
						.size(); i++)
				{
					collectedData.add("\t\t\tBACK: (PRICE:"
							+ individual.getEx().getAvailableToBack().get(i)
									.getPrice()
							+ ",SIZE:"
							+ individual.getEx().getAvailableToBack().get(i)
									.getSize() + ")\n");
				}
				for (int i = 0; i < individual.getEx().getAvailableToLay()
						.size(); i++)
				{
					collectedData.add("\t\t\tLAY: (PRICE:"
							+ individual.getEx().getAvailableToLay().get(i)
									.getPrice()
							+ ",SIZE:"
							+ individual.getEx().getAvailableToLay().get(i)
									.getSize() + ")\n");
				}
			}
			System.out.println("Iteration: " + counter + " complete.");
			counter++;
		}
		else
		{
			this.cancel();
			writeToFile(".txt");
			System.out.println("Finished, total number of iterations: "
					+ counter);
		}
	}

	private void writeToFile(String type)
	{
		BufferedWriter outputWriter;
		try
		{
			outputWriter = new BufferedWriter(new FileWriter("./logs/gamelogs/"
					+ gameName + type));
			if (type.equalsIgnoreCase(".txt"))
			{
				for (int i = 0; i < collectedData.size(); i++)
				{

					outputWriter.write(collectedData.get(i));
				}
			}
			else
			{
				for (int i = 0; i < formattedCollectedData.size(); i++)
				{

					outputWriter.write(formattedCollectedData.get(i));
				}
			}
			outputWriter.flush();
			outputWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}