package views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;

import model.BetFairGameObject;
import model.BetFairMarketObject;
import model.BetFairSportObject;
import model.GameRecorder;
import model.SimpleBetFairCore;
import exceptions.CryptoException;

//TODO provide exit points and opportunity to go back
/**
 * Text based UI provided to allow users to track games & their markets.
 * 
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

		// Prompt for login details
		if (loginPrompt())
		{
			// If successful login then prompt for selected sport
			sportId = sportPrompt().getId();

			// If sport successfully selected then prompt for game
			gameId = gamePrompt(sportId);

			// If game successfully selected then prompt for market.
			marketId = marketPrompt(gameId);

			
			//THIS CODE starts a recorder so we want to do the same sort of stuff except observe it too
			recorder = new GameRecorder(betFair.getBetFair(), marketId);
			System.out.println(marketId.get(0));
			Timer timer = new Timer();
			timer.schedule(recorder, recorder.getStartDelayInMS(), 5000);
			
			
			//timer runs on its own thread so i can do whatever after
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
		List<BetFairMarketObject> gameMarkets = betFair
				.getMarketsForGame(gameId);
		BetFairMarketObject selectedMarket;
		Set<String> selectedMarkets = new HashSet<String>();

		System.out.println("MARKETS");
		for (int i = 0; i < gameMarkets.size(); i++)
		{
			System.out.println("NO: " + i + gameMarkets.get(i));

		}
		while (true)
		{
			System.out
					.println("Pick a market you want to record\n\tSELECT 'NUMBER'\n\tEnter 'DONE' to continue");
			inputLine = userInput.nextLine();

			if (inputLine.equalsIgnoreCase("done"))
			{
				List<String> ret = new ArrayList<String>();
				ret.addAll(selectedMarkets);
				return ret;
			}
			else
			{
				inputTokens = inputLine.split(" ");
				if (inputTokens.length == 2
						&& inputTokens[0].equalsIgnoreCase("select"))
				{
					selectedMarket = gameMarkets.get(Integer
							.parseInt(inputTokens[inputTokens.length - 1]));
					System.out.println("Adding market: "
							+ selectedMarket.getName() + " to list");
					selectedMarkets.add(gameId + ","
							+ selectedMarket.getId());
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
	// TODO filter out general markets, look only for those with v, check first
	// 10
	// if first 10 have it then next10 etc or change 10 for x
	private String gamePrompt(String sportId)
	{
		//sportId = "101153190";
		String inputLine;
		String[] inputTokens;
		List<BetFairGameObject> gameList = betFair.getGameListForSport(sportId);
		BetFairGameObject selectedGame;

		System.out.println("GAME LIST");
		for (int i = 0; i < gameList.size(); i++)
		{
			System.out.println("NO: " + i + " " + gameList.get(i)); // instead
																	// of
																	// another
			// for i'm just
			// printing data too
		}

		while (true)
		{
			System.out
					.println("Pick a game you want to record\n\tSELECT 'NUMBER'");
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split(" ");
			if (inputTokens.length == 2)
			{
				selectedGame = gameList.get(Integer
						.parseInt(inputTokens[inputTokens.length - 1]));
				System.out.println("Selected game: " + selectedGame.getName());
				return selectedGame.getId();
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
			System.out
					.println("Please enter login details in form of : 'USERNAME-PASSWORD-FILEPASSWORD'");
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split("-");

			if (inputTokens.length == 3)
			{
				String response;
				try
				{
					response = betFair.login(inputTokens[0], inputTokens[1],
							inputTokens[2]);
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
					System.err
							.println("Issue with the file password. Please try again.");
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
	private BetFairSportObject sportPrompt()
	{
		String inputLine;
		String[] inputTokens;
		List<BetFairSportObject> results = betFair.getSupportedSportList();

		System.out.println("SPORT LIST\nNUM\tNAME\t\tID");

		for (int i = 0; i < results.size(); i++)
		{
			System.out.println(i + " " + results.get(i));
		}

		while (true)
		{
			System.out
					.println("Options: \n\t\"SELECT 'NUMBER'\". Select the sport with the given number");

			inputLine = userInput.nextLine();
			inputTokens = inputLine.split(" ");

			if (inputTokens[0].equalsIgnoreCase("SELECT")
					&& inputTokens.length == 2)
			{
				// TODO add boundary checking
				BetFairSportObject selectedSportObject = results.get(Integer
						.parseInt(inputTokens[1]));
				System.out.println("Selected sport: "
						+ selectedSportObject.getName());
				return selectedSportObject;
			}
			else
			{
				System.err.println("No valid keyword entered. Try again.");
			}
		}
	}
}