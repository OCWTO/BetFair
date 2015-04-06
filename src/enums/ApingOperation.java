package enums;

/**
 * Enum for Betfair method names
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 *
 */
public enum ApingOperation
{
	LISTEVENTTYPES("listEventTypes"), LISTCOMPETITIONS("listCompetitions"), LISTTIMERANGES(
			"listTimeRanges"), LISTEVENTS("listEvents"), LISTMARKETTYPES(
			"listMarketTypes"), LISTCOUNTRIES("listCountries"), LISTVENUES(
			"listVenues"), LISTMARKETCATALOGUE("listMarketCatalogue"), LISTMARKETBOOK(
			"listMarketBook"), PLACORDERS("placeOrders");

	private String operationName;

	private ApingOperation(String operationName)
	{
		this.operationName = operationName;
	}

	@Override
	public String toString()
	{
		return operationName;
	}
}