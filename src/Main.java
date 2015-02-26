import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import exceptions.CryptoException;
import views.TextFrontEnd;
import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.MarketFilter;
import betfairUtils.EventTypeResultComparator;
import model.BetFairCore;
import model.SimpleGameRecorder;

public class Main
{
	//TODO design gui
	//TODO add multi threaded support
	//TODO modify recorder to record all markets
	public static void main2(String[] args)
	{
		BetFairCore core = new BetFairCore(false);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
		}
		catch (CryptoException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> temp = new ArrayList<String>();
		temp.add("1.117137832");
		SimpleGameRecorder x = new SimpleGameRecorder(core,"27357665",temp,1);
		Timer time = new Timer();
		time.schedule(x, x.getStartDelay(),5000);		
	}

	//TODO check that theres a v in the name, to filter out league wide markets I don't care about.
	//print out names are in home vs away, not too relevant but maybe display it in gui
	public static void main(String[] args)
	{
		TextFrontEnd textUi = new TextFrontEnd(false);
		textUi.start();
		
		
//		BetFairCore core = new BetFairCore(true);
//		try
//		{
//			core.login("0ocwto0", "2014Project", "project");
//			try
//			{
//				core.getGames("1");
//				
//				//Roma vs feyenoord 6pm tomorrow
//				core.getMarketCatalogue("27327273");
//				
//				
//				
//				List<String> marketList = new ArrayList<String>();
//				marketList.add("1.116734120");
//				//marketList.add("1.117352806");
//				//marketList.add("1.117192958");
//				List<MarketBook> x = core.getMarketBook(marketList);	//goes in as 1,2,3,4. comes out as 4.3.2.1
//				//System.out.println(x.size());
//				//System.out.println(x.get(0).getMarketId());
//				//System.out.println(x.get(1).getMarketId());	//so if data for multiple markets is requested
//				//System.out.println(x.get(2).getMarketId()); //i need to get an items marketid and compare it against
//				//System.out.println(x.get(3).getMarketId()); //what my initial lists have to know where to put it
//			}												//i could store the order for less comparisons
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		catch (CryptoException e)
//		{
//			e.printStackTrace();
//		}
	}
}