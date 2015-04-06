
import java.io.File;

import views.LoginView;
import views.TextFrontEnd;
import model.DataAnalysis;
import model.ProgramOptions;
import model.TestFile;

/**
 * Class containing main method which runs the whole program
 * @author Craig Thomson
 *
 */
public class Main
{
	//Start method 1. This starts the GUI.
	public static void main(String[] args)
	{
		LoginView gui = new LoginView(new ProgramOptions());
	}
	
	//Start method 2. This bypasses the GUI and quickly runs the back end on the given test file.
//	public static void main(String[] args)
//	{
//		TestFile x = new TestFile(new File("./logs/gamelogs/Netherlands v Spain/rawjson.txt")); //test file 1 PREDICTS CORRECTLY
//		//TestFile x = new TestFile(new File("./logs/gamelogs/Italy v England/rawjson.txt")); //test file 2 PREDICTS ONE EXTRA GOAL
//		//TestFile x = new TestFile(new File("./logs/gamelogs/Chelsea v Stoke/rawjson.txt")); //test file 3  PREDICTS CORRECTLY
//		//TestFile x = new TestFile(new File("./logs/gamelogs/Netherlands v Turkey/rawjson.txt")); //test file 4 PREDICTS CORRECTLY
//		//TestFile x = new TestFile(new File("./logs/gamelogs/Everton v Southampton/rawjson.txt")); //test file 5 PREDICTS CORRECTLY
//		
//		DataAnalysis x1 = new DataAnalysis(x);
//		x1.start();
//	}
	
	//Start method 3. This starts the textual back end that's been used for setting up games to record
//	public static void main(String[] args)
//	{
//		TextFrontEnd textUi = new TextFrontEnd(false);
//		textUi.start();
//	}
}