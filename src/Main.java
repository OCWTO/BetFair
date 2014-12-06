import model.Core;

public class Main
{
	public static void main(String[] args)
	{
		Core betFair = new Core();

		try
		{
			betFair.login(args[0], args[1]);
			betFair.getMarketCatalogue();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
