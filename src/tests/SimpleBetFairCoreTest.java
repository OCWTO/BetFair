package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import model.SimpleBetFairCore;

import org.junit.Before;
import org.junit.Test;

import exceptions.CryptoException;

/**
 * Test class for all SimpleBetFairCore methods.
 * 
 * @author Craig Thomson
 *
 */
public class SimpleBetFairCoreTest
{
	private static final boolean debug = true;
	private static final String userName = "0ocwto0";
	private static final String password = "2014Project";
	private static final String filePassword = "project";
	private SimpleBetFairCore betFair;

	/**
	 * Creating a SimpleBetFairCore object for creation before testing
	 */
	@Before
	public void makeObject()
	{
		betFair = new SimpleBetFairCore(debug);
	}

	/**
	 * Testing SimpleBetFairCore login when given an incorrect certificate file
	 * password
	 */
	@Test
	public void testLoginBadFilePassword()
	{
		try
		{
			betFair.login(userName, password, filePassword + "A");
			fail("CryptoException expected in testLoginBadFilePassword()");
		}
		catch (CryptoException e)
		{
			System.out.println("testLoginBadFilePassword() pass!");
		}
	}

	/**
	 * Testing SimpleBetFairCore login when given an incorrect username (either
	 * testing incorrect password or account non existence)
	 */
	@Test
	public void testLoginBadUsername()
	{
		try
		{
			betFair.login(userName + "A", password, filePassword);
			fail("CryptoException expected in testLoginBadUsername()");
		}
		catch (CryptoException e)
		{
			System.out.println("testLoginBadUsername() pass!");
		}
	}

	/**
	 * Testing SimpleBetFairCore login when given an incorrect account password
	 */
	@Test
	public void testLoginBadPassword()
	{
		try
		{
			betFair.login(userName, password + "A", filePassword);
			fail("CryptoException expected in testLoginBadPassword()");
		}
		catch (CryptoException e)
		{
			System.out.println("testLoginBadPassword() pass!");
		}
	}

	/**
	 * Testing SimpleBetFairCore login when given correct credentials
	 */
	@Test
	public void testLoginSuccess()
	{
		String response;
		try
		{
			response = betFair.login(userName, password, filePassword);
			assertTrue(response.equalsIgnoreCase("success"));
		}
		catch (CryptoException e)
		{
			fail("CryptoException not expected");
		}
	}

	/**
	 * Testing SimpleBetFairCore login when there is no internet connection
	 */
	@Test
	public void testLoginNoInternet()
	{
		try
		{
			betFair.login(userName, password, filePassword);
			fail();
		}
		catch (Exception e)
		{

		}
	}
}