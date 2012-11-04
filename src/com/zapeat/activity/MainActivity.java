package com.zapeat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends DefaultActivity implements Runnable {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Handler handler = new Handler();
		handler.postDelayed(this, 3000);

	}

	public void run() {

		Intent intentMain = new Intent(this, BrowserActivity.class);

		this.startActivity(intentMain);

		super.startMonitoring();

		this.finish();
	}

}
