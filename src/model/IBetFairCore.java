/*
 * Method list obtained from: https://api.developer.betfair.com/services/webapps/docs/display/1smk3cen4v3lu3yomq5qye0ni/Betting+Operations
 * on 26/2/15.
 */

package model;

import java.io.File;
import java.util.List;
import java.util.Set;

import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.LoginResponse;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.MarketFilter;
import betFairGSONClasses.PriceProjection;
import enums.MarketProjection;
import enums.MarketSort;
import enums.OrderProjection;
import exceptions.CryptoException;

public interface IBetFairCore
{
	/**
	 * NOT SUPPORTED
	 */
	//public CancelExecutionReport cancelOrders(String marketId, List<CancelInstruction> instructions, String customerRef);
	
	/**
	 * NOT SUPPORTED
	 */
	//public ClearedOrderSummaryReport listClearedOrders(BetStatus betStatus, Set<EventTypeId> eventTypeIds, Set<EventId> eventIds, Set<MarketId> marketIds, Set<RunnerId> runnerIds, Set<BetId> betIds, Side side, TimeRange settledDateRange, GroupBy groupBy, boolean includeItemDescription, String locale, int fromRecord, int recordCount);
	
	/**
	 * NOT SUPPORTED
	 */
	//public List<CompetitionResult> listCompetitions(MarketFilter filter, String locale);
	
	/**
	 * NOT SUPPORTED
	 */
	//public List<CountryCodeResult> listCountries(MarketFilter filter, String locale);
	
	/**
	 * NOT SUPPORTED
	 */
	//public CurrentOrderSummaryReport listCurrentOrders(Set<String>betIds, Set<String> marketIds, OrderProjection orderProjection, TimeRange placedDateRange, OrderBy orderBy, SortDir sortDir, int fromRecord, int recordCount);
	
	/**
	 * UPDATE JDOC
	 * @param filter
	 * @return
	 */
	public List<MarketCatalogue> listEvents(MarketFilter filter);

	/**
	 * UPDATE JDOC
	 * @param filter
	 * @return
	 */
	public List<EventTypeResult> listEventTypes(MarketFilter filter);
	
	/**
	 * UPDATE JDOC
	 * @param marketIds
	 * @param priceProjection
	 * @param orderProjection
	 * @return
	 */
	public List<MarketBook> listMarketBook(List<String>marketIds, PriceProjection priceProjection, OrderProjection orderProjection);
	
	/**
	 * UPDATE JDOC
	 * @param filter
	 * @param marketProjection
	 * @param sort
	 * @param maxResults
	 * @param locale
	 * @return
	 */
	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter, Set<MarketProjection>marketProjection, MarketSort sort, int maxResults, String locale);
	
	/**
	 * NOT SUPPORTED
	 */
	//public List<MarketProfitAndLoss>	listMarketProfitAndLoss(Set<MarketId> marketIds, boolean includeSettledBets, boolean includeBspBets, boolean netOfCommission);

	/**
	 * NOT SUPPORTED
	 */
	//public List<MarketTypeResult> listMarketTypes(MarketFilter filter ,String locale);
	
	/**
	 * NOT SUPPORTED
	 */
	//public List<VenueResult> listVenues (MarketFilter filter ,String locale);

	/**
	 * 
	 * @param username BetFair account username
	 * @param password BetFair account password
	 * @param filePassword BetFairs certificate file password
	 * @return
	 * @throws CryptoException if certificate file password is incorrect
	 */
	public LoginResponse login(String username, String password, String filePassword, File certificateFile);
	
	/**
	 * NOT SUPPORTED
	 */
	//public PlaceExecutionReport placeOrders(String marketId, List<PlaceInstruction> instructions, String customerRef);
	
	/**
	 * NOT SUPPORTED
	 */
	//public ReplaceExecutionReport replaceOrders(String marketId, List<ReplaceInstruction> instructions, String customerRef);
	
	/**
	 * NOT SUPPORTED
	 */
	//public List<TimeRangeResult> listTimeRanges(MarketFilter filter, TimeGranularity granularity);

	/**
	 * NOT SUPPORTED
	 */
	//public UpdateExecutionReport updateOrders(StringmarketId, List<UpdateInstruction> instructions, String customerRef);
}