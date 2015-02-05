import java.util.Date;
import java.util.List;

import betfairUtils.MarketBook;
import model.BetFairCore;
import Exceptions.CryptoException;

public class Main
{
	public static void main(String[] args)
	{
		
		//For quick collector I need a gameid to get catalogue and a market id
		//with catalogue I can find start time and then with market id i know wha tto query until its closed
		
		//So get date from catalogue getevent and sleep in diff between then and now. Then while marketbook status is open query until its shut
		
		// TextFrontEnd ui = new TextFrontEnd(true);
		BetFairCore core = new BetFairCore(true);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
			try
			{
				core.getGames("1");
				core.getMarketCatalogue("27361306");
				
				core.getMarketCatalogue("27361306");
				long x = System.currentTimeMillis();
				core.getMarketBook("1.117192103");
				long y = System.currentTimeMillis();
				System.out.println(y-x);
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