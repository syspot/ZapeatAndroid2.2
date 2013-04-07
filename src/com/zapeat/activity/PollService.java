package com.zapeat.activity;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import android.text.TextUtils;

import com.zapeat.dao.PromocaoDAO;
import com.zapeat.exception.ApplicationException;
import com.zapeat.http.HttpUtil;
import com.zapeat.model.Promocao;
import com.zapeat.util.Constantes;

public class PollService extends Service {

	private static LocationManager locationManager;
	private PromocaoDAO promocaoDAO = new PromocaoDAO();
	private static Location lastLocation;

	@Override
	public void onStart(Intent intent, int startId) {

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new ZapeatLocationListener());

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class BuscarPromocoes extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			List<Promocao> promocoesNovas = null;

			List<Promocao> promocoesAtuais = null;

			Promocao nova = null;

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

			try {

				promocoesNovas = HttpUtil.pesquisarPromocoes();

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

			} catch (ApplicationException ex) {
				ex.printStackTrace();
			}

			return null;
		}

	}

	private class ZapeatLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			lastLocation = location;

			float[] result = new float[4];

			List<Promocao> notificaveis = new ArrayList<Promocao>();

			for (Promocao promocao : promocaoDAO.pesquisarNaoNotificadas(getApplicationContext())) {

				try {

					Location.distanceBetween(promocao.getLatitude(), promocao.getLongitude(), location.getLatitude(), location.getLongitude(), result);

					if (result.length >= 0 && result[0] <= Constantes.GPS.DISTANCIA_ALERT_PROMOCAO) {

						promocaoDAO.marcarEnviada(promocao, getApplicationContext());

						notificaveis.add(promocao);

					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

			if (!notificaveis.isEmpty()) {

				if (notificaveis.size() <= 3) {

					notificate(notificaveis.get(0));

					notificate(notificaveis.get(1));

					notificate(notificaveis.get(2));

				} else {

					notificate(notificaveis);

				}

			}

			SharedPreferences preferences = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0);

			String ultima = preferences.getString(Constantes.Preferencias.ULTIMA_ATUALIZACAO, "");

			if (TextUtils.isEmpty(ultima)) {

				new BuscarPromocoes().execute();

			} else {

				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

				try {

					Date data = dateFormat.parse(ultima);

					long milis = new Date().getTime() - data.getTime();

					milis = milis / 1000 / 60 / 60;

					if (milis >= 3) {

						new BuscarPromocoes().execute();

					}

				} catch (ParseException e) {

					e.printStackTrace();
				}

			}

		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}

	}

	private String makePromocaoMessage(Promocao promocao) {

		StringBuilder message = new StringBuilder(promocao.getLocalidade()).append(" - ").append(promocao.getDescricao());
		if (promocao.getPrecoOriginal() != null && ! "".equals(promocao.getPrecoOriginal().trim())) {
			message.append(" De ").append(promocao.getPrecoOriginal()).append(" por ").append(promocao.getPrecoPromocional());
		}

		return message.toString();

	}

	private String makePromocaoMessage(List<Promocao> promocoes) {

		StringBuilder message = new StringBuilder("Existem ").append(promocoes.size()).append(" zapeats para você!");

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
			notificationManager.notify(promocao.getId().intValue(), notification);

			SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

			editor.remove(Constantes.Preferencias.PROMOCAO_NOTIFICADA);

			editor.putLong(Constantes.Preferencias.PROMOCAO_NOTIFICADA, promocao.getId());

			editor.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void notificate(List<Promocao> promocoes) {

		try {

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification notification = new Notification(R.drawable.ic_launcher, makePromocaoMessage(promocoes), System.currentTimeMillis());
			notification.defaults |= Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);

			PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Zapeat", makePromocaoMessage(promocoes), activity);
			notificationManager.notify(promocoes.get(0).getId().intValue(), notification);

			SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

			editor.remove(Constantes.Preferencias.PROMOCAO_NOTIFICADA);

			editor.putLong(Constantes.Preferencias.PROMOCAO_NOTIFICADA, promocoes.get(0).getId());

			editor.commit();

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
			ex.printStackTrace();
		}
	}

	public static Location getLastLocation() {
		return lastLocation;
	}
}
