package com.zapeat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.zapeat.util.Constantes;

public class MainActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences shared = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0);

		String token = shared.getString(Constantes.Preferencias.USUARIO_LOGADO, null);

		Intent intentMain = null;

		if (token == null) {

			intentMain = new Intent(this, AuthActivity.class);

		} else {

			intentMain = new Intent(this, BrowserActivity.class);

		}

		this.startActivity(intentMain);

		this.finish();
	}

}
