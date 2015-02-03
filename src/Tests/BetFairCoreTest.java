package Tests;

import static org.junit.Assert.*;
import model.BetFairCore;

import org.junit.Test;

import betfairUtils.LoginResponse;
import Exceptions.CryptoException;

public class BetFairCoreTest
{
	// Testing bad file password
	@Test
	public void testLoginBadFilePassword()
	{
		BetFairCore betFair = new BetFairCore(true);
		
		try
		{
			betFair.login("bad", "bad", "bad");
			fail("CryptoException expected in testLoginBadFilePassword()");
		}
		catch(CryptoException e)
		{
			System.out.println("testLoginBadFilePassword() pass!");
		}
	}

	// Testing bad username
	@Test
	public void testLoginBadUsername()
	{
		BetFairCore betFair = new BetFairCore(true);
		
		try
		{
			betFair.login("bad", "2014Project", "project");
			fail("CryptoException expected in testLoginBadUsername()");
		}
		catch(CryptoException e)
		{
			System.out.println("testLoginBadUsername() pass!");
		}
	}
	
	// Testing bad password
	@Test
	public void testLoginBadPassword()
	{
		BetFairCore betFair = new BetFairCore(true);
		
		try
		{
			betFair.login("0ocwto0", "bad", "project");
			fail("CryptoException expected in testLoginBadPassword()");
		}
		catch(CryptoException e)
		{
			System.out.println("testLoginBadPassword() pass!");
		}
	}
	
	//Test good login
	
	//Test no internet
}
