
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import views.TextFrontEnd;
import model.ISimpleBetFair;
import model.SimpleBetFair;

import views.TextFrontEnd;

/**
 * Class containing main method which runs the whole program
 * @author Craig Thomson
 *
 */
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
		TextFrontEnd textUi = new TextFrontEnd(false);
		textUi.start();
		
		
		//TestFile x = new TestFile(new File("C:\\Users\\Craig\\Desktop\\Workspace\\BetFair\\logs\\gamelogs\\Netherlands v Turkey FULL\\rawjson.txt"));
		//DataAnalysis x1 = new DataAnalysis(x);
		//x1.start();
		
//		ISimpleBetFair x = new SimpleBetFair(true);
//		
//		x.login("0ocwto0", "2014Project", "project", new File("C:\\Users\\Craig\\Desktop\\Workspace\\BetFair\\certs\\client-2048.p12"));
//		x.getSupportedSportList();
//		x.getGameListForSport("1");
//		x.getMarketsForGame("27408709");
//		List<String> xxx = new ArrayList<String>();
//		xxx.add("1.118007208");
//		x.getMarketInformation(xxx);
	}
}