package views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;

import model.GameRecorder;
import model.SimpleBetFairCore;
import exceptions.CryptoException;

//TODO provide exit points and opportunity to go back
/**
 * Text based UI provided to allow users to track games & their markets.
 * @author Craig Thomson
 *
 */
public class TextFrontEnd
{
	private Scanner userInput;
	private SimpleBetFairCore betFair;
	private GameRecorder recorder;
	
	/**
	 * 
	 * @param debug
	 */
	public TextFrontEnd(boolean debug)
	{
		betFair = new SimpleBetFairCore(debug);
		userInput = new Scanner(System.in);
	}
	
	/**
	 * 
	 */
	public void start()
	{
		prompt();
	}

	/**
	 * 
	 */
	private void prompt()
	{
		String sportId;
		String gameId;
		List<String> marketId;

		//Prompt for login details
		if (loginPrompt())
		{
			//If successful login then prompt for selected sport
			sportId = sportPrompt();			
			
			//If sport successfully selected then prompt for game
			gameId = gamePrompt(sportId);		
			
			//If game successfully selected then prompt for market.
			marketId = marketPrompt(gameId);		
			
			//recordMode = recorderPrompt();
			//now we call
			recorder = new GameRecorder(betFair.getBetFair(), marketId);
			System.out.println(marketId.get(0));
			Timer timer = new Timer();
			timer.schedule(recorder, recorder.getStartDelay(),5000);
			//TODO let recordmode be an enum
		}
	}
	
	/**
	 * 
	 * @param gameId
	 * @return
	 */
	private List<String> marketPrompt(String gameId)
	{
		String inputLine;
		String[] inputTokens;
		List<String> gameMarkets = betFair.getMarketsForGame(gameId);
		String[] selectedMarketRow;
		Set<String> selectedMarket = new HashSet<String>();

		System.out.println("MARKETS");
		for(int i = 0; i < gameMarkets.size(); i++)
		{
			System.out.println("NO: " + gameMarkets.get(i));

		}
		while(true)
		{
			System.out.println("Pick a market you want to record\n\tSELECT 'NUMBER'\n\tEnter 'DONE' to continue");
			inputLine = userInput.nextLine();
			
			if(inputLine.equalsIgnoreCase("done"))
			{
				List<String> ret = new ArrayList<String>();
				ret.addAll(selectedMarket);
				return ret;
			}
			else
			{
				inputTokens = inputLine.split(" ");
				if(inputTokens.length == 2 && inputTokens[0].equalsIgnoreCase("select"))
				{
					selectedMarketRow = gameMarkets.get(Integer.parseInt(inputTokens[inputTokens.length-1])).split(",");
					System.out.println("Adding market: " + selectedMarketRow[selectedMarketRow.length-2] + " to list");
					selectedMarket.add(gameId +","+selectedMarketRow[selectedMarketRow.length-1]);
				}
				else
				{
					System.out.println("bad syntax try again");
				}
			}
		}
	}

	/**
	 * 
	 * @param sportId
	 * @return
	 */
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
			if(inputTokens.length == 2)
			{
				selectedGameTokens = gameList.get(Integer.parseInt(inputTokens[inputTokens.length-1])).split(",");
				System.out.println("Selected game: " + selectedGameTokens[1]);
				return selectedGameTokens[selectedGameTokens.length-1];
			}
			else
				System.out.println("Invalid number of tokens. Try again!");
		}
	}

	/**
	 * 
	 * @return
	 */
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
	
	/**
	 * 
	 * @return
	 */
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
			
			if(inputTokens[0].equalsIgnoreCase("SELECT") && inputTokens.length == 2)
			{
				//get the number token, get the sport in that index, split it and get the last number (the betfair id)
				String[] selectedTokens = (results.get(Integer.parseInt(inputTokens[inputTokens.length-1]))).split(" ");
				System.out.println("Selected sport: " + selectedTokens[1]);
				return selectedTokens[selectedTokens.length-1];
			}
			else
			{
				System.err.println("No valid keyword entered. Try again.");
			}
		}
	}
}