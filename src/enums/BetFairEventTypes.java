package enums;

/**
 * Enum for Event Type Ids
 * @author Craig Thomson
 *
 */
public enum BetFairEventTypes
{
	//Ideally if the program were to be extended then new ids would go here.
	FOOTBALL_ID("1");

	private String gameEvent;

	private BetFairEventTypes(String gameEvent)
	{
		this.gameEvent= gameEvent;
	}

	@Override
	public String toString()
	{
		return gameEvent;
	}
}
