import java.util.List;
import java.util.Timer;

import views.TextFrontEnd;
import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.MarketBook;
import betfairUtils.MarketFilter;
import model.BetFairCore;
import model.SimpleGameRecorder;
import Exceptions.CryptoException;

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
		SimpleGameRecorder x = new SimpleGameRecorder(core,"27357665","1.117137832",1);
		Timer time = new Timer();
		time.schedule(x, x.getStartDelay(),5000);
		//time.s
		//x.
		//time.sc
		
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
//				//core.getGames("1");
//				//core.getMarketCatalogue("27359034");
//				//core.getMarketCatalogue("27359028");
//				//List<MarketBook> x = core.getMarketBook("1.117137832");
//			}
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