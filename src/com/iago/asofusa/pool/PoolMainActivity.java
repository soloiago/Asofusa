package com.iago.asofusa.pool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.iago.asofusa.R;
import com.iago.asofusa.activities.ConfigUserActivity;

public class PoolMainActivity extends SherlockActivity {
	private final int DIALOG_INFO = 0;
	
	private Context context;
	private String currentLiga;
	private SharedPreferences prefs;
	private Button modificarUserName;
	private String userName;
	
	private String infoDialogText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setClassVariables();
		
		configLayout();
		
		findViews();
		
		setNames();
		
		configEvents();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				String keyName = getString(R.string.name_key);
				userName = prefs.getString(keyName, "");
				modificarUserName.setText(userName);
			}

			if (resultCode == RESULT_CANCELED) {
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		setNames();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch(id)
		{
		case DIALOG_INFO:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Informacion");
			builder.setMessage(Html.fromHtml(infoDialogText));
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			dialog = builder.create();
		}
		
		return dialog; 
	}
	
	private void setClassVariables() {
		context = this;
		infoDialogText = context.getString(R.string.pool_dialog_info);
		currentLiga = getIntent().getExtras().getString("currentLiga");
		prefs = getSharedPreferences("ASOFUSA", Context.MODE_PRIVATE);
		String keyName = getString(R.string.name_key);
		userName = prefs.getString(keyName, "");
	}
	
	private void configLayout() {
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.pool_main);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Porra: " + currentLiga);
	}
	
	private void findViews() { 
		modificarUserName = (Button) findViewById(R.id.buttonModificarUserName);
	}

	private void setNames() {
		getSupportActionBar().setSubtitle("User: " + userName);
		modificarUserName.setText(userName);
	}

	private void configEvents() {
		modificarUserName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ConfigUserActivity.class);
				startActivityForResult(intent, 1);	
			}
		});
		
		findViewById(R.id.buttonPorraCalificacion).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PoolClasificationActivity.class);
				intent.putExtra("currentLiga", currentLiga);
				startActivity(intent);
			}
		});

		findViewById(R.id.buttonPorraHacer).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PoolDoActivity.class);
				intent.putExtra("currentLiga", currentLiga);
				startActivity(intent);
			}
		});

		findViewById(R.id.buttonPorraHistorico).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PoolHistoryActivity.class);
				intent.putExtra("currentLiga", currentLiga);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.imageButtonInfo).setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_INFO);
			}
		});
	}

}
