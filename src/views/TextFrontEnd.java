package views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import model.BetfairGameObject;
import model.BetfairMarketObject;
import model.BetfairSportObject;
import model.DataAnalysis;
import model.ISimpleBetFair;
import model.ProgramOptions;
import model.SimpleBetfair;
import exceptions.CryptoException;

/**
 * Text based UI provided to allow users to set up game recordings.
 * This is hard coded to use './certs/client-2048.p12' as the certificate file
 * It performs little to no validity checking on the input so it can crash if mistakes are made.
 * @author Craig Thomson
 *
 */
public class TextFrontEnd
{
	private Scanner userInput;
	private ISimpleBetFair betFair;
	private DataAnalysis recorder;

	/**
	 * 
	 * @param debug if true then json requests are printed to console as well as replies
	 */
	public TextFrontEnd(boolean debug)
	{
		betFair = new SimpleBetfair(debug);
		userInput = new Scanner(System.in);
	}

	/**
	 * Start prompting for required information to record markets
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
			sportId = sportPrompt().getSportId();

			// If sport successfully selected then prompt for game
			gameId = gamePrompt(sportId);

			// If game successfully selected then prompt for market.
			marketId = marketPrompt(gameId);

			
			//Create a programoptions object and start recording
			ProgramOptions options = new ProgramOptions();
			options.setUserDetails("0ocwto0", "2014Project", "project");
			options.addMarketIds(marketId);
			options.setEventId(gameId);
			options.setEventTypeId(sportId);
			options.addBetFair(betFair);
			
			
			recorder = new DataAnalysis(options);
			//Preset polling time of 5000ms
			recorder.start(5000);			
		}
	}

	/**
	 * 
	 * @param gameId The game id that markets will be displayed for
	 * @return
	 */
	private List<String> marketPrompt(String gameId)
	{
		String inputLine;
		String[] inputTokens;
		List<BetfairMarketObject> gameMarkets = betFair
				.getMarketsForGame(gameId);
		BetfairMarketObject selectedMarket;
		Set<String> selectedMarkets = new HashSet<String>();

		System.out.println("MARKETS");
		for (int i = 0; i < gameMarkets.size(); i++)
		{
			System.out.println("NO: " + i + " " + gameMarkets.get(i));

		}
		while (true)
		{
			System.out
					.println("Pick a market you want to record\n\tSELECT 'NUMBER'\n\tEnter 'DONE' to continue");
			inputLine = userInput.nextLine();

			//If done is pressed then it passes back the list of ids entered
			if (inputLine.equalsIgnoreCase("done"))
			{
				List<String> selectedMarketList = new ArrayList<String>();
				selectedMarketList.addAll(selectedMarkets);
				return selectedMarketList;
			}
			//Otherwise it looks for select commands.
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
					selectedMarkets.add(selectedMarket.getMarketId());
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
	 * @param sportId The sport id that you want to see a game list for
	 * @return The users selected game id
	 */
	private String gamePrompt(String sportId)
	{
		String inputLine;
		String[] inputTokens;
		List<BetfairGameObject> gameList = betFair.getGameListForSport(sportId);
		BetfairGameObject selectedGame;

		//Fairly readable output for the games available
		System.out.println("GAME LIST");
		for (int i = 0; i < gameList.size(); i++)
		{
			System.out.println("NO: " + i + " " + gameList.get(i)); 
		}

		while (true)
		{
			System.out
					.println("Pick a game you want to record\n\tSELECT 'NUMBER'");
			inputLine = userInput.nextLine();
			inputTokens = inputLine.split(" ");
			
			//Split on whitespace and if there's enough so that a "select x" command was entered
			if (inputTokens.length == 2)
			{
				selectedGame = gameList.get(Integer
						.parseInt(inputTokens[inputTokens.length - 1]));
				System.out.println("Selected game: " + selectedGame.getName());
				return selectedGame.getMarketId();
			}
			else
				System.out.println("Invalid number of tokens. Try again!");
		}
	}

	/**
	 * 
	 * @return true or false depending on if the login was successful
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
							inputTokens[2], new File("./certs/client-2048.p12"));
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
	private BetfairSportObject sportPrompt()
	{
		String inputLine;
		String[] inputTokens;
		List<BetfairSportObject> results = betFair.getSupportedSportList();

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
				BetfairSportObject selectedSportObject = results.get(Integer
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