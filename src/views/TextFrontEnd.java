package views;

import java.util.List;
import java.util.Scanner;

import Exceptions.CryptoException;
import model.SimpleBetFairCore;
import model.SimpleGameRecorder;

public class TextFrontEnd
{
	private Scanner userInput;
	private SimpleBetFairCore betFair;
	private SimpleGameRecorder recorder;
	
	public TextFrontEnd(boolean debug)
	{
		betFair = new SimpleBetFairCore(debug);
		userInput = new Scanner(System.in);
	}
	
	public void start()
	{
		prompt();
	}

	private void prompt()
	{
		String sportId;
		String gameId;
		String marketId;
		int recordMode;
		
		//Prompt for login details
		if (loginPrompt())
		{
			//If successful login then prompt for selected sport
			sportId = sportPrompt();			
			
			//If sport successfully selected then prompt for game
			gameId = gamePrompt(sportId);		
			
			//If game successfully selected then prompt for market.
			marketId = marketPrompt(gameId);		
			
			recordMode = recorderPrompt();
			//now we call
			recorder = new SimpleGameRecorder(betFair.getBetFair(), gameId, marketId,1);
			
		}
	}


	private int recorderPrompt()
	{
		System.out.println("What recorder mode do you want?\n\t1. Simple Print\n\t2. Simple Record\n\t3. Record all");
		
		return userInput.nextInt();
	}

	private String marketPrompt(String gameId)
	{
		String inputLine;
		String[] inputTokens;
		List<String> gameMarkets = betFair.getMarketsForGame(gameId);
		String[] selectedMarketRow;
		
		System.out.println("MARKETS");
		for(int i = 0; i < gameMarkets.size(); i++)
		{
			System.out.println("NO: " + gameMarkets.get(i));

		}
		while(true)
		{
			System.out.println("Pick a market you want to record\n\tSELECT 'NUMBER'");
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split(" ");
			selectedMarketRow = gameMarkets.get(Integer.parseInt(inputTokens[inputTokens.length-1])).split(",");
			
			return selectedMarketRow[selectedMarketRow.length-1];
		}
	}

	private String gamePrompt(String sportId)
	{
		String inputLine;
		String[] inputTokens;		
		List<String> gameList = betFair.getGameListForSport(sportId);	
		String[] selectedGameTokens;
		
		System.out.println("GAME LIST");
		for(int i = 0; i < gameList.size(); i++)
		{
			System.out.println("NO: " + gameList.get(i));	//instead of another for i'm just printing data too
		}

		while(true)
		{
			System.out.println("Pick a game you want to record\n\tSELECT 'NUMBER'");
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split(" ");
			selectedGameTokens = gameList.get(Integer.parseInt(inputTokens[inputTokens.length-1])).split(",");

			return selectedGameTokens[selectedGameTokens.length-1];
		}
	}

	private boolean loginPrompt()
	{
		String inputLine;
		String[] inputTokens;
		
		while (true)
		{
			System.out.println("Please enter login details in form of : 'USERNAME-PASSWORD-FILEPASSWORD'");
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split("-");

			if (inputTokens.length == 3)
			{
				String response;
				try
				{
					response = betFair.login(inputTokens[0], inputTokens[1], inputTokens[2]);
					if (response.equalsIgnoreCase("success"))
					{
						System.out.println("Successful login!");
						return true;
					}
					else
					{
						System.err.println(response);
					}
				}
				catch (CryptoException e)
				{
					System.out.println("Unsuccessful login");
					System.err.println("Issue with the file password. Please try again.");
				}
			}
			else
			{
				System.err.println("Invalid format. Try again.");
			}
		}
	}
	
	private String sportPrompt()
	{
		String inputLine;
		String[] inputTokens;
		List<String> results = betFair.getSupportedSportList();
		
		System.out.println("SPORT LIST\nNUM\tNAME\t\tID");
		for(String resultItem: results)
			System.out.println(resultItem);
		
		while (true)
		{
			System.out.println("Options: \n\t\"SELECT 'NUMBER'\". Select the sport with the given number");
			
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split(" ");
			
			if(inputTokens[0].equalsIgnoreCase("SELECT"))
			{
				//get the number token, get the sport in that index, split it and get the last number (the betfair id)
				String[] selectedTokens = (results.get(Integer.parseInt(inputTokens[inputTokens.length-1]))).split(" ");
				return selectedTokens[selectedTokens.length-1];
			}
			else
			{
				System.err.println("No valid keyword entered. Try again.");
			}
		}
	}
}