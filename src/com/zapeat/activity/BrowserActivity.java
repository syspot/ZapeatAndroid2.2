package com.zapeat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zapeat.util.Constantes;

public class BrowserActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.browser);

		WebView ecra = (WebView) findViewById(R.id.wvBrowser);
		
		ecra.setWebViewClient(new WebViewClient() {
			
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
//				view.loadUrl(url);
//				
//				if (url != null && url.endsWith("sair.xhtml")) {
//					sair();
//					Intent intentSair = new Intent(BrowserActivity.this, AuthActivity.class);
//					startActivity(intentSair);
//					finish();
//				}

				return false;
			}
			
		});

		String url = Constantes.Http.URL_ZAPEAT;

		Long promocao = this.getPromocaoNotificada();

		if (promocao != null && Long.valueOf(0).compareTo(promocao) != 0) {

			url = Constantes.Http.URL_ZAPEAT_PROMOCAO + "?promocaoId=" + promocao;

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

			public void onProgressChanged(WebView view, int progress) {
				BrowserActivity.this.setProgress(progress * 100);
			}
		});

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		WebView ecra = (WebView) findViewById(R.id.wvBrowser);
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (ecra.canGoBack() == true) {
					ecra.goBack();
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

}
