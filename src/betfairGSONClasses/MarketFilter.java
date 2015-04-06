package betfairGSONClasses;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class MarketFilter
{
	private String textQuery;
	private Set<String> exchangeIds;
	private Set<String> eventTypeIds;
	private Set<String> marketIds;
	private Boolean inPlayOnly;
	private Set<String> eventIds;
	private Set<String> competitionIds;
	private Set<String> venues;
	private Boolean bspOnly;
	private Boolean turnInPlayEnabled;
	private Set<String> marketCountries;
	private Set<String> marketTypeCodes;
	private TimeRange marketStartTime;

	public String getTextQuery()
	{
		return textQuery;
	}

	public void setTextQuery(String textQuery)
	{
		this.textQuery = textQuery;
	}

	public Set<String> getExchangeIds()
	{
		return exchangeIds;
	}

	public void setExchangeIds(Set<String> exchangeIds)
	{
		this.exchangeIds = exchangeIds;
	}

	public Set<String> getEventTypeIds()
	{
		return eventTypeIds;
	}

	public void setEventTypeIds(Set<String> eventTypeIds)
	{
		this.eventTypeIds = eventTypeIds;
	}

	public Set<String> getMarketIds()
	{
		return marketIds;
	}

	public void setMarketIds(Set<String> marketIds)
	{
		this.marketIds = marketIds;
	}

	public Boolean getInPlayOnly()
	{
		return inPlayOnly;
	}

	public void setInPlayOnly(Boolean inPlayOnly)
	{
		this.inPlayOnly = inPlayOnly;
	}

	public Set<String> getEventIds()
	{
		return eventIds;
	}

	public void setEventIds(Set<String> eventIds)
	{
		this.eventIds = eventIds;
	}

	public Set<String> getCompetitionIds()
	{
		return competitionIds;
	}

	public void setCompetitionIds(Set<String> competitionIds)
	{
		this.competitionIds = competitionIds;
	}

	public Set<String> getVenues()
	{
		return venues;
	}

	public void setVenues(Set<String> venues)
	{
		this.venues = venues;
	}

	public Boolean getBspOnly()
	{
		return bspOnly;
	}

	public void setBspOnly(Boolean bspOnly)
	{
		this.bspOnly = bspOnly;
	}

	public Boolean getTurnInPlayEnabled()
	{
		return turnInPlayEnabled;
	}

	public void setTurnInPlayEnabled(Boolean turnInPlayEnabled)
	{
		this.turnInPlayEnabled = turnInPlayEnabled;
	}

	public Set<String> getMarketCountries()
	{
		return marketCountries;
	}

	public void setMarketCountries(Set<String> marketCountries)
	{
		this.marketCountries = marketCountries;
	}

	public Set<String> getMarketTypeCodes()
	{
		return marketTypeCodes;
	}

	public void setMarketTypeCodes(Set<String> marketTypeCodes)
	{
		this.marketTypeCodes = marketTypeCodes;
	}

	public TimeRange getMarketStartTime()
	{
		return marketStartTime;
	}

	public void setMarketStartTime(TimeRange marketStartTime)
	{
		this.marketStartTime = marketStartTime;
	}

	public String toString()
	{
		return "{" + "" + "textQuery=" + getTextQuery() + "," + "exchangeIds="
				+ getExchangeIds() + "," + "eventTypeIds=" + getEventTypeIds()
				+ "," + "eventIds=" + getEventIds() + "," + "competitionIds="
				+ getCompetitionIds() + "," + "marketIds=" + getMarketIds()
				+ "," + "venues=" + getVenues() + "," + "bspOnly="
				+ getBspOnly() + "," + "turnInPlayEnabled="
				+ getTurnInPlayEnabled() + "," + "inPlayOnly="
				+ getInPlayOnly() + "," + "marketCountries="
				+ getMarketCountries() + "," + "marketTypeCodes="
				+ getMarketTypeCodes() + "," + "marketStartTime="
				+ getMarketStartTime() + "}";
	}
	
	public static MarketFilter createMarketFilter(int gameStartMinsAgo, int gameStartHoursLater, String eventId)
	{
		TimeRange gameTimeRange = new TimeRange();
		gameTimeRange.setFrom(new Date(new Date().getTime() - (gameStartMinsAgo * 60 * 1000)));
		gameTimeRange.setTo(new Date(new Date().getTime() + (gameStartHoursLater * 60 * 60 * 1000)));
		
		Set<String> eventIds = new HashSet<String>();
		eventIds.add(eventId);
		
		MarketFilter thisFilter = new MarketFilter();
		thisFilter.setEventTypeIds(eventIds);
		thisFilter.setMarketStartTime(gameTimeRange);
		
		return thisFilter;
	}
}