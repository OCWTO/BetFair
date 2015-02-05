package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import Exceptions.CryptoException;
import betfairUtils.ExchangePrices;
import betfairUtils.MarketBook;
import betfairUtils.MarketCatalogue;
import betfairUtils.Runner;
import betfairUtils.RunnerCatalog;

public class SimpleGameRecorder extends TimerTask
{
	//TODO consider utilizing the timer class to set up timertasks at given times
	
	//take in betfair, gameid and marketid
	private BetFairCore betFair;
	private List<String> collectedData;
	//take in betfair
	
	private String gameId;
	private String marketId;
	private String marketName;
	private Map<String,String> runnerIds;
	private Date startDate;
	private List<MarketBook> tempData;
	private int counter;
	private List<Runner> runners;
	private String gameName;
	
	public SimpleGameRecorder(BetFairCore betFair, String gameId, String marketId)
	{
		this.betFair = betFair;
		this.gameId = gameId;
		this.marketId = marketId;
		counter = 1;
		collectedData = new ArrayList<String>();
		runnerIds = new HashMap<String,String>();
		tempData = new ArrayList<MarketBook>();
		runners = new ArrayList<Runner>();
		getInitValues();
	}
	
	private void getInitValues()
	{
		//get market catalogue, get runner names and map to runner id, put in initial value saying market, starttime, runners
		List<MarketCatalogue> catalogue = betFair.getMarketCatalogue(gameId);
		for(int i = 0; i < catalogue.size(); i++)
		{
			//find the market id
			if(catalogue.get(i).getMarketId().equals(marketId))
			{
				//store the market name
				marketName = catalogue.get(i).getMarketName();
				startDate = catalogue.get(0).getEvent().getOpenDate();
				//get our mapping from runner id to runner name
				List<RunnerCatalog> runnerCata = catalogue.get(i).getRunners();
				for(int j =0;j<runnerCata.size();j++)
				{
					runnerIds.put(Long.toString(runnerCata.get(j).getSelectionId()), runnerCata.get(j).getRunnerName());
				}
				break;
			}
		}
		gameName = catalogue.get(0).getEvent().getName();
		collectedData.add(gameName + ". START DATE: " + catalogue.get(0).getEvent().getOpenDate() + ". Game ID: " + gameId + ". Market NAME: " + marketName + ". Market ID: " + marketId + ".");
		System.out.println("setup");
	}
	
	public Date getStartDate()
	{
		return this.startDate;
	}
	
	@Override
	public void run()
	{
		tempData = betFair.getMarketBook(marketId);
		runners = tempData.get(0).getRunners();
		
		if(!tempData.get(0).getStatus().equals("CLOSED"))
		{
			collectedData.add("ENTRY: " + counter);
			collectedData.add("\tTIME: " + System.currentTimeMillis());
			
			for(Runner individual: runners)
			{
				collectedData.add("\t\tRUNNER: " + runnerIds.get(individual.getSelectionId()));
				

				for(int i=0;i<individual.getEx().getAvailableToBack().size(); i++)
				{
					collectedData.add("\t\t\tBACK: (PRICE:" + individual.getEx().getAvailableToBack().get(i).getPrice() + ",SIZE:" + individual.getEx().getAvailableToBack().get(i).getSize());
				}
				for(int i=0;i<individual.getEx().getAvailableToLay().size(); i++)
				{
					collectedData.add("\t\t\tLAY: (PRICE:" + individual.getEx().getAvailableToLay().get(i).getPrice() + ",SIZE:" + individual.getEx().getAvailableToLay().get(i).getSize());
				}
			}
			System.out.println("Interation: " + counter +" complete.");
			counter++;
		}
		else
		{
			this.cancel();
			writeToFile();
			System.out.println("DONE");
		}
	}

	private void writeToFile()
	{
		BufferedWriter outputWriter;
		try
		{
			outputWriter = new BufferedWriter(new FileWriter("./logs/"+gameName+".txt"));
			for (int i = 0; i < collectedData.size(); i++) 
			{

					outputWriter.write(collectedData.get(i));
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