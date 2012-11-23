package com.iago.asofusa;

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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.iago.asofusa.activities.CompetitionActivity;
import com.iago.asofusa.activities.FirstActivity;
import com.iago.asofusa.db.DbHandler;
import com.iago.asofusa.utils.Data;
import com.iago.asofusa.utils.Utils;

/**
 * GCMIntentService. 
 * @author iago
 *
 */
public class GCMIntentService extends GCMBaseIntentService {
	private DbHandler dbHandler;

	public GCMIntentService() {
		//Number got from the GAE
		super("537359134324");
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.d(Utils.tag, "REGISTRATION: Error -> " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String msg = intent.getExtras().getString("msg");
		if (msg != null) {
			Log.d(Utils.tag, "Message: " + msg);
			showForumNotification(context, msg);
		}

		String general = intent.getExtras().getString("general");
		if (general != null) {
			Log.d(Utils.tag, "Message: " + general);
			showGeneralNotification(context, general);
		}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		Log.d(Utils.tag, "REGISTRATION OK");

		dbHandler = new DbHandler(context);
		dbHandler.open();
		List<Integer> alertList = dbHandler.getListaAlertas();
		List <String> competitionsList = dbHandler.getListaCompeticiones();
		serverRegistration(alertList, competitionsList, regId);
		dbHandler.close();
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.d(Utils.tag, "UNREGISTRATION OK");

		serverUnregistration(regId);
	}

	private void serverRegistration(List<Integer> alertList, List <String> competitionsList, String regId)
	{
		final String url = Data.registerUrl;

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("regId", regId));

			nameValuePairs.add(new BasicNameValuePair("A0", "GENERAL"));
			Integer index = 1;
			for (int i = 0; i < alertList.size(); i++) {
				if (alertList.get(i) == 1) {
					nameValuePairs.add(new BasicNameValuePair("A" + index.toString(), competitionsList.get(i)));
					index++;
				}
			}
			nameValuePairs.add(new BasicNameValuePair("numA", index.toString()));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			String pageSource = "";
			String inputLine;
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			while ((inputLine = bufferedReader.readLine()) != null) {
				pageSource += inputLine;
			}
			Log.d(Utils.tag, "Server response: " + pageSource);
			bufferedReader.close();

		} catch (ClientProtocolException e) {
			Log.e(Utils.tag, e.getMessage());
		} catch (IOException e) {
			Log.e(Utils.tag, e.getMessage());
		}
	}

	private void serverUnregistration(String regId)
	{
		final String URL="http://www.iagodiaz.com/ASOFUSA/desregistro.php";

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("regId", regId));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			String pageSource = "";
			String inputLine;
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			while ((inputLine = bufferedReader.readLine()) != null) {
				pageSource += inputLine;
			}
			Log.d(Utils.tag, "Respuesta del servidor: " + pageSource);
			bufferedReader.close();


		} catch (ClientProtocolException e) {
			Log.e(Utils.tag, e.getMessage());
		} catch (IOException e) {
			Log.e(Utils.tag, e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private void showForumNotification(Context context, String msg)
	{
		//Reference for the notification service
		String notificationService = Context.NOTIFICATION_SERVICE;
		NotificationManager notManager = (NotificationManager) context.getSystemService(notificationService);

		//Notification configuration
		int icon = R.drawable.ic_launcher;
		CharSequence stateText = context.getString(R.string.new_forum_comment);
		long time = System.currentTimeMillis();

		Notification notification = new Notification(icon, stateText, time);

		Context contexto = context.getApplicationContext();
		CharSequence title = context.getString(R.string.new_forum_comment_in);
		CharSequence description = msg.replace("\\", "");

		//Intent configuration
		Intent intent = new Intent(contexto, CompetitionActivity.class);
		intent.putExtra("currentLiga", msg);
		intent.putExtra("tab", 5);

		PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		notification.setLatestEventInfo(contexto, title, description, contIntent);

		//AutoCancel: it disappears when it is clicked
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//Send notification
		notManager.notify(1, notification);
	}

	@SuppressWarnings("deprecation")
	private void showGeneralNotification(Context context, String msg)
	{
		//Reference for the notification service
		String notificationService = Context.NOTIFICATION_SERVICE;
		NotificationManager notManager = (NotificationManager) context.getSystemService(notificationService);

		//Notification configuration
		int icon = R.drawable.ic_launcher;
		CharSequence stateText = context.getString(R.string.general_message);
		long time = System.currentTimeMillis();

		Notification notification = new Notification(icon, stateText, time);

		Context contexto = context.getApplicationContext();
		CharSequence title = context.getString(R.string.general_message_title);
		CharSequence description = msg.replace("\\", "");

		//Intent configuration
		Intent intent = new Intent(contexto, FirstActivity.class);
		intent.putExtra("msgGeneral", msg);

		PendingIntent contIntent = PendingIntent.getActivity(contexto, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		notification.setLatestEventInfo(contexto, title, description, contIntent);

		//AutoCancel: it disappears when it is clicked
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//Send notification
		notManager.notify(1, notification);
	}
}
