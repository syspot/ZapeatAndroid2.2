package com.zapeat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zapeat.util.Constantes;

public class BrowserActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		WebView ecra = (WebView) findViewById(R.id.wvBrowser);
		ecra.setWebViewClient(new WebViewClient());
		ecra.loadUrl(Constantes.Http.URL_ZAPEAT);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.promocoes:

			Intent intent = new Intent(this, PromocaoListActivity.class);

			startActivity(intent);

			finish();

			return true;

		case R.id.mapa:

			Intent intentMapa = new Intent(BrowserActivity.this, MapViewActivity.class);

			startActivity(intentMapa);

			finish();

			return true;

		case R.id.sair:

			super.sair();

			Intent intentSair = new Intent(this, AuthActivity.class);

			startActivity(intentSair);

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
