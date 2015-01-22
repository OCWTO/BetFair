package Tests;

import model.BetFairCore;

import org.junit.Test;

public class CoreTest
{
	//TODO look into before and after annotations. might only need 1 set of resources.
	
	@Test
	public void testLoginStabilityOnBadDetails()
	{
		BetFairCore betFair = new BetFairCore();
		betFair.login("a", "b", "c");
	}
}
