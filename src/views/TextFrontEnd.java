package views;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Exceptions.CryptoException;
import model.GameRecorderManager;
import model.SimpleBetFairCore;

public class TextFrontEnd
{
	Scanner userInput;
	SimpleBetFairCore betFair;
	GameRecorderManager recorder;
//	String selectedID;
//	String gameID;
//	String sportID;
//	String gameId;
//	List<String> markets = new ArrayList<String>();
	
	public TextFrontEnd(boolean debug)
	{
		betFair = new SimpleBetFairCore(debug);
		recorder = new GameRecorderManager(betFair.getBetFair());
		userInput = new Scanner(System.in);
		prompt();
	}

	private void prompt()
	{
		// prompt for login
		// print sport options
		// prompt for sport
		// print game options?
		// prompt for game
		String sportId;
		String gameId;
		List<String> markets = new ArrayList<String>();
		
		
		if (loginPrompt())
		{
			sportId = sportPrompt();			//Get the sport they want
			gameId = gamePrompt(sportId);		//Get the game in the sport they want
			markets = marketPrompt(gameId);		//Get the markets in the game they want
			recorder.assignTask(markets);
		}
	}

	//TODO fix later
	private List<String> marketPrompt(String gameId)
	{
		List<String> markets = betFair.getMarketsForGame(gameId);
		List<String> marketIds = new ArrayList<String>();
		List<String> trackedMarkets = new ArrayList<String>();
		String[] temp;
		System.out.println(markets.size());
		for(int i = 0; i < markets.size(); i++)
		{
			System.out.println(markets.get(i));
			temp = markets.get(i).split(",");
			marketIds.add(temp[temp.length-1]);
		}
		while(true)
		{
			System.out.println("Pick a market you want to record\n\tSELECT 'ID'");
			String response = userInput.nextLine();
			
			String[] temp2 = response.split(" ");
			trackedMarkets.add(marketIds.get(Integer.parseInt(temp2[temp2.length-1])));
			return trackedMarkets;
		}
	}

	private String gamePrompt(String sportId)
	{
		List<String> gameList = betFair.getGameListForSport(sportId);	
		List<String> parsedGameCode = new ArrayList<String>();
		String[] temp;
		
		for(int i = 0; i < gameList.size(); i++)
		{
			temp = gameList.get(i).split(",");		//finding codes
			parsedGameCode.add(temp[temp.length-1]);
			System.out.println(gameList.get(i));	//instead of another for i'm just printing data too
		}

		while(true)
		{
			System.out.println("Pick a game you want to record\n\tSELECT 'ID'");
			String response = userInput.nextLine();

			//TODO sanity checks (check valid val etc...
			String[] temp2 = response.split(" ");
			//System.out.println(parsedGameCode.get(Integer.parseInt(temp2[temp2.length-1])));
			return parsedGameCode.get(Integer.parseInt(temp2[temp2.length-1]));
			//return true;
		}
	}

	// TODO provide exits
	private boolean loginPrompt()
	{
		while (true)
		{
			System.out.println("Please enter login details in form of : 'USERNAME-PASSWORD-FILEPASSWORD'");
			String inputLine = userInput.nextLine();
			String[] details = inputLine.split("-");

			if (details.length == 3)
			{
				String response;
				try
				{
					response = betFair.login(details[0], details[1], details[2]);
					if (response.equalsIgnoreCase("success"))
						return true;
				}
				catch (CryptoException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private String sportPrompt()
	{
		List<String> results = new ArrayList<String>();
		
		while (true)
		{
			System.out.println("Options: \n\t\"LIST\". Get Sports list\n\t\"SELECT 'NUMBER'\". Select the sport with the given id");

			//int input = userInput.nextInt();
			String input = userInput.nextLine();
			
			String[] tokens = input.split(" ");
			
			if(tokens[0].equalsIgnoreCase("LIST"))
			{
				if(results.isEmpty())
					results = betFair.getSupportedSportList();
				for(String resultItem: results)
					System.out.println(resultItem);
			}
			else if(tokens[0].equalsIgnoreCase("SELECT"))
			{
				int index = Integer.parseInt(tokens[tokens.length-1]);
				//selected number (1 to 29)
				String[] selectedTokens = results.get(index).split(" ");
				return selectedTokens[selectedTokens.length-1];
			}
		}
	}
}