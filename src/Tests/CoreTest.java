package Tests;

import java.io.IOException;

import javax.crypto.BadPaddingException;

import model.BetFairCore;

import org.junit.Test;

import betfairUtils.LoginResponse;
import Exceptions.CryptoException;

public class CoreTest
{
	//TODO look into before and after annotations. might only need 1 set of resources.
	
	@Test
	public void validLoginTest() throws Exception
	{
		BetFairCore betFair = new BetFairCore(true);
		LoginResponse resp = betFair.login("0ocwto0", "2014Project", "cracker");
	}
}
