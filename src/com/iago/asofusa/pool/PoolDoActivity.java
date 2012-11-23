package com.iago.asofusa.pool;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

public class PoolDoActivity extends SherlockActivity {
	private Context context;
	private String currentLiga;
	private String userName;
	private SharedPreferences prefs;
	
	private String partidoId;
	private ListView listViewVotedMatches;
	private ListView listViewNoVotedMatches;
	private ArrayAdapter<Partido> adapterListViewVotedMatches;
	private ArrayAdapter<Partido> adapterListViewNoVotedMatches;
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				new SendDataAsyncTask().execute(data.getExtras().getString("resultado"));
			}

			if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	private void setClassVariables() {
		context = this;
		currentLiga = getIntent().getExtras().getString("currentLiga");
		
		prefs = getSharedPreferences("ASOFUSA", Context.MODE_PRIVATE);
		String keyName = getString(R.string.name_key);
		userName = prefs.getString(keyName, "");
	}
	
	private void configLayout() {
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.pool_do);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Porra: " + currentLiga);
		getSupportActionBar().setSubtitle("User: " + userName);
	}

	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutPoolDoPool);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}
	
	private void setProgressDialog() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Descargando datos de porras...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);	

		new DownloadDataAsyncTask().execute();
	}

	private void showListView() {
		List<Partido> lista = downloadList();

		List<Partido> partidosVotados = new ArrayList<Partido>();
		List<Partido> partidosNoVotados = new ArrayList<Partido>();

		for (int i = 0; i < lista.size(); i++) {
			if (lista.get(i).getVotado().equals("1")) {
				Partido partido = new Partido();
				partido.setId(lista.get(i).getId());
				partido.setPartido(lista.get(i).getPartido());
				partido.setResultadoPorra(lista.get(i).getResultadoPorra());
				partido.setFecha(lista.get(i).getFecha());
				partidosVotados.add(partido);
			} else {
				Partido partido = new Partido();
				partido.setId(lista.get(i).getId());
				partido.setPartido(lista.get(i).getPartido());
				partido.setResultadoPorra("-");
				partido.setFecha(lista.get(i).getFecha());
				partidosNoVotados.add(partido);
			}
		}

		adapterListViewVotedMatches = new PartidoVotadoListAdapter(context, R.layout.partido_votado, partidosNoVotados);
		adapterListViewNoVotedMatches = new PartidoVotadoListAdapter(context, R.layout.partido_votado, partidosVotados);

		listViewVotedMatches = (ListView) findViewById(R.id.listView1);
		listViewNoVotedMatches = (ListView) findViewById(R.id.listView2);

		listViewVotedMatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView tv = (TextView) arg1.findViewById(R.id.partido);
				String partidoVoto = tv.getText().toString();
				TextView tv2 = (TextView) arg1.findViewById(R.id.idPartido);
				partidoId = tv2.getText().toString();

				Intent intent = new Intent(context, PoolPoolDialogActivity.class);
				intent.putExtra("partidoVoto", partidoVoto);
				startActivityForResult(intent, 1);
			}

		});
	}

	private List<Partido> downloadList() {
		String args = currentLiga.replaceAll(" ", "%20");
		args = args.replaceAll("\"", "%22");

		String urlEnd = "?USER=" + userName + "&COMPETICION=" + args;

		XMLParserSAX xmlParser = null;

		try {
			xmlParser = new XMLParserSAX(Data.getMatchesListUrl + urlEnd);
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}

		List<Partido> lista = xmlParser.parsePartidos();
		return lista;
	}

	private class DownloadDataAsyncTask extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			dialog.setMessage("Descargando datos de porras...");
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Boolean doInBackground(Void... params) {
			showListView();
			return true;
		}

		protected void onPostExecute(Boolean message) {
			dialog.dismiss();
			listViewVotedMatches.setAdapter(adapterListViewVotedMatches);
			listViewNoVotedMatches.setAdapter(adapterListViewNoVotedMatches);
		}
	}

	private class SendDataAsyncTask extends AsyncTask<String, Float, Boolean>{

		protected void onPreExecute() {
			dialog.setMessage("Envíando porra...");
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Boolean doInBackground(String... params) {
			String resultado = params[0];

			try {
				Utils.sendResult(userName, resultado, partidoId, currentLiga);
			} catch (Exception e) {
				Log.e(Utils.tag, e.getMessage());
			}
			return true;
		}

		protected void onPostExecute(Boolean message) {
			dialog.dismiss();
			new DownloadDataAsyncTask().execute();
		}

	}
	
}

