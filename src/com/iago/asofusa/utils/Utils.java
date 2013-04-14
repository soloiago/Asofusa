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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;


public class Utils {
	public static final String tag = "ASOFUSA";

	public static String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context); 
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];      
		} else {
			account = null;
		}
		return account;
	}
	
	public static boolean sendResult(String user, String result, String partidoId, String competicion) throws Exception
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Data.sendPoolResultUrl);

		String webSource = "";
		String inputLine;

		InputStream inputStream = null;
		
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("USER", user));
			nameValuePairs.add(new BasicNameValuePair("RESULTADO", result));
			nameValuePairs.add(new BasicNameValuePair("PARTIDO_ID", partidoId));
			nameValuePairs.add(new BasicNameValuePair("COMPETICION", competicion));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpClient.execute(httppost);

			inputStream = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			while ((inputLine = bufferedReader.readLine()) != null) {
				webSource += inputLine;
			}

			bufferedReader.close();
	
		} catch (ClientProtocolException e) {
			Log.e("test", e.getMessage());
		} catch (IOException e) {
			Log.e("test", e.getMessage());
		}
		
		return true;
	}
	
	public static String recuperarUsuario(String email) throws Exception
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Data.getUserUrl);

		String webSource = "";
		String inputLine;

		InputStream inputStream = null;
		
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
	
		} catch (ClientProtocolException e) {
			Log.e("test", e.getMessage());
		} catch (IOException e) {
			Log.e("test", e.getMessage());
		}
		
		return webSource;
	}
}
