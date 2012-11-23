package com.iago.asofusa.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.asofusa.R;
import com.iago.asofusa.db.DbHandler;
import com.iago.asofusa.utils.ServerCommunicationManager;
import com.iago.asofusa.utils.PageSourceParser;
import com.iago.asofusa.utils.WebLoader;

public class CompetitionActivity extends SherlockActivity implements ActionBar.TabListener, ActionBar.OnNavigationListener {
	/*
	 * DIALOG_COMMENT. Dialog used to send a comment to the forum
	 */
	private static final int DIALOG_COMMENT = 1;

	/*
	 * currentLiga. It comes with the intent
	 */
	private String currentLiga = "";
	
	/*
	 * competicionUrl. URL associated with currentLiga
	 */
	private String competicionUrl = "";
	
	/*
	 * webSource. Corresponding to competicionUrl
	 */
	private String webSource = "";
	
	
	private Context context;
	private DbHandler dbHandler;
	private SharedPreferences preferences;
	
	private AdView adView;
	private WebView webview;
	private View ratingEntryView;
	private Button buttonUser;
	
	private int currentTab = 0;
	private String textForWebView;
	private boolean downloading = false;
	private String userName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock); 

		context = this;

		preferences = getSharedPreferences("ASOFUSA", Context.MODE_PRIVATE);
		String keyName = getString(R.string.name_key);
		userName = preferences.getString(keyName, "");

		//Custom layout to an AlertDialog
		LayoutInflater factory = LayoutInflater.from(context);
		ratingEntryView = factory.inflate(R.layout.dialog_comment, null);
		buttonUser =  (Button) ratingEntryView.findViewById(R.id.buttonConfigUser);
		if (userName != "") {
			buttonUser.setText(userName.toString());
		}

		buttonUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ConfigUserActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		try {
			dbHandler = new DbHandler(this);
			dbHandler.open();
		} catch (Exception e) {
			//TODO
		}

		currentLiga = getIntent().getExtras().getString("currentLiga");
		competicionUrl = dbHandler.getCompetitionUrlFromLiga(currentLiga);
		setSubtitle(currentLiga);

		setContentView(R.layout.tab_navigation);

		webview = (WebView) findViewById(R.id.webViewCompeticion);
		webview.setWebViewClient(new MyWebViewClient());

		setAdMob();

		String[] opcionesTab = {"Clasificación", "Goleadores", "Deportividad", "Resultados", "Próxima jornada", "Foro"};
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < opcionesTab.length; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(opcionesTab[i]);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setSupportProgressBarIndeterminateVisibility(false);

		if (getIntent().getExtras().getInt("tab") == 5) {
			getSupportActionBar().setSelectedNavigationItem(5);
		} else {
			textForWebView = dbHandler.getTabla(DbHandler.DATABASE_CLASIFICACION_TABLE, DbHandler.KEY_CALIFICACION, competicionUrl);

			if (textForWebView.equals("")) {
				new DownloadData().execute();
				textForWebView = "No hay datos guardados. Actualizamos automáticamente...";
				webview.loadDataWithBaseURL("", textForWebView, "",  "utf-8", "");
			} else {		
				showContent();
			}
		}

	}

	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutCompeticion);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				String keyName = getString(R.string.name_key);
				userName = preferences.getString(keyName, "");
				buttonUser.setText(userName.toString());
			}

			if (resultCode == RESULT_CANCELED) {
			}
		}
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
			setSubtitle("Actualizando");
			if (currentTab == 5) {
				new DownloadForo().execute();
			} else {
				new DownloadData().execute();
			}
			return false;
		} else {
			finish();
			return false;
		}
	}

	private void actualizarDatos() {
		try {
			webSource = WebLoader.load(competicionUrl);

			if (webSource.equals("fail")) {
				//Toast.makeText(ctx, "No se han podido cargar los datos", Toast.LENGTH_SHORT).show();
				Log.w("ASOFUSA", "No se han podido cargar los datos");
			} else {
				dbHandler.updateTabla(DbHandler.DATABASE_CLASIFICACION_TABLE, DbHandler.KEY_CALIFICACION, PageSourceParser.getClasificacion(webSource), competicionUrl);
				dbHandler.updateTabla(DbHandler.DATABASE_GOLEADORES_TABLE, DbHandler.KEY_GOLEADORES, PageSourceParser.getGoleadores(webSource), competicionUrl);
				dbHandler.updateTabla(DbHandler.DATABASE_DEPORTIVIDAD_TABLE, DbHandler.KEY_DEPORTIVIDAD, PageSourceParser.getDeportividad(webSource), competicionUrl);
				dbHandler.updateTabla(DbHandler.DATABASE_RESULTADOS_TABLE, DbHandler.KEY_RESULTADOS, PageSourceParser.getResultados(webSource), competicionUrl);
				dbHandler.updateTabla(DbHandler.DATABASE_PROXIMA_JORNADA_TABLE, DbHandler.KEY_PROXIMA_JORNADA, PageSourceParser.getProximaJornada(webSource), competicionUrl);
			}
		} catch (Exception e) {
			Log.e("ASOFUSA", "reboteeer" + e.getMessage());
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction transaction) {
		setSubtitle(currentLiga);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		currentTab = tab.getPosition();
		setSubtitle(currentLiga);
		showContent();
	}

	private void showContent() {
		switch (currentTab) {
		case 0:
			textForWebView = dbHandler.getTabla(DbHandler.DATABASE_CLASIFICACION_TABLE, DbHandler.KEY_CALIFICACION, competicionUrl);
			break;

		case 1:
			textForWebView = dbHandler.getTabla(DbHandler.DATABASE_GOLEADORES_TABLE, DbHandler.KEY_GOLEADORES, competicionUrl);
			break;

		case 2:
			textForWebView = dbHandler.getTabla(DbHandler.DATABASE_DEPORTIVIDAD_TABLE, DbHandler.KEY_DEPORTIVIDAD, competicionUrl);			
			break;

		case 3:
			textForWebView = dbHandler.getTabla(DbHandler.DATABASE_RESULTADOS_TABLE, DbHandler.KEY_RESULTADOS, competicionUrl);			
			break;

		case 4:
			textForWebView = dbHandler.getTabla(DbHandler.DATABASE_PROXIMA_JORNADA_TABLE, DbHandler.KEY_PROXIMA_JORNADA, competicionUrl);
			break;
		case 5:
			textForWebView = "Recuperando mensajes del foro de " + currentLiga;
			new DownloadForo().execute();
			break;
		default:
			break;
		}

		setSubtitle(currentLiga);

		webview.loadDataWithBaseURL("", textForWebView, "",  "utf-8", "");
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
	}

	private void setSubtitle(String subtitle) {
		getSupportActionBar().setSubtitle(subtitle);	
	}

	private class MyWebViewClient extends WebViewClient {
		@SuppressWarnings("deprecation")
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.equals("insertar_comentario")) {
				showDialog(DIALOG_COMMENT);
			} else if (url.contains("temporada")){
				Intent i = new Intent(context, TeamActivity.class);
				i.putExtra("equipo", url);
				startActivity(i);
			}

			return true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbHandler != null && downloading == false) 
			dbHandler.close();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {

		case DIALOG_COMMENT:
			final EditText editTextComment = (EditText) ratingEntryView.findViewById(R.id.editTextComment);

			dialog = new AlertDialog.Builder(context)
			.setIcon(android.R.drawable.btn_plus)
			.setTitle("Enviar comentario")
			.setView(ratingEntryView)
			.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String comment = editTextComment.getText().toString();
					if (userName != "") {
						ServerCommunicationManager.sendComment(userName, comment, "", currentLiga);
						editTextComment.setText("");
						textForWebView = ServerCommunicationManager.getComments(currentLiga);
						webview.loadDataWithBaseURL("", textForWebView, "",  "utf-8", "");
					} else {
						Toast.makeText(context, "Debe configurar el usuario para poder comentar en los foros", Toast.LENGTH_LONG).show();
					}
				}
			})
			.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			}).create();
			break;
		}

		return dialog;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
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
			Toast.makeText(context, "Datos de " + currentLiga + " actualizados", Toast.LENGTH_SHORT).show();
		}
	}

	private class DownloadForo extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			downloading = true;
			setSupportProgressBarIndeterminateVisibility(true);
		}

		protected Boolean doInBackground(Void... params) {
			textForWebView = ServerCommunicationManager.getComments(currentLiga);
			return true;
		}

		protected void onPostExecute(Boolean message) {
			setSupportProgressBarIndeterminateVisibility(false);
			setSubtitle(currentLiga);
			webview.loadDataWithBaseURL("", textForWebView, "",  "utf-8", "");
			downloading = false;
			Toast.makeText(context, "Datos del foro de " + currentLiga + " actualizados", Toast.LENGTH_SHORT).show();
		}
	}

}