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
		//time.sc
		
	}


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
//				//core.listEventTypes(new MarketFilter());
//				core.getGames("1");
//				core.getMarketCatalogue("27357665");
//				List<MarketBook> x = core.getMarketBook("1.117137832");
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