import model.Core;

public class Main
{
	public static void main(String[] args)
	{
		Core betFair = new Core();

		try
		{
			betFair.login(args[0], args[1]);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
