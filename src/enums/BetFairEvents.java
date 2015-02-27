package enums;

public enum BetFairEvents
{
	//TODO look below
	//most likely going to use this to resolve a market name to an event so if a steep gradient change occurs then
	//the value of the enum for that market will be the predicted event. Will work for simplistic market
	//Might need separate enums for each game since market names are shared. I'll also need to cut off 
	//special data since market names dont have runner names (i dont need for resolving)
	Match_Odds("GOAL");// INACTIVE_MARKET("INACTIVE"), SUSPENDED_MARKET(
			//"SUSPENDED"), OPEN_MARKET("OPEN");

	private String gameEvent;

	private BetFairEvents(String gameEvent)
	{
		this.gameEvent= gameEvent;
	}

	@Override
	public String toString()
	{
		return gameEvent;
	}
}
