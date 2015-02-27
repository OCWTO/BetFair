package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.EventTypeResultContainer;
import betFairGSONClasses.ListMarketBooksContainer;
import betFairGSONClasses.ListMarketCatalogueContainer;
import betFairGSONClasses.LoginResponse;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.MarketFilter;
import betFairGSONClasses.PriceProjection;
import betFairGSONClasses.TimeRange;
import betfairUtils.HttpUtil;
import betfairUtils.JsonConverter;
import betfairUtils.JsonrpcRequest;

import com.google.gson.Gson;

import enums.ApingOperation;
import enums.BetFairParams;
import enums.MarketProjection;
import enums.MarketSort;
import enums.OrderProjection;
import enums.PriceData;
import exceptions.CryptoException;
import exceptions.NotLoggedInException;

//TODO modify to use new apache code
public class BetFairCore implements IBetFairCore
{
	// BetFair app keys. Live is for fast requests and delayed is for slow.
	private static final String liveAppKey = "ztgZ1aJPu2lvvW6a";
	private static final String delayedAppKey = "scQ6H11vdb6C4s7t";
	private static final int httpsPort = 443;

	// Created on log in, required for all other calls.
	private String sessionToken;

	// Used to locate certs.
	private String directoryPrefix;
	private boolean debug = false;

	// Class used for handling all (except login) http calls.
	private HttpUtil httpRequester;

	// Then separate methods for calls and dealing with filters

	/**
	 * Creates a new BetFairCore object.
	 */
	public BetFairCore()
	{
		directoryPrefix = System.getProperty("user.dir");
		httpRequester = new HttpUtil();
	}
	
	/**
	 * Initialises the BetFairCore object.
	 * @param debug if true then JSON request and response strings are printed to console
	 */
	public BetFairCore(boolean debug)
	{
		this.debug = debug;
		directoryPrefix = System.getProperty("user.dir");
		httpRequester = new HttpUtil();
	}

	// File not found will be avoided using the ui to locate the key thingy
	/**
	 * Logs the user into BetFair so that further requests can be made.
	 * 
	 * @param username
	 *            BetFair account user name
	 * @param password
	 *            BetFair account password
	 * @param filePassword
	 *            p12 certificate file passwords
	 * @return LoginResponse object containing the login request response and if
	 *         successful, the sessionToken for that login
	 * @throws CryptoException
	 *             If filePassword cannot successfully decrypt the certificate
	 *             file.
	 */
	public LoginResponse login(String username, String password,
			String filePassword) throws CryptoException
	{
		LoginResponse responseObject = new LoginResponse();

		
		//DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpClient httpClient = null;//HttpClientBuilder.create().build();
		
		
		Gson gson = new Gson();

		try
		{

			// SSL stuff
			KeyManager[] keyManagers = getKeyManagers("pkcs12",
					new FileInputStream(new File(directoryPrefix
							+ "/certs/client-2048.p12")), filePassword);
			SSLContext sslContext = SSLContext.getInstance("TLS");

			sslContext.init(keyManagers, null, new SecureRandom());

			
			HttpClientBuilder builder = HttpClientBuilder.create();
		    SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, new StrictHostnameVerifier());
		    builder.setSSLSocketFactory(sslConnectionFactory);

		    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
		            .register("https", sslConnectionFactory)
		            .build();

		    HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
		   
		    builder.setConnectionManager(ccm);

		    httpClient = builder.build();
		    //////////////
			
			
			
			
			
			
			
			
			
			
			
			// Making a post object
			HttpPost httpPost = new HttpPost(
					"https://identitysso.betfair.com/api/certlogin");

			// Name value pair used for parameters?
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			// Header
			httpPost.setHeader("X-Application", "appkey");

			if (debug)
				System.out.println("Request: " + httpPost.getRequestLine());
			// Execute
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			// bad password exception here
			if (entity != null)
			{
				responseObject = gson.fromJson(EntityUtils.toString(entity),
						LoginResponse.class);
				// System.out.println(responseObject);
				if (debug)
					System.out.println("Response:" + responseObject.toString());

				sessionToken = responseObject.getSessionToken();
			}
		}
		catch (Throwable e)
		{
			if (e.getCause().getClass().toString()
					.contains("BadPaddingException"))
			{
				throw new CryptoException("Issue with given file/password. "
						+ e.getMessage());
			}
			e.printStackTrace();
		}
		return responseObject;
	}

	protected String makeRequest(String operation, Map<String, Object> params,
			String appKey, String ssoToken) throws NotLoggedInException
	{
		if (sessionToken == null)
			throw new NotLoggedInException(
					"You must be logged in to call makeRequest");

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

		String response = httpRequester.sendPostRequestJsonRpc(requestString,
				operation, appKey, sessionToken);
		if (debug)
			System.out.println("\nResponse: " + response);
		return response;
	}

	public List<MarketBook> getMarketBook(String marketId)
	{
		List<String> parameters = new ArrayList<String>();
		parameters.add(marketId);
		return getMarketBook(parameters);
	}

	public List<MarketBook> getMarketBook(List<String> marketId)
	{
		Set<String> eventCode = new HashSet<String>();
		eventCode.add(Integer.toString(6423));

		List<String> marketIds = new ArrayList<String>();
		marketIds.addAll(marketId);

		Set<PriceData> priceData = new HashSet<PriceData>();
		priceData.add(PriceData.EX_BEST_OFFERS);

		PriceProjection priceProjection = new PriceProjection();
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

		return listMarketBook(marketIds, priceProjection, null, null, null);

	}

	/**
	 * @param filter
	 * @param marketProjection removed**
	 * @return
	 */
	public List<MarketCatalogue> listEvents(MarketFilter filter)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(BetFairParams.FILTER.toString(), filter);
		parameters
				.put(BetFairParams.SORT.toString(), MarketSort.FIRST_TO_START);
		parameters.put(BetFairParams.MARKET_PROJECTION.toString(),
				null);//marketprojection used to go here.

		String jsonResultLine = null;

		try
		{
			jsonResultLine = makeRequest(ApingOperation.LISTEVENTS.toString(),
					parameters, liveAppKey, sessionToken);
		}
		catch (NotLoggedInException notLoggedIn)
		{
			notLoggedIn.printStackTrace();
		}

		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
				jsonResultLine, ListMarketCatalogueContainer.class);

		if (container.getError() != null)
			System.out.println(container.getError().toString());

		return container.getResult();
	}
	
	public List<MarketBook> listMarketBook(List<String> marketIds,
			PriceProjection priceProjection, OrderProjection orderProjection,
			MarketProjection matchProjection, String currencyCode)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(BetFairParams.MARKET_IDS.toString(), marketIds);
		params.put(BetFairParams.PRICE_PROJECTION.toString(), priceProjection);

		params.put("currencyCode", currencyCode);

		String jsonResultLine = null;
		try
		{
			jsonResultLine = makeRequest(
					ApingOperation.LISTMARKETBOOK.toString(), params,
					liveAppKey, sessionToken);
		}
		catch (NotLoggedInException e)
		{
			e.printStackTrace();
		}

		ListMarketBooksContainer container = JsonConverter.convertFromJson(
				jsonResultLine, ListMarketBooksContainer.class);

		if (container.getError() != null)
			System.out.println(container.getError().toString());

		return container.getResult();
	}

	/**
	 * 
	 * @param filter
	 * @param marketProjection
	 * @param sort
	 * @param maxResults
	 * @return
	 */
	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter,
			Set<MarketProjection> marketProjection, MarketSort sort,
			String maxResults)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(BetFairParams.FILTER.toString(), filter);
		parameters.put(BetFairParams.SORT.toString(), sort);
		parameters.put(BetFairParams.MAX_RESULT.toString(), maxResults);
		parameters.put(BetFairParams.MARKET_PROJECTION.toString(),
				marketProjection);

		String jsonResultLine = null;
		try
		{
			jsonResultLine = makeRequest(
					ApingOperation.LISTMARKETCATALOGUE.toString(), parameters,
					liveAppKey, sessionToken);
		}
		catch (NotLoggedInException e)
		{
			e.printStackTrace();
		}

		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
				jsonResultLine, ListMarketCatalogueContainer.class);

		if (container.getError() != null)
			System.out.println(container.getError().toString());

		return container.getResult();
	}

	/**
	 * @param gameId
	 * @return
	 */
	public List<MarketCatalogue> getMarketCatalogue(String gameId)
	{
		Set<String> eventParams = new HashSet<String>();
		eventParams.add(gameId);

		MarketFilter marketFilter = new MarketFilter();
		marketFilter.setEventIds(eventParams);

		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		List<MarketProjection> projections = new ArrayList<MarketProjection>();
		projections.add(MarketProjection.COMPETITION);
		projections.add(MarketProjection.EVENT);
		projections.add(MarketProjection.EVENT_TYPE);
		projections.add(MarketProjection.RUNNER_DESCRIPTION);
		projections.add(MarketProjection.RUNNER_METADATA);
		projections.add(MarketProjection.MARKET_START_TIME);
		marketProjection.addAll(projections);
		String maxResults = "1000";

		return listMarketCatalogue(marketFilter, marketProjection,
				MarketSort.FIRST_TO_START, maxResults);
	}


	/**
	 * This method is used to return a list of MarketCatalogue objects
	 * representing the games available for betting with the given sport.
	 * 
	 * @param sportID
	 *            - String representing the sports BetFair ID.
	 */
	public List<MarketCatalogue> getGames(String sportID)
	{
		// By default it's going to be from 2 hours ago to 1 day from now.
		TimeRange timeRange = new TimeRange();
		timeRange.setFrom(new Date(new Date().getTime() - (120 * 60 * 1000)));
		timeRange.setTo(new Date(new Date().getTime() + (24 * 60 * 60 * 1000)));

		MarketFilter marketFilter = new MarketFilter();
		Set<String> eventCode = new HashSet<String>();
		eventCode.add(sportID);

		marketFilter.setEventTypeIds(eventCode);
		marketFilter.setMarketStartTime(timeRange);
		// marketFilter.setMarketCountries(countries);

		// TODO look at market projections again
		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);

		return listEvents(marketFilter);
	}

	/**
	 * 
	 */
	public List<EventTypeResult> listEventTypes(MarketFilter filter)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("filter", filter);

		String jsonResultLine = null;
		try
		{
			jsonResultLine = makeRequest(
					ApingOperation.LISTEVENTTYPES.toString(), parameters,
					liveAppKey, sessionToken);
		}
		catch (NotLoggedInException e)
		{
			e.printStackTrace();
		}

		EventTypeResultContainer container = JsonConverter.convertFromJson(
				jsonResultLine, EventTypeResultContainer.class);

		if (container.getError() != null)
			System.out.println(container.getError().toString());

		return container.getResult();
	}

	/**
	 * 
	 * @param keyStoreType
	 * @param keyStoreFile
	 * @param keyStorePassword
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * Sets the debug variable...
	 * 
	 * @param state
	 *            True if debug is to be true, false if not.
	 */
	public void setDebug(Boolean state)
	{
		debug = state;
	}


	@Override
	public List<MarketBook> listMarketBook(List<String> marketIds,
			PriceProjection priceProjection, OrderProjection orderProjection)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter,
			Set<MarketProjection> marketProjection, MarketSort sort,
			int maxResults, String locale)
	{
		// TODO Auto-generated method stub
		return null;
	}
}