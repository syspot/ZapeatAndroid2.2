package com.zapeat.activity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Intent intentMain = new Intent(this, BrowserActivity.class);

		this.startActivity(intentMain);

		super.startMonitoring();

		this.finish();

	}

}
