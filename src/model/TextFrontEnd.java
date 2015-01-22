package model;

import java.util.List;
import java.util.Scanner;

public class TextFrontEnd
{
	Scanner userInput;
	SimpleBetfair betFair;

	public TextFrontEnd()
	{
		betFair = new SimpleBetfair();
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

		if (loginPrompt())
		{
			if (sportPrompt())
			{
				System.out.println("next stage");
			}
		}
		// System.out.println("Options: \n\t1. Get Sports list\n2. ");
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
				String response = betFair.login(details[0], details[1], details[2]);
				if (response.equalsIgnoreCase("success"))
					return true;
			}
		}
	}

	private boolean sportPrompt()
	{
		while (true)
		{
			System.out.println("Options: \n\t1. Get Sports list\n\t2. Get games for selected sport");

			int input = userInput.nextInt();

			switch (input)
			{
				case 1:
					List<String> results = betFair.getSportSupportedSportList();
					for (String resultItem : results)
						System.out.println(resultItem);
					break;
				case 2:
					System.out.println("We know the selected sport so proceed");
					// so we print out our games
				default:
					break;
			}
		}
	}

}
