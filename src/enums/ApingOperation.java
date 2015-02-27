package enums;

public enum ApingOperation {
	LISTEVENTTYPES("listEventTypes"), 
	LISTCOMPETITIONS("listCompetitions"),
	LISTTIMERANGES("listTimeRanges"),
	LISTEVENTS("listEvents"),
	LISTMARKETTYPES("listMarketTypes"),
	LISTCOUNTRIES("listCountries"),
	LISTVENUES("listVenues"),
	LISTMARKETCATALOGUE("listMarketCatalogue"),
	LISTMARKETBOOK("listMarketBook"),
	PLACORDERS("placeOrders");
	
	private String operationName;
	
	private ApingOperation(String operationName){
		this.operationName = operationName;
	}

	@Override
	public String toString() {
		return operationName;
	}
}