package com.zapeat.activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.zapeat.dao.PromocaoDAO;
import com.zapeat.http.HttpUtil;
import com.zapeat.model.Promocao;
import com.zapeat.model.Usuario;
import com.zapeat.util.Constantes;

public class PollService extends Service {

	private LocationManager locationManager;
	private PromocaoDAO promocaoDAO = new PromocaoDAO();

	@Override
	public void onStart(Intent intent, int startId) {

		if (getUsuarioLogado() != null) {

			this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, -1, Constantes.GPS.DISTANCIA, new ZapeatLocationListener());
			new LocationTask(PollService.this).execute();

		} else {
			stopSelf();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public Usuario getUsuarioLogado() {

		SharedPreferences prefs = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0);

		Usuario usuario = new Usuario();

		Integer id = prefs.getInt(Constantes.Preferencias.USUARIO_LOGADO, 0);

		if (id == 0) {
			return null;
		}

		usuario.setId(id);

		return usuario;

	}

	private class LocationTask extends AsyncTask<String, Void, Boolean> {

		private Service service;

		public LocationTask(Service service) {
			this.service = service;
		}

		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(final String... args) {
			initStrictMode();
			List<Promocao> promocoesNovas = null;
			List<Promocao> promocoesAtuais = null;
			Promocao nova = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

			while (true) {

				if (getUsuarioLogado() != null) {

					try {

						promocoesNovas = HttpUtil.pesquisarPromocoes(getUsuarioLogado());

						promocoesAtuais = promocaoDAO.pesquisarTodas(getApplicationContext());

						Iterator<Promocao> iterador = promocoesNovas.iterator();

						while (iterador.hasNext()) {

							nova = iterador.next();

							if (promocoesAtuais.contains(nova)) {
								iterador.remove();
							}
						}

						promocaoDAO.inserir(promocoesNovas, getApplicationContext());

						SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

						editor.remove(Constantes.Preferencias.ULTIMA_ATUALIZACAO);

						editor.putString(Constantes.Preferencias.ULTIMA_ATUALIZACAO, dateFormat.format(new Date()));

						editor.commit();

						Thread.sleep(Constantes.Services.TRES_HORAS);

					} catch (Exception ex) {

					}
				}

			}
		}
	}

	private class ZapeatLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			float[] result = new float[10];

			for (Promocao promocao : promocaoDAO.pesquisar(getApplicationContext())) {

				try {

					Location.distanceBetween(promocao.getLatitude(), promocao.getLongitude(), location.getLatitude(), location.getLongitude(), result);

					if (result.length >= 0 && result[0] <= Constantes.GPS.DISTANCIA_ALERT_PROMOCAO) {

						notificate(promocao);

						promocaoDAO.marcarEnviada(promocao, getApplicationContext());

					}

				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				}

			}

		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
			Toast.makeText(getApplicationContext(), "Zapeat foi interrompido pois o provedor de localização foi desabilitado.", Toast.LENGTH_LONG).show();

			stopSelf();
		}

		public void onProviderEnabled(String s) {

		}

	}

	private String makePromocaoMessage(Promocao promocao) {

		StringBuilder message = new StringBuilder(promocao.getLocalidade()).append(" - ").append(promocao.getDescricao());

		return message.toString();

	}

	private void notificate(Promocao promocao) {

		try {

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification notification = new Notification(R.drawable.ic_launcher, makePromocaoMessage(promocao), System.currentTimeMillis());
			notification.defaults |= Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);

			PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Zapeat", makePromocaoMessage(promocao), activity);
			notificationManager.notify(0, notification);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void initStrictMode() {

		try {

			Class<?> strictModeClass = Class.forName("android.os.StrictMode", true, Thread.currentThread().getContextClassLoader());

			Class<?> threadPolicyBuilderClass = Class.forName("android.os.StrictMode$ThreadPolicy$Builder", true, Thread.currentThread().getContextClassLoader());

			Method permitNetwork = strictModeClass.getMethod("permitNetwork");

			Method buildMethod = threadPolicyBuilderClass.getMethod("build");

			permitNetwork.invoke(null);

			buildMethod.invoke(null);

		} catch (Exception ex) {
		}
	}

}
