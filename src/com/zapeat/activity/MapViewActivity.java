package com.zapeat.activity;

import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.zapeat.dao.PromocaoDAO;
import com.zapeat.model.Promocao;
import com.zapeat.model.PromocaoOverlay;
import com.zapeat.util.Constantes;
import com.zapeat.util.Utilitario;

@SuppressLint("ShowToast")
public class MapViewActivity extends MapActivity {

	private  final int ZOOM = 11;
	private final String ASK_DISTANCIA_MAXIMA = "Qual distância máxima?";
	private final String SEM_LOCALIZACAO = "Não foi possível obter localização atual, verifique as configurações de localização";
	private final String NENHUMA_PROMOÇÃO_ENCONTRADA = "Nenhuma promoção encontrada!";
	private MapView mapView;
	private AlertDialog dialog;
	private String distanciaFiltro;
	private final String KM_1 = "1 Km";
	private final String KM_3 = "3 Km";
	private final String KM_5 = "5 Km";
	private final String SEM_DISTANCIA = "Ilimitada";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_view);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		this.initPromocoes();
		this.initDialog();
		this.redirectLocation();
	}

	private void redirectLocation() {

		mapView.getController().setZoom(ZOOM);

		Location atual = PollService.getLastLocation();

		if (atual != null) {

			GeoPoint geoPoint = new GeoPoint(Double.valueOf(atual.getLatitude() * 1E6).intValue(), Double.valueOf(atual.getLongitude() * 16).intValue());

			mapView.getController().animateTo(geoPoint);

		} else {

			Toast.makeText(this, SEM_LOCALIZACAO, Toast.LENGTH_LONG);

		}

	}

	private void initDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(ASK_DISTANCIA_MAXIMA);

		final CharSequence[] items = { SEM_DISTANCIA, KM_1, KM_3, KM_5 };

		builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				filtrar();
				dialog.cancel();
			}
		});

		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				distanciaFiltro = items[id].toString();
				if (SEM_DISTANCIA.equals(distanciaFiltro)) {
					distanciaFiltro = null;
				}
			}
		});

		this.dialog = builder.create();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_map_view, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.filtrarMapa:

			this.dialog.show();

			return true;

		case R.id.promocoesMapa:

			Intent intentMapa = new Intent(MapViewActivity.this, PromocaoListActivity.class);

			startActivity(intentMapa);

			finish();

			return true;

		case R.id.webMapa:

			Intent intent = new Intent(MapViewActivity.this, BrowserActivity.class);

			startActivity(intent);

			finish();

			return true;

		case R.id.sair:

			sair();

			Intent intentSair = new Intent(MapViewActivity.this, AuthActivity.class);

			startActivity(intentSair);

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void sair() {

		SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

		editor.remove(Constantes.Preferencias.USUARIO_LOGADO);

		editor.commit();

		stopService(new Intent(Constantes.Services.MONITORING));

		Utilitario.cleanDirectory();

	}

	private void initPromocoes() {

		this.mapView.getOverlays().clear();

		this.mapView.invalidate();

		GeoPoint geoPoint = null;
		Promocao promocao = null;
		List<Promocao> promocoes = new PromocaoDAO().pesquisarTodas(getApplicationContext());
		this.mapView.getOverlays().add(new PromocaoOverlay(this.getResources().getDrawable(R.drawable.talheres), this));
		PromocaoOverlay overlay = null;

		for (Promocao promo : promocoes) {

			geoPoint = new GeoPoint(Double.valueOf(promo.getLatitude() * 1E6).intValue(), Double.valueOf(promo.getLongitude() * 1E6).intValue());
			promocao = new Promocao(geoPoint, promo);
			overlay = new PromocaoOverlay(this.getResources().getDrawable(R.drawable.talheres), this);
			overlay.addOverlay(promocao);
			this.mapView.getOverlays().add(overlay);

		}

		this.mapView.postInvalidate();

	}

	private void filtrar() {

		if (this.distanciaFiltro != null && !"".equals(distanciaFiltro)) {

			Location location = PollService.getLastLocation();

			if (location != null) {

				this.initPromocoesByLocation(location);

			} else {

				Toast t = Toast.makeText(this, SEM_LOCALIZACAO, Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();

				this.initPromocoes();

			}

		} else {

			this.initPromocoes();

		}

	}

	private float getDistanciaFiltro() {

		if (KM_1.equals(this.distanciaFiltro)) {
			return 1000f;
		}

		if (KM_3.equals(this.distanciaFiltro)) {
			return 3000f;
		}

		if (KM_5.equals(this.distanciaFiltro)) {
			return 5000f;
		}
		return 0;

	}

	private void initPromocoesByLocation(Location location) {

		List<Promocao> promocoes = new PromocaoDAO().pesquisarTodas(getApplicationContext());

		Iterator<Promocao> iterador = promocoes.iterator();

		this.mapView.getOverlays().clear();

		this.mapView.invalidate();

		PromocaoOverlay promocaoOverlay = null;

		Promocao promo = null;

		float distancia = this.getDistanciaFiltro();

		float[] distanciaCalculada = new float[4];

		this.mapView.getOverlays().add(new PromocaoOverlay(this.getResources().getDrawable(R.drawable.talheres), this));

		while (iterador.hasNext()) {

			promo = iterador.next();

			Location.distanceBetween(promo.getLatitude(), promo.getLongitude(), location.getLatitude(), location.getLongitude(), distanciaCalculada);

			if (distanciaCalculada != null && distanciaCalculada.length >= 0 && distanciaCalculada[0] > distancia) {
				iterador.remove();
			}

		}

		GeoPoint geoPoint = null;
		Promocao promocao = null;

		if (promocoes.isEmpty()) {
			Toast t = Toast.makeText(this, NENHUMA_PROMOÇÃO_ENCONTRADA, Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		} else {

			for (Promocao promocaoMapa : promocoes) {
				geoPoint = new GeoPoint(Double.valueOf(promocaoMapa.getLatitude() * 1E6).intValue(), Double.valueOf(promocaoMapa.getLongitude() * 1E6).intValue());
				promocao = new Promocao(geoPoint, promocaoMapa);
				promocaoOverlay = new PromocaoOverlay(this.getResources().getDrawable(R.drawable.talheres), this);
				promocaoOverlay.addOverlay(promocao);
				this.mapView.getOverlays().add(promocaoOverlay);
			}

		}

		this.mapView.postInvalidate();

	}

}
