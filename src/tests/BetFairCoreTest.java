package tests;

import static org.junit.Assert.*;
import model.BetFairCore;

import org.junit.Before;
import org.junit.Test;

import exceptions.CryptoException;
import betFairGSONClasses.LoginResponse;

public class BetFairCoreTest
{
	private static final boolean debug = true;
	private static final String userName = "0ocwto0";
	private static final String password = "2014Project";
	private static final String filePassword = "project";
	private BetFairCore betFair;
	
	@Before
	public void makeObject()
	{
		betFair = new BetFairCore(debug);
	}
	
	// Testing bad file password
	@Test
	public void testLoginBadFilePassword()
	{
		try
		{
			betFair.login(userName, password, filePassword + "A");
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
		try
		{
			betFair.login(userName + "A", password, filePassword);
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
		try
		{
			betFair.login(userName, password + "A", filePassword);
			fail("CryptoException expected in testLoginBadPassword()");
		}
		catch(CryptoException e)
		{
			System.out.println("testLoginBadPassword() pass!");
		}
	}
	
	@Test
	public void testLoginSuccess()
	{
		try
		{
			LoginResponse response = betFair.login(userName, password, filePassword);
			assertEquals(response.getLoginStatus(), "SUCCESS");
		}
		catch(CryptoException e)
		{
			System.out.println("testLoginBadPassword() pass!");
			fail("CryptoException not expected in testLoginSuccess()");
		}
	}
	
	@Test
	public void testLoginNoInternet()
	{
		try
		{
			betFair.login(userName, password, filePassword);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
	}
	//TODO test you get data back in multiple calls, need to call login first before all following tests
	//TODO test implemented betfair methods are returning the right stuff
	//TODO test no internet
	//TODO test unique token?
	//TODO test event fired
	//TODO test markets closing
	//TODO test debug?
	
}
