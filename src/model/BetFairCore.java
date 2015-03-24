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
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
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
import enums.BetFairLogin;
import enums.BetFairParams;
import enums.MarketProjection;
import enums.MarketSort;
import enums.OrderProjection;
import enums.PriceData;
import exceptions.BadLoginDetailsException;
import exceptions.CryptoException;
import exceptions.NotLoggedInException;

/**
 * BetFairCore is a class that implements all the BetFair API methods in the
 * IBetFairCore interface. It is used for all BetFair method calls. Currently
 * it sits below an ISimpleBetFairObject
 * 
 * @author Craig Thomson
 *
 */
public class BetFairCore implements IBetFairCore
{
	// BetFair app keys. Live is for fast requests and delayed is for slow.
	@SuppressWarnings("unused")
	private static final String delayedAppKey = "scQ6H11vdb6C4s7t";
	private static final String liveAppKey = "ztgZ1aJPu2lvvW6a";

	// Created on log in, required for all other calls.
	private String sessionToken;

	// Used to locate certs.
	private String directoryPrefix;
	private String separator;
	private boolean debug = false;

	// Class used for handling all (except login) http calls.
	private HttpUtil httpRequester;


	/**
	 * Creates a new BetFairCore object.
	 */
	public BetFairCore()
	{
		utilInit();
	}

	/**
	 * Initialises the BetFairCore object.
	 * 
	 * @param debug
	 *            if true then JSON request and response strings are printed to
	 *            console
	 */
	public BetFairCore(boolean debug)
	{
		this.debug = debug;
		utilInit();
	}
	
	private void utilInit()
	{
		directoryPrefix = System.getProperty("user.dir");
		separator =  File.separator;
		httpRequester = new HttpUtil();
	}

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
	public LoginResponse login(String username, String password, String filePassword, File certificateFile)
	{
		LoginResponse responseObject = new LoginResponse();
		HttpClient httpClient = null;

		Gson gson = new Gson();

		try
		{
			//Dealing with certificate file
			KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File(directoryPrefix + separator + "certs" + separator + "client-2048.p12")),
					filePassword);
			SSLContext sslContext = SSLContext.getInstance("TLS");

			sslContext.init(keyManagers, null, new SecureRandom());

			HttpClientBuilder builder = HttpClientBuilder.create();
			SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, new StrictHostnameVerifier());
			builder.setSSLSocketFactory(sslConnectionFactory);

			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslConnectionFactory)
					.build();

			HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);

			builder.setConnectionManager(ccm);

			//Creating a client with an encrypted connection
			httpClient = builder.build();

			// Making a post object
			HttpPost httpPost = new HttpPost("https://identitysso.betfair.com/api/certlogin");

			List<NameValuePair> credentialsParameters = new ArrayList<NameValuePair>();
			credentialsParameters.add(new BasicNameValuePair("username", username));
			credentialsParameters.add(new BasicNameValuePair("password", password));

			httpPost.setEntity(new UrlEncodedFormEntity(credentialsParameters));

			httpPost.setHeader("X-Application", "appkey");

			// Not using HttpUtil so need a separate debug check here.
			if (debug)
				System.out.println("Request: " + httpPost.getRequestLine());

			// Execute
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			responseObject = gson.fromJson(EntityUtils.toString(entity), LoginResponse.class);

			if (debug)
				System.out.println("Response:" + responseObject.toString());

			if (responseObject.getSessionToken() == null)
				throw new BadLoginDetailsException(responseObject.getLoginStatus());

			sessionToken = responseObject.getSessionToken();
		} 
		catch (Throwable e)
		{
			if (e.getMessage().equals(BetFairLogin.BADLOGINDETAILS.toString()))
			{
				throw new BadLoginDetailsException(e.getMessage());
			} 
			else if (e.getCause().getClass().toString().contains("BadPaddingException"))
			{
				throw new CryptoException("Issue with given file/password. " + e.getMessage());
			} 
			else
			{
				e.printStackTrace();
			}
		}
		return responseObject;
	}

	/**
	 * 
	 * @param operation The operation name
	 * @param params Request parameters
	 * @param appKey The application key
	 * @param ssoToken The current session token
	 * @return
	 * @throws NotLoggedInException
	 */
	private String makeRequest(String operation, Map<String, Object> params) throws NotLoggedInException
	{
		if (sessionToken == null)
			throw new NotLoggedInException("You must be logged in to call makeRequest");

		String requestString;
		
		// Handling the JSON-RPC request
		JsonrpcRequest request = new JsonrpcRequest();
		request.setId("1");
		request.setMethod("SportsAPING/v1.0/" + operation);
		request.setParams(params);

		//Convert the request string to a JSON object to give to the API
		requestString = JsonConverter.convertToJson(request);

		if (debug)
			System.out.println("\nRequest: " + requestString);

		//Send the JSON string request line and get the response
		String response = httpRequester.sendPostRequestJsonRpc(requestString, operation, liveAppKey, sessionToken);
		if (debug)
			System.out.println("\nResponse: " + response);
		
		//Return our json response line
		return response;
	}

	/**
	 * Request the market book for a given markets id
	 * @param marketId The markets id
	 * @return A list of marketbook objects for the id (of size 1)
	 */
	public List<MarketBook> getMarketBook(String marketId)
	{
		List<String> parameters = new ArrayList<String>();
		parameters.add(marketId);
		return getMarketBook(parameters);
	}

	/**
	 * Request a list of market books for a list of market ids
	 * @param marketId The list of marketIds that data is being requested for
	 * @return A lift of marketbook objects for the id
	 */
	public List<MarketBook> getMarketBook(List<String> marketId)
	{
		List<String> marketIds = new ArrayList<String>();
		marketIds.addAll(marketId);

		//Request the 3 best back and lay prices
		Set<PriceData> priceData = new HashSet<PriceData>();
		priceData.add(PriceData.EX_BEST_OFFERS);

		//Priceprojection can hold other data but in this case only PriceData
		PriceProjection priceProjection = new PriceProjection();
		priceProjection.setPriceData(priceData);

		return listMarketBook(marketIds, priceProjection, null);
	}

	//Doc in interface.
	public List<MarketCatalogue> listEvents(MarketFilter filter)
	{
		//Create parameter map
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//Populate parameters
		parameters.put(BetFairParams.FILTER.toString(), filter);
		parameters.put(BetFairParams.SORT.toString(), MarketSort.FIRST_TO_START);
		parameters.put(BetFairParams.MARKET_PROJECTION.toString(), null);

		String jsonResultLine = null;

		try
		{
			//Get json result from api
			jsonResultLine = makeRequest(ApingOperation.LISTEVENTS.toString(), parameters);
		} 
		catch (NotLoggedInException notLoggedIn)
		{
			notLoggedIn.printStackTrace();
		}

		//Convert the json result string to objects using GSON (in the JsonConverter class)
		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(jsonResultLine, ListMarketCatalogueContainer.class);

		//Shouldn't happen...
		if (container.getError() != null)
			System.out.println(container.getError().toString());

		return container.getResult();
	}

	//Doc in IBetFair interface
	public List<MarketBook> listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection)
	{
		//Create parameter object for the request
		Map<String, Object> params = new HashMap<String, Object>();
		
		//Populate with the market ids requested and priceprojection for format of received data.
		params.put(BetFairParams.MARKET_IDS.toString(), marketIds);
		params.put(BetFairParams.PRICE_PROJECTION.toString(), priceProjection);
		params.put("currencyCode", null);

		String jsonResultLine = null;
		try
		{
			jsonResultLine = makeRequest(ApingOperation.LISTMARKETBOOK.toString(), params);
		} 
		catch (NotLoggedInException e)
		{
			e.printStackTrace();
		}

		ListMarketBooksContainer container = JsonConverter.convertFromJson(jsonResultLine, ListMarketBooksContainer.class);
		
		//Recursively call again if it fails. Occassionally requests aren't served by the server, or timed out.
		while(container == null)
		{
			jsonResultLine = makeRequest(ApingOperation.LISTMARKETBOOK.toString(), params);
			container = JsonConverter.convertFromJson(jsonResultLine, ListMarketBooksContainer.class);
		}
		
		if (container.getError() != null)
			System.out.println(container.getError().toString());
	
		return container.getResult();
	}

	/**
	 * 
	 * @param filter MarketFilter object
	 * @param marketProjection MarketProjection object
	 * @param sort Result sorting option
	 * @param maxResults Number of results that can be received.
	 * @return A list of MarketCatalogue objects for the request.
	 */
	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection, MarketSort sort, String maxResults)
	{
		//Create http parameter object
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		//Populate...
		parameters.put(BetFairParams.FILTER.toString(), filter);
		parameters.put(BetFairParams.SORT.toString(), sort);
		parameters.put(BetFairParams.MAX_RESULT.toString(), maxResults);
		parameters.put(BetFairParams.MARKET_PROJECTION.toString(), marketProjection);

		String jsonResultLine = null;
		try
		{
			//Get results in json form from api.
			jsonResultLine = makeRequest(ApingOperation.LISTMARKETCATALOGUE.toString(), parameters);
		} 
		catch (NotLoggedInException e)
		{
			e.printStackTrace();
		}
		
		//Convert from json to objects using gson.
		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(jsonResultLine, ListMarketCatalogueContainer.class);
		
		if (container.getError() != null)
			System.out.println(container.getError().toString());

		return container.getResult();
	}

	/**
	 * @param marketId The market id that the requested market catalogue is for.
	 * @return A list of marketCatalogue objects for the given market id, these represent the markets
	 * available for betting with this game. 
	 */
	public List<MarketCatalogue> getMarketCatalogue(String marketId)
	{
		//Create a set of event Parameters, in this case the market id
		Set<String> eventParams = new HashSet<String>();
		eventParams.add(marketId);

		MarketFilter marketFilter = new MarketFilter();
		marketFilter.setEventIds(eventParams);

		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		List<MarketProjection> projections = new ArrayList<MarketProjection>();
		
		//Create a list of the requested data and add it to the parameters
		projections.add(MarketProjection.COMPETITION);
		projections.add(MarketProjection.EVENT);
		projections.add(MarketProjection.EVENT_TYPE);
		projections.add(MarketProjection.RUNNER_DESCRIPTION);
		projections.add(MarketProjection.RUNNER_METADATA);
		projections.add(MarketProjection.MARKET_START_TIME);
		marketProjection.addAll(projections);
		String maxResults = "1000";

		return listMarketCatalogue(marketFilter, marketProjection, MarketSort.FIRST_TO_START, maxResults);
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
		//For now it looks for games that started 2 hours into the past (so in play
		//games can be found and 1 day ahead to see games that havent started yet.
		//The range is a day so that games can be scheduled for recording some time
		//ahead of their start (good for recording american sports).
		TimeRange timeRange = new TimeRange();
		timeRange.setFrom(new Date(new Date().getTime() - (120 * 60 * 1000)));
		timeRange.setTo(new Date(new Date().getTime() + (24 * 60 * 60 * 1000)));

		MarketFilter marketFilter = new MarketFilter();
		Set<String> eventCode = new HashSet<String>();
		eventCode.add(sportID);

		marketFilter.setEventTypeIds(eventCode);
		marketFilter.setMarketStartTime(timeRange);


		Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
		marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);

		return listEvents(marketFilter);
	}

	//See IBetFairCore for doc
	public List<EventTypeResult> listEventTypes(MarketFilter filter)
	{
		//Populate http params
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("filter", filter);

		String jsonResultLine = null;
		try
		{
			//Get the result from the api is json form
			jsonResultLine = makeRequest(ApingOperation.LISTEVENTTYPES.toString(), parameters);
		} 
		catch (NotLoggedInException e)
		{
			e.printStackTrace();
		}

		//Use gson to convert the json to objects
		EventTypeResultContainer container = JsonConverter.convertFromJson(jsonResultLine, EventTypeResultContainer.class);

		if (container.getError() != null)
			System.out.println(container.getError().toString());

		//Return the result.
		return container.getResult();
	}

	//Code for unpacking the certificate so the user can log in.
	private KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception
	{
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
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
}