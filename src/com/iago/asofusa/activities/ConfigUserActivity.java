package com.iago.asofusa.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.iago.asofusa.R;
import com.iago.asofusa.utils.ServerCommunicationManager;
import com.iago.asofusa.utils.Utils;

public class ConfigUserActivity extends SherlockActivity {
	private EditText editTextEmail;
	private EditText editTextName;
	private Button buttonAceptar;
	private SharedPreferences preferences;
	private Context context;
	private String email;
	private String name;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);

		context = this;

		setContentView(R.layout.dialog_user);

		getViews();

		getPreferences();

		checkName();

		getUserEmail();

		setViews();

		confifEvents();
	}

	private void checkName() {
		if (name.equals("")) {
			dialog = new ProgressDialog(this);
			dialog.setMessage("Descargando información de usuario");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);			

			new DownloadDataAsyncTask().execute();
		}

	}

	private void getViews() {
		editTextEmail= (EditText) findViewById(R.id.editTextEmail);
		editTextName = (EditText) findViewById(R.id.editTextName);
		buttonAceptar = (Button) findViewById(R.id.buttonAceptar);
	}

	private void getPreferences() {
		preferences = getSharedPreferences("ASOFUSA", Context.MODE_PRIVATE);
		String keyName = getString(R.string.name_key);
		name = preferences.getString(keyName, "");
	}


	private void getUserEmail() {
		email = Utils.getEmail(context);
	}

	private void setViews() {
		editTextEmail.setText(email);
		editTextName.setText(name);
	}

	private void confifEvents() {
		buttonAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = editTextName.getText().toString();
				String email = editTextEmail.getText().toString();

				if (email.equals("")) {
					Toast.makeText(context, "Sin una cuenta de email en el teléfono no puede continuar. Lo siento", Toast.LENGTH_LONG).show();
				} else {
					if (name.equals("")) {
						Toast.makeText(context, "Debe rellenar el campo \"Nombre\"", Toast.LENGTH_LONG).show();
					} else {
						if (ServerCommunicationManager.insertUser(email, name)) {
							savePreferences(name, email);
							Toast.makeText(context, "¡Ya puede continuar!", Toast.LENGTH_LONG).show();
							finishActivityAndBack();
						} else {
							Toast.makeText(context, "Ya existe un usuario con ese nombre, elija otro diferente", Toast.LENGTH_LONG).show();
						}
					}
				}
			}

			private void finishActivityAndBack() {
				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);     
				finish();
			}

			private void savePreferences(String name, String email) {
				SharedPreferences.Editor editor;
				editor = preferences.edit();
				String keyEmail = getString(R.string.email_key);
				editor.putString(keyEmail, email);
				String keyName = getString(R.string.name_key);
				editor.putString(keyName, name);
				editor.commit();
			}
		});

		findViewById(R.id.buttonCancelar).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);        
				finish();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class DownloadDataAsyncTask extends AsyncTask<Void, Float, Boolean>{
		private String nameRecuperado;

		protected void onPreExecute() {
			dialog.show(); //Mostramos el diálogo antes de comenzar
		}

		protected Boolean doInBackground(Void... params) {
			try {
				nameRecuperado = Utils.recuperarUsuario(Utils.getEmail(context));
			} catch (Exception e) {
				Log.e(Utils.tag, e.getMessage());
			}
			return true;
		}

		protected void onPostExecute(Boolean message) {
			dialog.dismiss();

			if (nameRecuperado.equals("-")) {
				Toast.makeText(context, "Todavía no está en la base de datos", Toast.LENGTH_SHORT).show();
			} else {
				SharedPreferences.Editor editor;
				editor = preferences.edit();
				String keyName = getString(R.string.name_key);
				editor.putString(keyName, nameRecuperado);
				editor.commit();
				name = nameRecuperado;
			}
			setViews();
		}
	}

}
