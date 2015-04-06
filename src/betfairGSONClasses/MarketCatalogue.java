package betfairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class MarketCatalogue
{
	private String marketId;
	private String marketName;
	private MarketDescription description;
	private List<RunnerCatalog> runners = null;
	private EventType eventType;
	private Competition competition;
	private Event event;

	public String getMarketId()
	{
		return marketId;
	}

	public void setMarketId(String marketId)
	{
		this.marketId = marketId;
	}

	public String getMarketName()
	{
		return marketName;
	}

	public void setMarketName(String marketName)
	{
		this.marketName = marketName;
	}

	public MarketDescription getDescription()
	{
		return description;
	}

	public void setDescription(MarketDescription description)
	{
		this.description = description;
	}

	public List<RunnerCatalog> getRunners()
	{
		return runners;
	}

	public void setRunners(List<RunnerCatalog> runners)
	{
		this.runners = runners;
	}

	public EventType getEventType()
	{
		return eventType;
	}

	public void setEventType(EventType eventType)
	{
		this.eventType = eventType;
	}

	public Competition getCompetition()
	{
		return competition;
	}

	public void setCompetition(Competition competition)
	{
		this.competition = competition;
	}

	public Event getEvent()
	{
		return event;
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}

	public String toString()
	{
		return getMarketName();
	}
}