package model;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
//need to check if started,
//start the thing, it will init if not already then has a method for adddata
//method for save (marketname)
//needs to recognise when to stop tracking

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
		//String gameId = manager.getGameId();
		List<String> storedMarketList = manager.getMarkets();
		
		//For each market that's going to be tracked
		for(String marketId : storedMarketList)
		{
			ArrayList<ArrayList<String>> marketData = new ArrayList<ArrayList<String>>();
			
			for(BetFairMarketObject receivedMarket : receivedMarketObjects)
			{
				if(receivedMarket.getId().equals(marketId))
				{
					ArrayList<String> individualMarketData = new ArrayList<String>();
					individualMarketData.add(receivedMarket.getMarketEvent().getName() + "_" + 
					receivedMarket.getName() + "_" + receivedMarket.getOpenDate().getTime());
					marketData.add(individualMarketData);
					
					for(RunnerCatalog runner : receivedMarket.getRunners())
					{
						ArrayList<String> runnerData = new ArrayList<String>();
						runnerData.add(individualMarketData.get(0) + "_" + runner.getRunnerName());
					}
				}
			}
			gameData.add(marketData);
		}
		System.out.println("IO " + gameData.get(0).get(0));
	}
}