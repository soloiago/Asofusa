package com.iago.asofusa.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.asofusa.R;
import com.iago.asofusa.db.DbHandler;
import com.iago.asofusa.utils.WebLoader;
import com.iago.asofusa.utils.PageSourceParser;

public class TeamActivity extends SherlockActivity {
	private String web;
	private WebView webview;
	private String table;
	private String url;
	private DbHandler dbHandler;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.equipo);
		
		setSupportProgressBarIndeterminateVisibility(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		url = getIntent().getExtras().getString("equipo");
		webview = (WebView) findViewById(R.id.webViewEquipos);
		
		setAdMob();

		try {
			dbHandler = new DbHandler(this);
			dbHandler.open();
		} catch (Exception e) {
			//TODO
		}
		

		table = dbHandler.getEquipo(url);

		if (table.equals("")) {
			new DownloadData().execute();
			table = "No hay datos guardados. Actualizamos automáticamente...";
			webview.loadDataWithBaseURL("", table, "",  "utf-8", "");
		} else {
			showContent();
		}

	}
	
	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutEquipo);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
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

	private void showContent() {
		webview.loadDataWithBaseURL("", table, "",  "utf-8", "");
	}

	private void actualizarDatos() {
		try {
			web = WebLoader.load("http://www.asofusa.com/" + url);
			dbHandler.updateTablaEquipo(url, PageSourceParser.getEquipo(web));
			table = dbHandler.getEquipo(url);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private class DownloadData extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}

		protected Boolean doInBackground(Void... params) {
			actualizarDatos();
			return true;
		}

		protected void onPostExecute(Boolean message) {
			setSupportProgressBarIndeterminateVisibility(false);
			showContent();
		}
	}

	@Override
	public void onDestroy() {
		dbHandler.close();
		super.onDestroy();
	}

}
