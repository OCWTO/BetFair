package betfairGSONClasses;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class EventTypeResult
{
	private EventType eventType;
	private int marketCount;

	public EventType getEventType()
	{
		return eventType;
	}

	public void setEventType(EventType eventType)
	{
		this.eventType = eventType;
	}

	public int getMarketCount()
	{
		return marketCount;
	}

	public void setMarketCount(int marketCount)
	{
		this.marketCount = marketCount;
	}
}