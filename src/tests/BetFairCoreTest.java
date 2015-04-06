package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Method;

import model.BetfairCore;
import model.ProgramOptions;

import org.junit.Before;
import org.junit.Test;

import betfairGSONClasses.LoginResponse;
import exceptions.BadLoginDetailsException;
import exceptions.CryptoException;

/**
 * Test class for all BetFairCore methods.
 * 
 * @author Craig Thomson
 *
 */
public class BetFairCoreTest
{
	private final boolean debug = true;
	private final String userName = "0ocwto0";
	private final String password = "2014Project";
	private final String filePassword = "project";
	private final String sep = File.separator;
	private final String filePath = "certs" + sep + "client-2048.p12";
	private final File certFile = new File(filePath);
	private BetfairCore betFair;

	/**
	 * Creating a BetFairCore object for tests to use
	 */
	@Before
	public void makeObject()
	{
		betFair = new BetfairCore(debug);
	}

	/**
	 * Testing BetFairCore login when given bad certificate file password
	 */
	@Test
	public void testLoginBadFilePassword()
	{
		try
		{
			betFair.login(userName, password, filePassword + "A", certFile);
			fail("CryptoException expected in testLoginBadFilePassword()");
		}
		catch (CryptoException expectedException)
		{
			System.out.println("testLoginBadFilePassword() pass!");
		}
	}

	/**
	 * Testing BetFairCore login when given bad account username
	 */
	@Test
	public void testLoginBadUsername()
	{
		try
		{
			betFair.login(userName + "A", password, filePassword, certFile);
			fail("CryptoException expected in testLoginBadUsername()");
		}
		catch (BadLoginDetailsException expectedException)
		{
			System.out.println("testLoginBadUsername() pass!");
		}
	}

	/**
	 * Testing BetFairCore login when given bad account password
	 */
	@Test
	public void testLoginBadPassword()
	{
		try
		{
			betFair.login(userName, password + "A", filePassword, certFile);
			fail("CryptoException expected in testLoginBadPassword()");
		}
		catch (BadLoginDetailsException expectedException)
		{
			System.out.println("testLoginBadPassword() pass!");
		}
	}

	/**
	 * Testing BetFairCore login when given correct credentials
	 */
	@Test
	public void testLoginSuccess()
	{
		try
		{
			LoginResponse response = betFair.login(userName, password,
					filePassword, certFile);
			assertEquals(response.getLoginStatus(), "SUCCESS");
		}
		catch (CryptoException expectedException)
		{
			System.out.println("testLoginBadPassword() pass!");
			fail("CryptoException not expected in testLoginSuccess()");
		}
	}

	/**
	 * Testing BetFairCore login when given correct credentials with no internet
	 * connection
	 */
	@Test
	public void testLoginNoInternet()
	{
		try
		{
			betFair.login(userName, password, filePassword, certFile);
			fail();
		}
		catch (Exception expectedException)
		{
		}
	}
	//TODO look into using reflection for testing http://stackoverflow.com/questions/34571/how-to-test-a-class-that-has-private-methods-fields-or-inner-classes
	// TODO test you get data back in multiple calls, need to call login first
	// before all following tests
	// TODO test implemented betfair methods are returning the right stuff
	// TODO test no internet
	// TODO test unique token?
	// TODO test event fired
	// TODO test markets closing
	// TODO test debug?
	//TODO test file location wrong

}
