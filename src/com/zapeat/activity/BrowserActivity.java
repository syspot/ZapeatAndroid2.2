package com.zapeat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zapeat.util.Constantes;

public class BrowserActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (getIntent().getBooleanExtra("checkin", false)) {
			Toast.makeText(this, "Check-in realizado com sucesso!", Toast.LENGTH_SHORT).show();
		}

		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.browser);

		WebView ecra = (WebView) findViewById(R.id.wvBrowser);

		ecra.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if (!isOnline()) {

					view.stopLoading();

					BrowserActivity.this.startActivity(new Intent(BrowserActivity.this, SemConexaoActivity.class));

					BrowserActivity.this.finish();

				}

				String urlComEspaco = url.replaceAll("%20", " ");

				if (urlComEspaco.contains("sair.xhtml")) {
					String token = urlComEspaco.substring(urlComEspaco.indexOf("[") + 1, urlComEspaco.lastIndexOf("]"));

					SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

					editor.remove("token");

					editor.commit();

				} else if (url.contains("[")) {

					String token = urlComEspaco.substring(urlComEspaco.indexOf("[") + 1, urlComEspaco.lastIndexOf("]"));

					SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

					editor.putString("token", token);

					editor.commit();

				}

				if (url.contains("detalhe_checkin.xhtml")) {

					String token = urlComEspaco.substring(urlComEspaco.indexOf("[") + 1, urlComEspaco.lastIndexOf("]"));

					String id = urlComEspaco.substring(url.indexOf("&") + 1, url.lastIndexOf("&"));

					String nome = urlComEspaco.substring(url.lastIndexOf("&") + 1, urlComEspaco.length());

					Intent intent = new Intent(BrowserActivity.this, CheckInActivity.class);

					intent.putExtra("id", Long.valueOf(id));

					intent.putExtra("nome", nome);

					intent.putExtra("token", token);

					BrowserActivity.this.startActivity(intent);

				}

				if (url.startsWith("http:") || url.startsWith("https:")) {
					return false;
				}

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

				startActivity(intent);

				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				view.stopLoading();
				BrowserActivity.this.startActivity(new Intent(BrowserActivity.this, SemConexaoActivity.class));
				BrowserActivity.this.finish();
			}

		});

		String url = Constantes.Http.URL_ZAPEAT + "?query=ok";

		Location location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (location == null) {

			location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);

		}

		if (location != null) {

			Double latitude = location.getLatitude();
			Double longitude = location.getLongitude();

			url = url + "&location=(" + latitude + ", " + longitude + ")";

		}

		SharedPreferences shared = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0);

		String token = shared.getString("token", "");

		if (!TextUtils.isEmpty(token)) {

			url = url + "&token=" + token;

		}

		ecra.loadUrl(url);

		ecra.getSettings().setJavaScriptEnabled(true);
		ecra.getSettings().setGeolocationEnabled(true);
		ecra.getSettings().setRenderPriority(RenderPriority.HIGH);
		ecra.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

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
					startActivity(new Intent(this, CategoriaActivity.class));
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

}
