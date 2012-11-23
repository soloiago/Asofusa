package com.iago.asofusa.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class WebLoader
{	
	public static String load(String url) throws Exception
	{
		String inputLine;
		String pageSource = "";
		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParams, "utf-8");
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1); 

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		URI uri = null;
		InputStream inputStream = null;

		try {
			uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse response = httpClient.execute(method);
			inputStream = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			while ((inputLine = bufferedReader.readLine()) != null) {
				pageSource += inputLine;
			}

			bufferedReader.close();
		} catch (Exception e) {
			pageSource = "fail";
			throw e; 
		}
		return pageSource;
	}
}