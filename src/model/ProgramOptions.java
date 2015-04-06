package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store the state of the GUI options. It's used to initilise various
 * objects in the back end with the settings it has.
 * @author Craig
 *
 */
public class ProgramOptions implements Cloneable
{
	private String eventTypeId;
	private String eventId;
	private List<String> marketIds;
	private ISimpleBetFair betFair;
	private boolean debugMode;
	private boolean testMode;
	private String[] userCredentials;
	private File certificateFile;
	private File testFile;
	
	public ProgramOptions()
	{
		debugMode = false;
		testMode = false;
		userCredentials = new String[3];
	}
	
	public void setTestMode(boolean value)
	{
		testMode = value;
	}
	
	public boolean getTestMode()
	{
		return testMode;
	}
	
	public void setTestFile(File newFile)
	{
		testFile = newFile;
	}
	
	public File getTestFile()
	{
		return testFile;
	}
	
	public void setDebugMode(boolean value)
	{
		debugMode = value;
	}
	
	public void setUserDetails(String username, String password, String filePassword)
	{
		userCredentials[0] = username;
		userCredentials[1] = password;
		userCredentials[2] = filePassword;
	}
	
	public void setCertificateFile(File certFile)
	{
		certificateFile = certFile;
	}
	
	public File getCertificateFile()
	{
		return certificateFile;
	}
	
	public String getUsername()
	{
		return userCredentials[0];
	}
	
	public String getPassword()
	{
		return userCredentials[1];
	}
	
	public String getFilePassword()
	{
		return userCredentials[2];
	}
	
	public boolean getDebugMode()
	{
		return debugMode;
	}
	
	public boolean getCollectionMode()
	{
		return testMode;
	}

	public void addBetFair(ISimpleBetFair betFair)
	{
		this.betFair = betFair;
	}
	
	public ISimpleBetFair getBetFair()
	{
		return this.betFair;
	}
	
	public String getEventTypeId()
	{
		return this.eventTypeId;
	}

	public void setEventTypeId(String option)
	{
		this.eventTypeId = option;
	}
	
	public String getEventId()
	{
		return this.eventId;
	}
	
	public void setEventId(String option)
	{
		this.eventId = option;
	}

	public List<String> getMarketIds()
	{
		return this.marketIds;
	}

	public void addMarketId(String option)
	{
		this.marketIds.add(option);
	}
	
	public void addMarketIds(List<String> options)
	{
		if(marketIds == null)
		{
			marketIds = new ArrayList<String>();
		}
		this.marketIds.addAll(options);
	}

	/**
	 * Clone is required because a copy is needed to be made of this class for production of test files
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}