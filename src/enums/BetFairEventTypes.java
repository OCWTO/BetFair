package enums;

/**
 * Enum for event type ids
 * @author Craig
 *
 */
public enum BetFairEventTypes
{
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
