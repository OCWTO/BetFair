import java.util.ArrayList;
import java.util.List;

import model.BetFairGameObject;
import model.BetFairMarketObject;
import model.ISimpleBetFair;
import model.SimpleBetFairCore;
import views.TextFrontEnd;

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
	
	public static void main2(String[] args)
	{
		ISimpleBetFair betFair = new SimpleBetFairCore(true);
		betFair.login("0ocwto0", "2014Project", "project");
		betFair.getSupportedSportList();
		List<BetFairGameObject> games = betFair.getGameListForSport("1");
//		List<String> gameIds = new ArrayList<String>();
//		for(int i = 0; i < games.size(); i++)
//		{
//			System.out.println(games.get(i).getId());
//			gameIds.add(games.get(i).getId());
//		}
//		
//		//get market list for game in in
//		List<String> marketIds = new ArrayList<String>();
//		List<BetFairMarketObject> marketList = betFair.getMarketsForGame(gameIds.get(0));
//		for(int i = 0; i < marketList.size(); i++)
//		{
//			marketIds.add(marketList.get(i).getId());
//		}
		
		//cost of this is 	5*no of markets
		//betFair.getMarketInformation(marketIds);
		
		
		
		//betFair.getMarketInformation(marketIds);
//		List<MarketCatalogue> catalogue = new ArrayList<MarketCatalogue>();
//		catalogue = betFair.getGames("1");
//		List<String> ids = new ArrayList<String>();
//		
//		for(int i = 0; i < catalogue.size(); i++)
//		{
//			System.out.println(catalogue.get(i).getMarketId());
//			ids.add(catalogue.get(i).getMarketId());
//		}
//		betFair.getMarketBook(ids);
		//core.getGames("101153190");
		//core.getMarketBook("1.01153190");
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