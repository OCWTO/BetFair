import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import enums.BetFairMarketStatus;
import enums.MarketProjection;
import exceptions.CryptoException;
import views.TextFrontEnd;
import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.MarketFilter;
import betfairUtils.EventTypeResultComparator;
import model.BetFairCore;
import model.SimpleBetFairCore;
import model.SimpleGameRecorder;

/**
 * Class containing main method which runs the whole program
 * @author Craig Thomson
 *
 */
public class Main
{
	//TODO test out marketprojection? parameters to see if the maps are necessary, see what data is available whilst getting market books
	//TODO design gui
	//TODO record all markets at every tick and their pricematched
	
	//TODO record all json object requests so reflection testing can be done later
	
	public static void main2(String[] args)
	{
		BetFairCore core = new BetFairCore(true);
		core.login("0ocwto0", "2014Project", "project");

		
		core.getGames("1");
		//List<String> temp = new ArrayList<String>();
		//temp.add("1.117517592");
		//List<MarketBook> t2 = core.getMarketBook("1.117517592");
		//System.out.println(BetFairMarket.CLOSED_MARKET.toString());
		//System.out.println(t2.get(0).getStatus().equalsIgnoreCase(BetFairMarket.CLOSED_MARKET.toString()));
		//SimpleGameRecorder x = new SimpleGameRecorder(core,"27357665",temp,1);
		//Timer time = new Timer();
		//time.schedule(x, x.getStartDelay(),5000);		
	}

	//TODO check that theres a v in the name, to filter out league wide markets I don't care about.
	//print out names are in home vs away, not too relevant but maybe display it in gui
	public static void main(String[] args)
	{
		TextFrontEnd textUi = new TextFrontEnd(true);
		textUi.start();

		//Plan for both classes will be to start a timer and just sit waiting for events
	}
}