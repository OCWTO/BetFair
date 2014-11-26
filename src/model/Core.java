package model;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import betfairUtils.ApingOperation;
import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultContainer;
import betfairUtils.HttpUtil;
import betfairUtils.JsonConverter;
import betfairUtils.JsonrpcRequest;
import betfairUtils.LoginResponse;
import betfairUtils.MarketFilter;

import com.google.gson.Gson;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Core
{
	//private static final String username = "0ocwto0";				//prompt?
	//private static final String password = "@Cracker93";			//prompt?
	private static final String filePassword = "cracker";			//prompt?
	private static final String liveKey = "ztgZ1aJPu2lvvW6a";		//hard code req
	private static final String delayedKey = "scQ6H11vdb6C4s7t";	//hard code req
	private static int	port	= 443;	//??? whatever
	private String dirPrefix;
	//Special method for login (ssl connection and encrypted password sent)
	//So special method for a request too
	
	//Then separate methods for calls and dealing with filters
	
	//Look at enums
	
	public Core()
	{
		dirPrefix = System.getProperty("user.dir");
	}
	
	
	private String makeRequest()
	{
		//make request string
		//make request object
		//populate request object
		//make string the json of the object
		//call 
		//HttpUtil requester = new HttpUtil();
        //return requester.sendPostRequestJsonRpc(requestString, operation, appKey, ssoToken);
		return null;
	}
	

	public void login(String username, String password) throws Exception
	{
		//will call the 2nd makerequest method
		//like listeventtypes does
		
		//Client important since it will do requests for us?
		DefaultHttpClient httpClient = new DefaultHttpClient();
		Gson gson = new Gson();
		
		try
		{
			//SSL stuff
			SSLContext ctx = SSLContext.getInstance("TLS");
			//KeyManager[] keyManagers = getKeyManagers("pkcs12",new FileInputStream(new File(dirPrefix + "\\certs\\client-2048.p12")), filePassword);
			KeyManager[] keyManagers = getKeyManagers("pkcs12",new FileInputStream(new File(dirPrefix + "/certs/client-2048.p12")), filePassword);
			
			ctx.init(keyManagers, null, new SecureRandom());
			SSLSocketFactory factory = new SSLSocketFactory(ctx,new StrictHostnameVerifier());
			ClientConnectionManager manager = httpClient.getConnectionManager();
			manager.getSchemeRegistry().register(new Scheme("https", port, factory));
			
			//Making a post object
			HttpPost httpPost = new HttpPost("https://identitysso.betfair.com/api/certlogin");
			
			//Name value pair used for parameters?
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));
			//httpPost.setParams(new HttpParams);
			//No idea
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	
			//Header
			httpPost.setHeader("X-Application", "appkey");
			System.out.println("Executing request: " + httpPost.getRequestLine());
			
			//Execute
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			System.out.println();
			System.out.println(response.getStatusLine());
			
			if (entity != null)
			{
				LoginResponse responseObject = gson.fromJson(EntityUtils.toString(entity), LoginResponse.class);
				System.out.println(responseObject);
				System.out.println("Response:\nsession token: " + responseObject.sessionToken + " status: " + responseObject.loginStatus);
				
				List<EventTypeResult> r = listEventTypes(new MarketFilter(), liveKey, responseObject.sessionToken);
				
/*




				 * List<EventTypeResult> r = jsonOperations.listEventTypes(marketFilter, applicationKey, sessionToken);
				 * 
				 * 
				 * String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);
				 * 
				 * 
				 *  public List<EventTypeResult> listEventTypes(MarketFilter filter, String appKey, String ssoId) throws APINGException {
				        Map<String, Object> params = new HashMap<String, Object>();
				        params.put(FILTER, filter);
				        params.put(LOCALE, locale);
				        String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);
				        if(ApiNGDemo.isDebug())
				            System.out.println("\nResponse: "+result);
				
				        EventTypeResultContainer container = JsonConverter.convertFromJson(result, EventTypeResultContainer.class);
				        if(container.getError() != null)
				            throw container.getError().getData().getAPINGException();
				
				        return container.getResult();
				
				    }
				 * 
				 * 
				 * 
				 *  protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) {
				        String requestString;
				        //Handling the JSON-RPC request
				        JsonrpcRequest request = new JsonrpcRequest();
				        request.setId("1");
				        request.setMethod(ApiNGDemo.getProp().getProperty("SPORTS_APING_V1_0") + operation);
				        request.setParams(params);
				
				        requestString =  JsonConverter.convertToJson(request);
				        if(ApiNGDemo.isDebug())
				            System.out.println("\nRequest: "+requestString);
				
				        //We need to pass the "sendPostRequest" method a string in util format:  requestString
				        HttpUtil requester = new HttpUtil();
				        return requester.sendPostRequestJsonRpc(requestString, operation, appKey, ssoToken);

				 */
			}
		}
		finally
		{
			//Closing the connection?
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken) {
        String requestString;
        //Handling the JSON-RPC request
        JsonrpcRequest request = new JsonrpcRequest();
        request.setId("1");
        request.setMethod("SportsAPING/v1.0/" + operation);
        request.setParams(params);

        requestString =  JsonConverter.convertToJson(request);
        System.out.println("\nRequest: "+requestString);

        //We need to pass the "sendPostRequest" method a string in util format:  requestString
        HttpUtil requester = new HttpUtil();
        return requester.sendPostRequestJsonRpc(requestString, operation, appKey, ssoToken);
       }
	
	public List<EventTypeResult> listEventTypes(MarketFilter filter, String appKey, String ssoId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filter", filter);
        params.put("locale", Locale.getDefault().toString());
        
        String result = makeRequest(ApingOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);
        System.out.println("\nResponse: "+result);

        EventTypeResultContainer container = JsonConverter.convertFromJson(result, EventTypeResultContainer.class);
        if(container.getError() != null)
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
}