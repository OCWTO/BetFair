import java.io.File;

import views.TextFrontEnd;
import model.DataAnalysis;
import model.TestFile;

/**
 * Class containing main method which runs the whole program
 * @author Craig Thomson
 *
 */
//TODO convert to javafx
//TODO work on report
//TODO connect front end
public class Main
{
	//TODO test out marketprojection? parameters to see if the maps are necessary, see what data is available whilst getting market books
	//TODO design gui
	//TODO record all markets at every tick and their pricematched
	
	//TODO record all json object requests so reflection testing can be done later
	
	//TODO check that theres a v in the name, to filter out league wide markets I don't care about.
	//print out names are in home vs away, not too relevant but maybe display it in gui
	public static void main(String[] args)
	{
//		TextFrontEnd textUi = new TextFrontEnd(false);
//		textUi.start();
		TestFile x = new TestFile(new File("C:\\Users\\Craig\\Desktop\\Workspace\\BetFair\\logs\\gamelogs\\Hyde v Stockport\\rawjson.txt"));
		DataAnalysis x1 = new DataAnalysis(x);
		x1.start();
	}
}