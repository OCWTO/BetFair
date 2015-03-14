package model;

public class BetFairMarketObject implements BetFairDataObject
{
	//TODO add tostring
	private String marketName;
	private String marketId;
	
	public BetFairMarketObject(String name, String id)
	{
		marketName = name;
		marketId = id;
	}

	public String getName()
	{
		return marketName;
	}

	public String getId()
	{
		return marketId;
	}
	
	@Override
	public String toString()
	{
		return marketName + " " + marketId;
	}
}
