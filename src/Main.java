import model.BetFairCore;
import Exceptions.CryptoException;

public class Main
{
	public static void main(String[] args)
	{
		// TextFrontEnd ui = new TextFrontEnd(true);
		BetFairCore core = new BetFairCore(true);
		try
		{
			core.login("0ocwto0", "2014Project", "project");
			try
			{
				core.getGames("1");
				core.getMarketCatalogue("27355661");
				core.getMarketBook("1.117097453");
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