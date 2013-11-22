package media.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpRequest {
	
	private List<NameValuePair> params;
	private List<Header> headers;
	
	public HttpRequest(){
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<Header>();
	}
	
	public void addParam(String name,String value){
		params.add(new BasicNameValuePair(name, value));
	}
	
	public void addHeader(String name,String value){
		headers.add(new BasicHeader(name, value));
	}

	public HttpResult get(String url)
			throws ClientProtocolException, IOException {
		url += "?";
		for (NameValuePair param : params) {
			url += param.getName() + "=" + param.getValue() + "&";
		}
		url = url.substring(0, url.length() - 1);
		
		HttpGet httpGet = new HttpGet(url);
		for(Header header:headers){
			httpGet.addHeader(header);
		}
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpGet);
		int code = response.getStatusLine().getStatusCode();
		String encoding = parseCharset(response);
		String result = EntityUtils.toString(response.getEntity(), encoding);
		params.clear();
		headers.clear();
		return new HttpResult(code, result);
	}

	@SuppressWarnings("deprecation")
	public HttpResult post(String url)
			throws IOException {
		HttpPost httpPost = new HttpPost(url);
		for(Header header:headers){
			httpPost.addHeader(header);
		}
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		httpPost.setEntity(entity);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResp = httpClient.execute(httpPost);
		int statusCode = httpResp.getStatusLine().getStatusCode();
		String encoding = parseCharset(httpResp);
		String result = EntityUtils.toString(httpResp.getEntity(), encoding);
		params.clear();
		headers.clear();
		return new HttpResult(statusCode, result);
	}

	private String parseCharset(HttpResponse httpResp) {
		String charset = "UTF-8";
		Header[] headers = httpResp.getAllHeaders();
		for (Header header : headers) {
			if (header.getName().equalsIgnoreCase("Content-Encoding")) {
				charset = header.getValue();
				break;
			}
		}
		return charset;
	}

	public class HttpResult {
		public int code;
		public String textResult;

		public HttpResult(int code, String result) {
			this.code = code;
			this.textResult = result;
		}
	}
}
