package model;

import java.util.Scanner;

public class TextFrontEnd
{
	Scanner userInput;

	public TextFrontEnd()
	{
		userInput = new Scanner(System.in);
		prompt();
	}

	private void prompt()
	{

		boolean sportSelected = false;

		// If a sport is selected
		if (sportPrompt(sportSelected))
		{
			System.out.println("next stage");
		}

		// System.out.println("Options: \n\t1. Get Sports list\n2. ");
	}

	// TODO make it work with strings so it doesnt break...
	private boolean sportPrompt(boolean selected)
	{
		while (!selected)
		{
			System.out
					.println("Options: \n\t1. Get Sports list\n\t2. Get games for selected sport");

			int input = userInput.nextInt();

			switch (input)
			{
				case 1:
					System.out.println("sport list call");
					// Call get sport list
					break;
				case 2:
					System.out.println("We know the selected sport so proceed");
					selected = true;
					return selected;
				default:
					break;
			}
		}
		return true;
	}

}
