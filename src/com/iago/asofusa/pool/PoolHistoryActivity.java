package com.iago.asofusa.pool;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.asofusa.R;
import com.iago.asofusa.utils.Data;
import com.iago.asofusa.utils.Partido;
import com.iago.asofusa.utils.PartidoVotadoListAdapter;
import com.iago.asofusa.utils.Utils;

public class PoolHistoryActivity extends SherlockActivity {
	private ListView listViewHistorico;
	private Context context;
	private ArrayAdapter<Partido> adapter;
	private String currentLiga;
	private SharedPreferences prefs;
	private String userName;
	private ProgressDialog dialog;
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setClassVariables();
		
		configLayout();
		
		setAdMob();

		setProgressDialog();
	}

	private void setClassVariables() {
		context = this;
		currentLiga = getIntent().getExtras().getString("currentLiga");
		userName = getIntent().getExtras().getString("userName");

		if (userName == null) {
			prefs = getSharedPreferences("ASOFUSA", Context.MODE_PRIVATE);
			String keyName = getString(R.string.name_key);
			userName = prefs.getString(keyName, "");
		}
	}
	
	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutPorraHistorico);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}

	private void configLayout() {
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.pool_history);
	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		getSupportActionBar().setTitle("Porra: " + currentLiga);
		getSupportActionBar().setSubtitle("User: " + userName);
	}

	private void setProgressDialog() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Descargando histórico de porras...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);			

		new DownloadDataAsyncTask().execute();
	}
	
	private void showListView() {
		List<Partido> lista = dowloadList();

		List<Partido> historico = new ArrayList<Partido>();

		for (int i = 0; i < lista.size(); i++) {
			Partido partido = new Partido();
			partido.setId(lista.get(i).getId());
			partido.setPartido(lista.get(i).getPartido());
			partido.setResultadoPorra(lista.get(i).getResultadoPorra() + " (" + lista.get(i).getResultadoReal() + ")" + ": +" +
					lista.get(i).getPuntos());
			partido.setFecha(lista.get(i).getFecha());
			historico.add(partido);
		}

		adapter = new PartidoVotadoListAdapter(context, R.layout.partido_votado, historico);

		listViewHistorico = (ListView) findViewById(R.id.listViewHistorico);

	}

	private List<Partido> dowloadList() {
		currentLiga = currentLiga.replaceAll(" ", "%20");
		currentLiga = currentLiga.replaceAll("\"", "%22");

		String args = "?USER=" + userName + "&COMPETICION=" + currentLiga;


		XMLParserSAX xmlParser = null;

		try {
			xmlParser = new XMLParserSAX(Data.getPoolHistoryUrl + args);
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}

		List<Partido> lista = xmlParser.parsePartidos();
		return lista;
	}
	
	private class DownloadDataAsyncTask extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Boolean doInBackground(Void... params) {
			showListView();
			return true;
		}

		protected void onPostExecute(Boolean message) {
			dialog.dismiss();
			listViewHistorico.setAdapter(adapter);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return false;
	}

}