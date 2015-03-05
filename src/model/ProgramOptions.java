package model;

import java.util.ArrayList;
import java.util.List;

public class ProgramOptions
{
	private String programOption;
	private int eventId;
	private List<Integer> marketIds;
	private ISimpleBetFair betFair;
	private boolean debugMode;
	private boolean collectionMode;
	private String[] userCredentials;
	
	public ProgramOptions()
	{
		marketIds = new ArrayList<Integer>();
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
	
	public int getEventId()
	{
		return this.eventId;
	}

	public void setEventId(int option)
	{
		this.eventId = option;
	}

	public List<Integer> getMarketIds()
	{
		return this.marketIds;
	}

	public void addMarketId(Integer option)
	{
		this.marketIds.add(option);
	}

	public String getProgramOption()
	{
		return this.programOption;
	}

	public void setProgramOption(String option)
	{
		this.programOption = option;
	}
}