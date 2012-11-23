package com.iago.asofusa.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.iago.asofusa.utils.User;
import com.iago.asofusa.utils.Utils;

public class PoolClasificationActivity extends SherlockActivity {
	private Context context;
	private String currentLiga;
		
	private ListView listViewHistorico;
	private ArrayAdapter<User> adapter;
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
	
	private void setClassVariables() {
		context = this;
		currentLiga = getIntent().getExtras().getString("currentLiga");
	}

	private void configLayout() {
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.pool_clasification);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Porra: " + currentLiga);
	}
	
	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutPorraClasificacion);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}

	private void setProgressDialog() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Descargando clasificación...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		
		new DownloadDataAsyncTask().execute();
	}

	private void showListView() {
		List<User> lista = downloadList();

		List<User> clasificacion = new ArrayList<User>();

		for (int i = 0; i < lista.size(); i++) {
			User user = new User();
			user.setName(lista.get(i).getName());
			user.setPuntos(lista.get(i).getPuntos());
			clasificacion.add(user);
		}
		
		Collections.sort(clasificacion, User.COMPARATOR);

		adapter = new ClasificationListAdapter(context, R.layout.user_clasificacion, clasificacion);

		listViewHistorico = (ListView) findViewById(R.id.listViewClasificacion);

		listViewHistorico.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView textViewUserName = (TextView) arg1.findViewById(R.id.userName);
				Intent intent = new Intent(context, PoolHistoryActivity.class);
				intent.putExtra("userName", textViewUserName.getText());
				intent.putExtra("currentLiga", currentLiga);
				startActivity(intent);
			}

		});
	}

	private List<User> downloadList() {
		String args = "?COMPETICION=";

		args = args + currentLiga.replaceAll(" ", "%20");
		args = args.replaceAll("\"", "%22");
		
		XMLParserSAX xmlParser = null;

		try {
			xmlParser = new XMLParserSAX(Data.getPoolClasificationUrl + args);
		} catch (Exception e) {
			Log.e(Utils.tag, e.getMessage());
		}

		List<User> lista = xmlParser.parseUsers();
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

}