/*
 * Method list obtained from: https://api.developer.betfair.com/services/webapps/docs/display/1smk3cen4v3lu3yomq5qye0ni/Betting+Operations
 * on 26/2/15.
 */

package model;

import java.util.List;
import java.util.Set;

import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.MarketFilter;
import betFairGSONClasses.PriceProjection;
import enums.MarketProjection;
import enums.MarketSort;
import enums.OrderProjection;

public interface IBetFairCore
{
	//cancelOrders
	
	//listClearedOrders
	
	//listCompetitions

	//listCountries
	
	//listCurrentOrders	
	
	//Return type here is different from API list but it's ok.
	public List<MarketCatalogue> listEvents ( MarketFilter filter);

	public List<EventTypeResult> listEventTypes(MarketFilter filter);
	
	public List<MarketBook> listMarketBook (List<String>marketIds , PriceProjection priceProjection, OrderProjection orderProjection);
	
	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter, Set<MarketProjection>marketProjection, MarketSort sort, int maxResults, String locale);
	
	//listMarketProfitAndLoss

	//listMarketTypes
	
	//listVenues

	//login 
	
	//placeOrders
	
	//replaceOrders
	
	//listTimeRanges

	//updateOrders
}