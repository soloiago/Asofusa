package com.iago.asofusa.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.iago.asofusa.GmcManager;
import com.iago.asofusa.R;
import com.iago.asofusa.db.DbHandler;
import com.iago.asofusa.pool.PoolMainActivity;
import com.iago.asofusa.utils.PageSourceParser;
import com.iago.asofusa.utils.Utils;
import com.iago.asofusa.utils.WebLoader;

public class FirstActivity extends SherlockActivity {
	final private int DIALOG_ALERT = 0;
	final private int DIALOG_INFO = 1;

	private Context ctx;
	private DbHandler dbHandler;
	private String [] arrayCompeticiones;
	private String currentLiga;
	private String userName;
	private int preferenceSpinner;
	private ProgressDialog dialog = null;
	private String msgGeneral;

	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	protected boolean[] selections;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);

		ctx = this;

		//Load partially transparent black background
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));

		setContentView(R.layout.main);

		prefs = getSharedPreferences("ASOFUSA", Context.MODE_PRIVATE);
		String keyName = getString(R.string.name_key);
		userName = prefs.getString(keyName, "");

		dbHandler = new DbHandler(ctx);
		dbHandler.open();

		getArraysForAlertsAndSpinner();

		setSpinner();

		configButtons();

		registerGCM();

		if (getIntent().getStringExtra("msgGeneral") != null) {
			msgGeneral = getIntent().getExtras().getString("msgGeneral");
			showDialog(DIALOG_INFO);
		}

	}

	/*
	 * registerGCM()
	 * Registramos la primera vez para mandar la notificación general
	 */
	private void registerGCM() {
		int registered = prefs.getInt("registered", 0);

		if (registered == 0) {
			GmcManager.registerGCM(ctx);
			editor = prefs.edit(); 
			editor.putInt("registered", 1);
			editor.commit();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {	
		Dialog out = null;

		switch (id) {
		case DIALOG_ALERT:
			out = new AlertDialog.Builder( this )
			.setTitle("Alertas Foro")
			.setMultiChoiceItems(arrayCompeticiones, selections, new DialogSelectionClickHandler() )
			.setPositiveButton("Aceptar", new DialogButtonClickHandler() )
			.setNegativeButton("Cancelar", null)
			.create();
			break;
		case DIALOG_INFO:
			out = new AlertDialog.Builder( this )
			.setTitle("Información")
			.setMessage(msgGeneral)
			.setPositiveButton("Aceptar", null)
			.create();
			break;
		default:
			break;
		}

		return out;

	}

	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean selected )
		{
			selections[clicked] = selected;
			Log.i(Utils.tag, arrayCompeticiones[clicked] + " selected: " + selected );
		}
	}

	public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked ) {
			switch( clicked ) {
			case DialogInterface.BUTTON_POSITIVE:
				updateAlerts();
				GmcManager.registerGCM(ctx);
				break;
			}
		}
	}
	protected void updateAlerts()
	{
		for( int i = 0; i < arrayCompeticiones.length; i++ ){
			dbHandler.updateAlerts(arrayCompeticiones[i], selections[i]);
			Log.i(Utils.tag, arrayCompeticiones[i] + " selected: " + selections[i] );
		}
	}

	private void configButtons() {
		findViewById(R.id.buttonCalendario).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, CalendarActivity.class);
				intent.putExtra("currentLiga", currentLiga);
				setPreference(preferenceSpinner);
				startActivity(intent);
			}
		});

		findViewById(R.id.buttonCompeticion).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, CompetitionActivity.class);
				intent.putExtra("currentLiga", currentLiga);
				setPreference(preferenceSpinner);
				startActivity(intent);
			}
		});

		findViewById(R.id.buttonPublicidad).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent send = new Intent(Intent.ACTION_SENDTO);
				String uriText;

				uriText = "mailto:soloiago@gmail.com" + 
						"?subject=Publicidad en Asofusa Mobile";
				uriText = uriText.replace(" ", "%20");
				Uri uri = Uri.parse(uriText);

				send.setData(uri);
				startActivity(Intent.createChooser(send, "Enviar e-mail..."));
			}
		});

		findViewById(R.id.imageButtonRefreshTeams).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog = new ProgressDialog(ctx);
				dialog.setMessage("Descargando lista de competiciones...");
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setCancelable(false);			

				new UpdateCompetitions().execute();
			}
		});

		findViewById(R.id.buttonAlertas).setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				getArraysForAlertsAndSpinner();
				showDialog(DIALOG_ALERT);

			}
		});
		
		findViewById(R.id.buttonConfigUser).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, ConfigUserActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
		
	}

	protected void getArraysForAlertsAndSpinner() {
		List <String> listaCompeticiones = dbHandler.getListaCompeticiones();
		arrayCompeticiones = listaCompeticiones.toArray(new String[listaCompeticiones.size()]);

		List <Integer> listaAlertas = dbHandler.getListaAlertas();
		selections =  new boolean[listaAlertas.size()];
		for (int i = 0; i < listaAlertas.size(); i++) {
			selections[i] = listaAlertas.get(i) > 0;
		}
	}

	protected void setPreference(int position) {
		editor = prefs.edit(); 
		editor.putInt("ligaPreference", preferenceSpinner);
		editor.commit();
	}

	private void setSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, arrayCompeticiones);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(arrayAdapter);

		int lastPreference = prefs.getInt("ligaPreference", 0);
		spinner.setSelection(lastPreference);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
				currentLiga = arrayCompeticiones[position];
				preferenceSpinner = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				return;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHandler != null) 
			dbHandler.close();
	}

	private class UpdateCompetitions extends AsyncTask<Void, Float, Boolean>{

		protected void onPreExecute() {
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Boolean doInBackground(Void... params) {
			updateCompetition();
			return true;
		}

		protected void onPostExecute(Boolean message) {
			getArraysForAlertsAndSpinner();
			setSpinner();
			dialog.dismiss();
			Toast.makeText(ctx, "Vuelva a configurar las alertas de los foros", Toast.LENGTH_LONG).show();
		}
	}

	public void updateCompetition() {
		String pageSource;
		try {
			pageSource = WebLoader.load("http://www.asofusa.com");
			dbHandler.updateListCompeticiones(PageSourceParser.getListaCompeticiones(pageSource));
		} catch (SQLException e) {
			Log.e(Utils.tag, "SQLException: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				String keyName = getString(R.string.name_key);
				userName = prefs.getString(keyName, "");
			}

			if (resultCode == RESULT_CANCELED) {
			}
		}
	}
}
