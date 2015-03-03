package model;

import java.util.ArrayList;
import java.util.List;

public class ProgramOptions
{
	private String programOption;
	private int eventId;
	private List<Integer> marketIds;
	private IBetFairCore betFair;
	
	public ProgramOptions()
	{
		marketIds = new ArrayList<Integer>();
	}

	public void addBetFair(IBetFairCore betFair)
	{
		this.betFair = betFair;
	}
	
	public IBetFairCore getBetFair()
	{
		return this.betFair;
	}
	
	public int getEventId()
	{
		return this.eventId;
	}

	public void seteventId(int option)
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