package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import Exceptions.CryptoException;
import betfairUtils.ApingOperation;
import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultContainer;
import betfairUtils.HttpUtil;
import betfairUtils.JsonConverter;
import betfairUtils.JsonrpcRequest;
import betfairUtils.ListMarketBooksContainer;
import betfairUtils.ListMarketCatalogueContainer;
import betfairUtils.LoginResponse;
import betfairUtils.MarketBook;
import betfairUtils.MarketCatalogue;
import betfairUtils.MarketFilter;
import betfairUtils.MarketProjection;
import betfairUtils.MarketSort;
import betfairUtils.OrderProjection;
import betfairUtils.PriceData;
import betfairUtils.PriceProjection;
import betfairUtils.TimeRange;

import com.google.gson.Gson;

public class BetFairCore
{
	private static final String liveAppKey = "ztgZ1aJPu2lvvW6a"; // Live app key
																	// for fast
																	// calls
	private static final String delayedAppKey = "scQ6H11vdb6C4s7t"; // Delayed
																	// app key
																	// for
																	// slower
																	// calls
	private static final int httpsPort = 443;

	private String sessionToken;

	private String directoryPrefix;

	private boolean debug = false;

	protected final String FILTER = "filter"; // Pretty much the same as
												// MARKET_IDS but it's not a
												// parameter in certain calls
	// protected final String LOCALE = "locale";
	protected final String SORT = "sort";
	protected final String MAX_RESULT = "maxResults";
	protected final String MARKET_IDS = "marketIds";
	protected final String MARKET_PROJECTION = "marketProjection";
	protected final String PRICE_PROJECTION = "priceProjection";
	// protected final String MATCH_PROJECTION = "matchProjection"; ?? Look at
	// NO_ROLLUP, ROLLED_UP_BY_PRICE, ROLLED_UP_BY_AVG_PRICE
	private HttpUtil requester;

	// https://api.developer.betfair.com/services/webapps/docs/display/1smk3cen4v3lu3yomq5qye0ni/Betting+Enums#BettingEnums-MatchProjection

	// Special method for login (ssl connection and encrypted password sent)
	// So special method for a request too

	// Then separate methods for calls and dealing with filters

	// Look at enums

	// TODO implement debug mode
	public BetFairCore(boolean debug)
	{
		this.debug = debug;
		requester = new HttpUtil();
		directoryPrefix = System.getProperty("user.dir");
	}

	// File not found will be avoided using the ui to locate the key thingy
	public LoginResponse login(String username, String password,
			String filePassword) throws CryptoException
	{
		LoginResponse responseObject = new LoginResponse();
		// Client important since it will do requests for us?
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Gson gson = new Gson();

		try
		{
			// SSL stuff
			// SSLContext sslContext = SSLContext.getInstance("TLS");
			// KeyManager[] keyManagers = getKeyManagers("pkcs12",new
			// FileInputStream(new File(dirPrefix +
			// "\\certs\\client-2048.p12")), filePassword);
			KeyManager[] keyManagers = getKeyManagers("pkcs12",
					new FileInputStream(new File(directoryPrefix
							+ "/certs/client-2048.p12")), filePassword);

			// KeyManager[] keyManagers = getKeyManagers("pkcs12", new
			// FileInputStream(new
			// File("C:/MinGW/msys/1.0/var/ssl/client-2048.p12")),
			// filePassword);
			SSLContext sslContext = SSLContext.getInstance("TLS");

			// SSLContext test = SSLContexts.custom().

			// KeyStore myTrustStore = <...>
			// SSLContext ss2 = SSLContexts.custom()
			// .useTLS()
			// .loadTrustMaterial(keyManger)
			// .build();
			// SSLConnectionSocketFactory sslsf = new
			// SSLConnectionSocketFactory(ss2);

			// SSLContext sslC = SSLContexts.custom().useTLS().

			sslContext.init(keyManagers, null, new SecureRandom());
			//
			// SSLContext sslContext = SSLContexts.custom()
			// .useTLS()
			// .loadTrustMaterial(myTrustStore)
			// .build();
			// SSLConnectionSocketFactory sslsf = new
			// SSLConnectionSocketFactory(sslContext);
			//

			SSLSocketFactory factory = new SSLSocketFactory(sslContext,
					new StrictHostnameVerifier());
			ClientConnectionManager manager = httpClient.getConnectionManager();
			manager.getSchemeRegistry().register(
					new Scheme("https", httpsPort, factory));

			// Making a post object
			HttpPost httpPost = new HttpPost(
					"https://identitysso.betfair.com/api/certlogin");

			// Name value pair used for parameters?
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));
			// httpPost.setParams(new HttpParams);
			// No idea
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			// Header
			httpPost.setHeader("X-Application", "appkey");
			// System.out.println("Executing request: "
			// + httpPost.getRequestLine());
			if (debug)
				System.out.println("Request: " + httpPost.getRequestLine());
			// Execute
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			// System.out.println();
			// System.out.println(response.getStatusLine());

			//bad password exception here
			if (entity != null)
			{
				responseObject = gson.fromJson(EntityUtils.toString(entity),
						LoginResponse.class);
				// System.out.println(responseObject);
				if (debug)
					System.out.println("Response:" + responseObject.toString());

				sessionToken = responseObject.getSessionToken();

				// List<EventTypeResult> r = listEventTypes(new MarketFilter(),
				// liveKey, responseObject.getSessionToken());

				//return responseObject;
			}
		}
		catch (Throwable e)
		{
			if(e.getCause().getClass().toString().contains("BadPaddingException"))
			{
				System.out.println("throwing");
				throw new CryptoException("Issue with given file/password" + e.getMessage());
			}
			System.out.println("NO");
			//TODO figure out what else can happen here
			e.printStackTrace();
		}
//		finally
//		{
//			System.out.println("THIS?");
//			//httpClient.getConnectionManager().shutdown();
//			//return responseObject;
//		}
		return responseObject;
	}

	
	//TODO******* check if logged in before any calls
	protected String makeRequest(String operation, Map<String, Object> params,
			String appKey, String ssoToken)
	{
		String requestString;
		// Handling the JSON-RPC request
		JsonrpcRequest request = new JsonrpcRequest();
		request.setId("1");
		request.setMethod("SportsAPING/v1.0/" + operation);
		request.setParams(params);

		requestString = JsonConverter.convertToJson(request);

		if (debug)
			System.out.println("\nRequest: " + requestString);

		// We need to pass the "sendPostRequest" method a string in util format:
		// requestString

		String response = requester.sendPostRequestJsonRpc(requestString,
				operation, appKey, sessionToken);
		if (debug)
			System.out.println("\nResponse: " + response);
		return response;
	}

	//TODO allow multiple
	public List<MarketBook> getMarketBook(String marketId) throws Exception
	{
		// 1.116584263

		// ApingOperation jsonOperations;
		Set<String> eventCode = new HashSet<String>();
		eventCode.add(Integer.toString(6423));

		//TimeRange time = new TimeRange();
		//time.setFrom(new Date());
		//Date t = new Date();
		//t.setDate(31);
		//time.setTo(t);
		// time.setTo((Date) (new Date().setDate(10)));
		Set<String> countries = new HashSet<String>();
		// countries.add("GB");

		Set<String> typesCode = new HashSet<String>();
		// typesCode.add("WIN");

		MarketFilter marketFilter = new MarketFilter();
		// marketFilter.setEventTypeIds(eventCode);
		// String temp = "1.116498268";
		// marketFilter.setEventIds(temp);
		// marketFilter.setMarketIds(temp);
		// marketFilter.set
		// marketFilter.set
		// marketFilter.setMarketStartTime(time);
		// marketFilter.setMarketCountries(countries);
		// marketFilter.setMarketTypeCodes(typesCode);
		// "COMPETITION","EVENT","EVENT_TYPE","RUNNER_DESCRIPTION","RUNNER_METADATA","MARKET_START_TIME"]},"id":
		// 1}'

		List<String> marketIds = new ArrayList<String>();
		marketIds.add(marketId);

		PriceProjection priceProjection = new PriceProjection();
		List<PriceProjection> projs = new ArrayList<PriceProjection>();

		Set<PriceData> priceData = new HashSet<PriceData>();
		priceData.add(PriceData.EX_BEST_OFFERS);

		priceProjection.setPriceData(priceData);
		// priceProjection.s
		// Set<MarketProjection> marketProjection = new
		// HashSet<MarketProjection>();
		// List<MarketProjection> projs = new ArrayList<MarketProjection>();
		// projs.add(MarketProjection.COMPETITION);
		// // MarketProjection.
		// projs.add(MarketProjection.EVENT);
		// projs.add(MarketProjection.EVENT_TYPE);
		// projs.add(MarketProjection.RUNNER_DESCRIPTION);
		// projs.add(MarketProjection.RUNNER_METADATA);
		// projs.add(MarketProjection.MARKET_START_TIME);
		// marketProjection.add(MarketProjection.RUNNER_DESCRIPTION,
		// MarketProjection.COMPETITION);
		// marketProjection.addAll(projs);
		// String maxResults = "1";

		// jsonOperations
		// .listMarketBook(marketIds, priceProjection,
		// orderProjection, matchProjection, currencyCode,
		// applicationKey, sessionToken);

		return listMarketBook(marketIds,
				priceProjection, null, null, null, liveAppKey, sessionToken);
		// System.out.println(marketCatalogueResult.get(0).g);
		// System.out
		// .println("6.(listMarketBook) Get volatile info for Market including best 3 exchange prices available...\n");
		// String marketIdChosen = marketCatalogueResult.get(0).getMarketId();
		//
		// PriceProjection priceProjection = new PriceProjection();
		// Set<PriceData> priceData = new HashSet<PriceData>();
		// priceData.add(PriceData.EX_BEST_OFFERS);
		// priceProjection.setPriceData(priceData);
		//
		// // In this case we don't need these objects so they are declared null
		// OrderProjection orderProjection = null;
		// MatchProjection matchProjection = null;
		// String currencyCode = null;
		//
		// List<String> marketIds = new ArrayList<String>();
		// marketIds.add(marketIdChosen);
		//
		// List<MarketBook> marketBookReturn = jsonOperations.listMarketBook(
		// marketIds, priceProjection, orderProjection, matchProjection,
		// currencyCode, applicationKey, sessionToken);

	}

	public List<MarketBook> listMarketBook(List<String> marketIds,
			PriceProjection priceProjection, OrderProjection orderProjection,
			MarketProjection matchProjection, String currencyCode,
			String appKey, String ssoId) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put(LOCALE, Locale.getDefault().toString());
		params.put(MARKET_IDS, marketIds);
		// System.out.println(marketIds);
		params.put(PRICE_PROJECTION, priceProjection);
		// params.put(ORDER_PROJECTION, orderProjection);
		// params.put(MATCH_PROJECTION, matchProjection);
		params.put("currencyCode", currencyCode);
		String result = makeRequest(ApingOperation.LISTMARKETBOOK.toString(),
				params, appKey, sessionToken);
		// String result = getInstance().makeRequest(
		// ApiNgOperation.LISTMARKETBOOK.getOperationName(), params,
		// appKey, ssoId);
		// if (ApiNGDemo.isDebug())
		// System.out.println("\nResponse: " + result);

		ListMarketBooksContainer container = JsonConverter.convertFromJson(
				result, ListMarketBooksContainer.class);

		if (container.getError() != null)
			System.out.println("CONTAINER ERROR");
		// throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	// TODO: currently only looks 1 day ahead, might want to modify this to look
	// further
	/**
	 * 
	 * @param sportID
	 *            - String representing the games BetFair ID.
	 * @throws Exception
	 */
	public List<MarketCatalogue> getGames(String sportID)
	{
		Set<String> eventCode = new HashSet<String>();
		eventCode.add(sportID);

		TimeRange timeRange = new TimeRange();
		Date fromDate = new Date(new Date().getTime() - (90 * 60 * 1000)); //90 Mins in the past so you can get in play games
		Date toDate = new Date(new Date().getTime() + (24 * 60 * 60 * 1000)); //24 hours from now
		timeRange.setFrom(fromDate);
		timeRange.setTo(toDate);

		Set<String> countries = new HashSet<String>();
		// countries.add("ES");

		Set<String> typesCode = new HashSet<String>();
		// typesCode.add("WIN");

		MarketFilter marketFilter = new MarketFilter();
		marketFilter.setEventTypeIds(eventCode);
		marketFilter.setMarketStartTime(timeRange);
		// marketFilter.setMarketCountries(countries);
		// marketFilter.setMarketTypeCodes(typesCode);

		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);

		// String maxResults = "10";

		// return listEvents(marketFilter, null, MarketSort.FIRST_TO_START,
		// maxResults);
		return listEvents(marketFilter, null, MarketSort.FIRST_TO_START);
	}

	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter,
			Set<MarketProjection> marketProjection, MarketSort sort,
			String maxResult, String maxResults, String appKey, String ssoId)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put(LOCALE, Locale.getDefault().toString());
		params.put(FILTER, filter);
		params.put(MAX_RESULT, maxResults);
		params.put(SORT, sort);
		// params.put(MAX_RESULT, maxResult);
		params.put(MARKET_PROJECTION, marketProjection);
		// String result = getInstance().makeRequest(
		// ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params,
		// appKey, ssoId);
		String result = makeRequest(
				ApingOperation.LISTMARKETCATALOGUE.toString(), params, appKey,
				sessionToken);
		// if (ApiNGDemo.isDebug())
		// System.out.println("\nResponse: " + result);

		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
				result, ListMarketCatalogueContainer.class);

		if (container.getError() != null)
			System.out.println("EXPCEITON");
		// throw container.getError().getData().getAPINGException();

		return container.getResult();
		//
		// jsonrpc_req = '{"jsonrpc": "2.0","method":
		// "SportsAPING/v1.0/listMarketCatalogue","params": {"filter":
		// {"eventIds": ["27312440"]},"maxResults": "200","marketProjection":
		// ["COMPETITION","EVENT","EVENT_TYPE","RUNNER_DESCRIPTION","RUNNER_METADATA","MARKET_START_TIME"]},"id":
		// 1}'
	}

	
	//TODO allow multiple marketid sent
	public List<MarketCatalogue> getMarketCatalogue(String gameId)
	{
		// ApingOperation jsonOperations;
		//Set<String> eventCode = new HashSet<String>();
		//eventCode.add(eventId);

		//TimeRange time = new TimeRange();
		//time.setFrom(new Date());
		Date t = new Date();
		//t.setDate(toDate);
		//time.setTo(t);
		// time.setTo((Date) (new Date().setDate(10)));
		Set<String> countries = new HashSet<String>();
		// countries.add("GB");

		Set<String> typesCode = new HashSet<String>();
		// typesCode.add("WIN");

		MarketFilter marketFilter = new MarketFilter();
		// marketFilter.setEventTypeIds(eventCode);
		Set<String> temp = new HashSet<String>();
		temp.add(gameId);
		marketFilter.setEventIds(temp);
		// marketFilter.setMarketStartTime(time);
		// marketFilter.setMarketCountries(countries);
		// marketFilter.setMarketTypeCodes(typesCode);
		// "COMPETITION","EVENT","EVENT_TYPE","RUNNER_DESCRIPTION","RUNNER_METADATA","MARKET_START_TIME"]},"id":
		// 1}'
		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		List<MarketProjection> projs = new ArrayList<MarketProjection>();
		projs.add(MarketProjection.COMPETITION);
		// MarketProjection.
		projs.add(MarketProjection.EVENT);
		projs.add(MarketProjection.EVENT_TYPE);
		projs.add(MarketProjection.RUNNER_DESCRIPTION);
		projs.add(MarketProjection.RUNNER_METADATA);
		projs.add(MarketProjection.MARKET_START_TIME);
		// marketProjection.add(MarketProjection.RUNNER_DESCRIPTION,
		// MarketProjection.COMPETITION);
		marketProjection.addAll(projs);
		String maxResults = "1";

		List<MarketCatalogue> marketCatalogueResult = listMarketCatalogue(
				marketFilter, marketProjection, MarketSort.FIRST_TO_START,
				maxResults, "200", liveAppKey, sessionToken);
		return marketCatalogueResult;
	}

	// public List<MarketCatalogue> listEvents(MarketFilter filter,
	// Set<MarketProjection> marketProjection, MarketSort sort, String
	// maxResult)
	public List<MarketCatalogue> listEvents(MarketFilter filter,
			Set<MarketProjection> marketProjection, MarketSort sort)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put(LOCALE, Locale.getDefault().toString());
		params.put(FILTER, filter);
		params.put(SORT, sort);
		// params.put(MAX_RESULT, maxResult);
		// params.put(MARKET_PROJECTION, marketProjection);
		// String result = getInstance().makeRequest(
		// ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params,
		// appKey, ssoId);
		String result = makeRequest(ApingOperation.LISTEVENTS.toString(),
				params, liveAppKey, sessionToken);
		// if (ApiNGDemo.isDebug())
		// System.out.println("\nResponse: " + result);

		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
				result, ListMarketCatalogueContainer.class);

		if (container.getError() != null)
			System.out.println("EXPCEITON");
		// throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public List<EventTypeResult> listEventTypes(MarketFilter filter)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("filter", filter);
		params.put("locale", Locale.getDefault().toString());

		String result = makeRequest(
				ApingOperation.LISTEVENTTYPES.getOperationName(), params,
				liveAppKey, sessionToken);
		// System.out.println("\nResponse: " + result);

		EventTypeResultContainer container = JsonConverter.convertFromJson(
				result, EventTypeResultContainer.class);
		if (container.getError() != null)
			System.out.println("err");

		return container.getResult();
	}

	private KeyManager[] getKeyManagers(String keyStoreType,
			InputStream keyStoreFile, String keyStorePassword) throws Exception
	{
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(keyStore, keyStorePassword.toCharArray());
		return kmf.getKeyManagers();
	}

	public void setDebug(Boolean state)
	{
		debug = state;
	}
}