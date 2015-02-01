package betfairUtils;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HttpUtil
{
	// timeout in ms
	private final int connectionTimeout = 10000;
	private final int socketTimeout = 10000;
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

			// HttpClient httpClient = HttpClientBuilder.create().build();
			// CloseableHttpClient httpClient =
			// HttpClientBuilder.create().build();

			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectionTimeout).build();

			CloseableHttpClient httpClient = HttpClientBuilder.create()
					.setDefaultRequestConfig(requestConfig).build();

			// Builder requestConfigBuilder = RequestConfig.custom();
			// requestConfigBuilder.setConnectTimeout(connectionTimeout).setSocketTimeout(10000);

			// httpClient.g

			// HttpParams httpParams = httpClient.getParams();
			// HttpConnectionParams.setConnectionTimeout(httpParams, new
			// Integer(10000));
			// HttpConnectionParams.setSoTimeout(httpParams, new
			// Integer(10000));

			resp = httpClient.execute(post, reqHandler);

		}
		catch (UnsupportedEncodingException e1)
		{
			// Do something

		}
		catch (ClientProtocolException e)
		{
			// Do something

		}
		catch (IOException ioE)
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
}