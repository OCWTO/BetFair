/*
 * Method list obtained from: https://api.developer.betfair.com/services/webapps/docs/display/1smk3cen4v3lu3yomq5qye0ni/Betting+Operations
 * on 26/2/15.
 */

package model;

import java.util.List;

import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.MarketFilter;

public interface IBetFairCore
{
	//cancelOrders
	
	//listClearedOrders
	
	//listCompetitions

	//listCountries
	
	//listCurrentOrders	
	
	//listEvents

	public List<EventTypeResult> listEventTypes(MarketFilter filter);
	
	//listMarketBook
	
	//listMarketCatalogue
	
	//listMarketProfitAndLoss

	//listMarketTypes
	
	//listVenues

	//placeOrders
	
	//replaceOrders
	
	//listTimeRanges

	//updateOrders
}