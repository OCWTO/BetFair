package model;

public class BetFairMarketObject
{
	//TODO add tostring
	private String marketName;
	private String marketId;
	
	public BetFairMarketObject(String name, String id)
	{
		marketName = name;
		marketId = id;
	}

	public String getMarketName()
	{
		return marketName;
	}

	public String getMarketId()
	{
		return marketId;
	}
	
	@Override
	public String toString()
	{
		return marketName + " " + marketId;
	}
}
