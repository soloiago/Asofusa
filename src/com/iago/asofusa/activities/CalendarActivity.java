package com.iago.asofusa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.asofusa.R;
import com.iago.asofusa.db.DbHandler;
import com.iago.asofusa.utils.PageSourceParser;
import com.iago.asofusa.utils.Utils;
import com.iago.asofusa.utils.WebLoader;

/**
 * CalendarActivity. It gets the competition calendar or get it from the DB if it is already saved there.
 * @author iago
 *
 */
public class CalendarActivity extends SherlockActivity {
	private Context context;
	private String currentLiga;
	private String url;

	private String pageSource;
	private WebView webview;
	private AdView adView;

	private String textForWebView;

	private DbHandler dbHandler;

	private boolean downloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		openDataBase();

		configLayout();

		getViews();

		setClassVariables();
		
		configViews();

		setAdMob();

		configViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Refresh")
		.setIcon(R.drawable.ic_refresh)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String option = item.getTitle().toString();

		if (option.equals("Refresh")) {
			new DownloadData().execute();
			return true;
		} else if (option.equals("Asofusa")) {
			finish();
		}

		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbHandler != null && downloading == false) {
			dbHandler.close();
		}
	}

	private void openDataBase() {
		try {
			dbHandler = new DbHandler(this);
			dbHandler.open();
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}
	}

	private void configLayout() {
		setTheme(R.style.Theme_Sherlock);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.equipo);
		setSupportProgressBarIndeterminateVisibility(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getViews() {
		webview = (WebView) findViewById(R.id.webViewEquipos);
	}

	private void setClassVariables() {
		context = this;

		currentLiga = getIntent().getExtras().getString("currentLiga");
		url = dbHandler.getCalendarUrlFromLiga(currentLiga);
		textForWebView = dbHandler.getCalendario(url);
	}
	
	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutEquipo);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}

	private void configViews() {
		webview.setWebViewClient(new MyWebViewClient());		
		
		if (textForWebView.equals("")) {
			new DownloadData().execute();
			textForWebView = "No hay datos guardados. Actualizamos automáticamente...";
		} 
		
		showContent();
	}

	private void showContent() {
		webview.loadDataWithBaseURL("", textForWebView, "",  "utf-8", "");
	}

	private void actualizarDatos() {
		try {
			pageSource = WebLoader.load(url);
			dbHandler.updateTablaCalendario(url, PageSourceParser.getCalendario(pageSource));
			textForWebView = dbHandler.getCalendario(url);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("temporada")){
				Intent i = new Intent(context, TeamActivity.class);
				int auxiliar = url.indexOf("temporada=");
				url = url.substring(0, auxiliar + "temporada=".length()) + "9" + url.substring(auxiliar + "temporada=".length() + 1, url.length());
				i.putExtra("equipo", url);
				startActivity(i);
			}

			return true;
		}
	}

	private class DownloadData extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			downloading = true;
			setSupportProgressBarIndeterminateVisibility(true);
		}

		protected Boolean doInBackground(Void... params) {
			actualizarDatos();
			return true;
		}

		protected void onPostExecute(Boolean message) {
			setSupportProgressBarIndeterminateVisibility(false);
			showContent();
			downloading = false;
			Toast.makeText(context, "Calendario " + currentLiga + " actualizados", Toast.LENGTH_SHORT).show();
		}
	}
	
}
