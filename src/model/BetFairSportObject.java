package model;

public class BetFairSportObject implements BetFairDataObject
{
	private String sportName;
	private String sportId;
	
	public BetFairSportObject(String name, String id)
	{
		this.sportName = name;
		this.sportId = id;
	}
	
	public String getName()
	{
		return this.sportName;
	}
	
	public String getId()
	{
		return this.sportId;
	}
	
	@Override
	public String toString()
	{
		return "Sport name: " + sportName +". Sport Id: " + sportId;
	}
}