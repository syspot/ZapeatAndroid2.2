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

		Integer usuario = shared.getInt(Constantes.Preferencias.USUARIO_LOGADO, 0);

		Intent intentMain = null;

		if (usuario.intValue() == 0) {

			intentMain = new Intent(this, ZapeatAuthActivity.class);

		} else {

			intentMain = new Intent(this, PromocaoListActivity.class);

		}

		this.startActivity(intentMain);

		this.finish();
	}

}
