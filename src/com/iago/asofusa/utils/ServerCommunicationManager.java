package com.iago.asofusa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class ServerCommunicationManager {

	public static void sendComment(String nick, String comment, String email, String competition) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Data.insertCommentUrl);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("NICKNAME", nick));
			nameValuePairs.add(new BasicNameValuePair("COMPETITION", competition));
			nameValuePairs.add(new BasicNameValuePair("EMAIL", email));
			nameValuePairs.add(new BasicNameValuePair("COMMENT", comment));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			Log.e(Utils.tag, e.getMessage());
		} catch (IOException e) {
			Log.e(Utils.tag, e.getMessage());
		}
	} 

	public static String getComments(String competition) {
		String out = "";
		String inputLine;

		InputStream inputStream = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Data.getCommentsUrl);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("COMPETITION", competition));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httppost);

			inputStream = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			while ((inputLine = bufferedReader.readLine()) != null) {
				out += inputLine;
			}

			bufferedReader.close();
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}

		return out;
	} 

	public static Boolean insertUser(String email, String name) {
		Boolean out = true;

		// Create a new HttpClient and Post Header
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Data.insertUserUrl);

		String webSource = "";
		String inputLine;

		InputStream inputStream = null;

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("NAME", name));
			nameValuePairs.add(new BasicNameValuePair("EMAIL", email));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpClient.execute(httppost);

			inputStream = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			while ((inputLine = bufferedReader.readLine()) != null) {
				webSource += inputLine;
			}

			bufferedReader.close();

			if (webSource.contains("Consulta fallida: Duplicate entry")) {
				out = false;
			} 

		} catch (ClientProtocolException e) {
			Log.e(Utils.tag, e.getMessage());
		} catch (IOException e) {
			Log.e(Utils.tag, e.getMessage());
		}

		return out;
	}

}
