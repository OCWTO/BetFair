import model.Core;

public class Main
{
	public static void main(String[] args)
	{
		Core betFair = new Core();

		try
		{
			betFair.login(args[0], args[1]);
			System.out.println("------");
			betFair.getEvents();
			System.out.println("------");
			betFair.getMarketCatalogue();
			System.out.println("------");
			betFair.getMarketBook();
			// betfair.getmarketbook
			// going to hard code for now
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
