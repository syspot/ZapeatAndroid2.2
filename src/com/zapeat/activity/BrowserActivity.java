package com.zapeat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zapeat.util.Constantes;

public class BrowserActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		WebView ecra = (WebView) findViewById(R.id.wvBrowser);
		ecra.setWebViewClient(new WebViewClient() { 
            public boolean shouldOverrideUrlLoading(WebView view, String url){
            	view.loadUrl(url); 
            	
            	if(url!=null && url.endsWith("sair.xhtml")) {
            		sair();
            		Intent intentSair = new Intent(BrowserActivity.this, AuthActivity.class);
        			startActivity(intentSair);
        			finish();
            	}
            	
                return false; 
            } 
        });


		String url = Constantes.Http.URL_ZAPEAT + "?usuarioId=" + super.getUsuarioLogado().getToken();

		Long promocao = this.getPromocaoNotificada();

		if (promocao != null && Long.valueOf(0).compareTo(promocao) != 0) {
			url = Constantes.Http.URL_ZAPEAT_PROMOCAO + "?promocaoId=" + promocao + "&usuarioId=" + super.getUsuarioLogado().getToken();
		}

		ecra.loadUrl(url);

		ecra.getSettings().setJavaScriptEnabled(true);
		ecra.getSettings().setGeolocationEnabled(true);
		ecra.getSettings().setAppCacheEnabled(true);
		ecra.getSettings().setDatabaseEnabled(true);
		ecra.getSettings().setDomStorageEnabled(true);
		ecra.setWebChromeClient(new WebChromeClient() {
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}
		});

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

	private Long getPromocaoNotificada() {

		SharedPreferences shared = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0);

		Long promocao = shared.getLong(Constantes.Preferencias.PROMOCAO_NOTIFICADA, 0);

		if (Long.valueOf(0).compareTo(promocao) != 0) {

			SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

			editor.remove(Constantes.Preferencias.PROMOCAO_NOTIFICADA);

			editor.commit();

		}

		return promocao;

	}

}
