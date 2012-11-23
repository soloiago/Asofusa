package com.iago.asofusa.pool;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.iago.asofusa.R;

public class PoolPoolDialogActivity extends SherlockActivity {
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		configLayout();

		setAdMob();
		
		setNames();

		configEvents();
	}

	private void configLayout() {
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.pool_voto_dialog);
	}
	
	private void setAdMob() {
		adView = new AdView(this, AdSize.BANNER, "a1506af03aa1be5");
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutPorraVoto);
		adView.setGravity(Gravity.BOTTOM);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
	}
	
	private void setNames() {
		String partidoVoto = getIntent().getExtras().getString("partidoVoto");

		TextView textViewMatchTitle = (TextView) findViewById(R.id.partidoVoto);
		textViewMatchTitle.setText(partidoVoto);
	}
	
	private void configEvents() {
		findViewById(R.id.buttonEnviarVoto).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView golesLocal = (TextView) findViewById(R.id.editTextGolesLocal);
				TextView golesVisitante = (TextView) findViewById(R.id.editTextGolesVisitante);

				Intent returnIntent = new Intent();
				returnIntent.putExtra("resultado", golesLocal.getText().toString() + "-" + golesVisitante.getText().toString());
				setResult(RESULT_OK, returnIntent);     
				finish();
			}
		});

		findViewById(R.id.buttonCancelarVoto).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);     
				finish();
			}
		});
	}
	
}
