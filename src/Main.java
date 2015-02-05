import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import betfairUtils.MarketBook;
import betfairUtils.MarketCatalogue;
import model.BetFairCore;
import model.SimpleGameRecorder;
import Exceptions.CryptoException;

public class Main
{
	public static void main(String[] args)
	{
		BetFairCore core = new BetFairCore(true);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
		}
		catch (CryptoException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleGameRecorder x = new SimpleGameRecorder(core,"27360530","1.117183165");
		Timer time = new Timer();
		time.schedule(x, x.getStartDate(),5000);
	}


		public static void main2(String[] args)
		{
		BetFairCore core = new BetFairCore(true);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
			try
			{
				core.getGames("1");
				core.getMarketCatalogue("27360530");
				List<MarketBook> x = core.getMarketBook("1.117183165");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (CryptoException e)
		{
			e.printStackTrace();
		}
	}
}