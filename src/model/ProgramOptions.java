package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProgramOptions implements Cloneable
{
	private String programOption;
	private String eventTypeId;
	private String eventId;
	private List<String> marketIds;
	private ISimpleBetFair betFair;
	private boolean debugMode;
	private boolean collectionMode;
	private String[] userCredentials;
	private File certificateFile;
	
	public ProgramOptions()
	{
		marketIds = new ArrayList<String>();
		debugMode = false;
		collectionMode = false;
		userCredentials = new String[3];
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
	
	public void setCollectionMode(boolean value)
	{
		collectionMode = value;
	}
	
	public boolean getDebugMode()
	{
		return debugMode;
	}
	
	public boolean getCollectionMode()
	{
		return collectionMode;
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
		this.marketIds.addAll(options);
	}

	public String getProgramOption()
	{
		return this.programOption;
	}

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
	
	public void setProgramOption(String option)
	{
		this.programOption = option;
	}
	
	public void resetBetFair()
	{
		this.betFair = null;
	}
}