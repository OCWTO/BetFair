import java.util.List;

import Exceptions.CryptoException;
import model.BetFairCore;
import model.SimpleBetFairCore;
import javafx.application.Application;
import views.LoginView;
import views.LoginView2;
import views.TextFrontEnd;

public class Main
{
	// public static void main(String[] args)
	// {
	// BetFairCore betFair = new BetFairCore();
	//
	// try
	// {
	// betFair.login(args[0], args[1], args[2]);
	//
	// System.out.println("------");
	//
	// // EventType, DateTo
	// betFair.getGames("1");
	//
	// System.out.println("------");
	//
	// // EventType, MarketId, DateTo
	// // betFair.getMarketCatalogue("1","27319782",31);
	//
	// System.out.println("------");
	//
	// // Selected market book
	// // betFair.getMarketBook("1.116616456");
	// // System.out.println("success");
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }

	public static void main(String[] args)
	{
		//TextFrontEnd ui = new TextFrontEnd(true);
		
		
		BetFairCore core = new BetFairCore(true);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
			try
			{
				core.getMarketBook("1.117170997");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (CryptoException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		List<String> ls = core.getGameListForSport("1");
//		
//		for(int i = 0; i < ls.size();i++)
//		{
//			System.out.println(ls.get(i));
//		}
		
		//LoginView2 view = new LoginView2();
		//Application view = new LoginView();
		//view.launch(args);
		//view.launch(args);
	}
}
