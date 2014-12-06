package betfairUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpUtil
{

	private final String HTTP_HEADER_X_APPLICATION = "X-Application";
	private final String HTTP_HEADER_X_AUTHENTICATION = "X-Authentication";
	private final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	private final String HTTP_HEADER_ACCEPT = "Accept";
	private final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";

	public HttpUtil()
	{
		super();
	}

	private String sendPostRequest(String param, String operation,
			String appKey, String ssoToken, String URL,
			ResponseHandler<String> reqHandler)
	{
		String jsonRequest = param;

		HttpPost post = new HttpPost(URL);
		String resp = null;
		try
		{
			post.setHeader(HTTP_HEADER_CONTENT_TYPE, "application/json");
			post.setHeader(HTTP_HEADER_ACCEPT, "application/json");
			post.setHeader(HTTP_HEADER_ACCEPT_CHARSET, "UTF-8");
			post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
			post.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);

			post.setEntity(new StringEntity(jsonRequest, "UTF-8"));

			HttpClient httpClient = new DefaultHttpClient();

			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, new Integer(
					10000));
			HttpConnectionParams.setSoTimeout(httpParams, new Integer(10000));

			resp = httpClient.execute(post, reqHandler);

		} catch (UnsupportedEncodingException e1)
		{
			// Do something

		} catch (ClientProtocolException e)
		{
			// Do something

		} catch (IOException ioE)
		{
			// Do something

		}

		return resp;

	}

	public String sendPostRequestJsonRpc(String param, String operation,
			String appKey, String ssoToken)
	{
		String apiNgURL = "https://api.betfair.com/exchange/betting/json-rpc/v1";

		return sendPostRequest(param, operation, appKey, ssoToken, apiNgURL,
				new JsonResponseHandler());

	}

	public String sendSecurePostRequestJsonRpc(String param, String appKey)
	{
		String apiNgURL = "https://identitysso.betfair.com/api/certlogin";

		return sendPostRequest(param, appKey, apiNgURL,
				new JsonResponseHandler());
	}

	private String sendPostRequest(String param, String appKey,
			String apiNgURL, ResponseHandler<String> reqHandler)
	{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String resp = null;
		try
		{
			// SSL stuff
			SSLContext ctx = SSLContext.getInstance("TLS");
			// KeyManager[] keyManagers = getKeyManagers("pkcs12",new
			// FileInputStream(new File(dirPrefix +
			// "\\certs\\client-2048.p12")),
			// filePassword);
			KeyManager[] keyManagers = getKeyManagers("pkcs12",
					new FileInputStream(new File(System.getProperty("user.dir")
							+ "/certs/client-2048.p12")), "cracker");

			ctx.init(keyManagers, null, new SecureRandom());
			SSLSocketFactory factory = new SSLSocketFactory(ctx,
					new StrictHostnameVerifier());
			ClientConnectionManager manager = httpClient.getConnectionManager();
			manager.getSchemeRegistry().register(
					new Scheme("https", 443, factory));
			// port 443 for https

			String jsonRequest = param;

			HttpPost post = new HttpPost(apiNgURL);
			// String resp = null;
			try
			{
				post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
				// post.setHeader(HTTP_HEADER_CONTENT_TYPE,
				// "application/x-www-form-urlencoded");
				// post.setHeader(HTTP_HEADER_ACCEPT, "application/json");
				// post.setHeader(HTTP_HEADER_ACCEPT_CHARSET, "UTF-8");
				// post.setHeader(HTTP_HEADER_X_APPLICATION, appKey);
				// post.setHeader(HTTP_HEADER_X_AUTHENTICATION, ssoToken);

				post.setEntity(new StringEntity(jsonRequest, "UTF-8"));

				// HttpClient httpClient = new DefaultHttpClient();

				HttpParams httpParams = httpClient.getParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						new Integer(10000));
				HttpConnectionParams.setSoTimeout(httpParams,
						new Integer(10000));

				resp = httpClient.execute(post, reqHandler);

			} catch (UnsupportedEncodingException e1)
			{
				// Do something

			} catch (ClientProtocolException e)
			{
				// Do something

			} catch (IOException ioE)
			{
				// Do something

			}

		} catch (Exception e)
		{
			System.out.println("EXCEPTION " + e.getStackTrace());
		} finally
		{
			httpClient.getConnectionManager().shutdown();
			return resp;
			// Closing the connection?

		}

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
