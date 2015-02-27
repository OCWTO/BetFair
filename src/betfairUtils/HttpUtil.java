package betfairUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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

			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectionTimeout).build();

			CloseableHttpClient httpClient = HttpClientBuilder.create()
					.setDefaultRequestConfig(requestConfig).build();
			resp = httpClient.execute(post, reqHandler);

		}
		catch (UnsupportedEncodingException e1)
		{
			// Do something

		}
		catch (ClientProtocolException e2)
		{
			// Do something

		}
		catch (IOException e3)
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