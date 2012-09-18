package com.zapeat.activity;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.zapeat.model.Usuario;
import com.zapeat.util.Constantes;
import com.zapeat.util.Utilitario;

public abstract class DefaultActivity extends Activity {

	protected static final int PROGRESS_DIALOG = 0;
	private ProgressThread progressThread;
	ProgressDialog progressDialog;

	protected String ultimaAtualizacao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public Usuario getUsuarioLogado() {

		SharedPreferences prefs = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0);

		Usuario usuario = new Usuario();

		String  token = prefs.getString(Constantes.Preferencias.USUARIO_LOGADO, null);

		if (token==null) {
			return null;
		}

		usuario.setToken(token);

		return usuario;

	}

	public void makeDefaultInfoMessage(Context context) {
		Toast.makeText(context, "Operação realizada com sucesso!", Toast.LENGTH_LONG).show();
	}

	public void makeInfoMessage(Context context, String message) {
		Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);

		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	protected void startMonitoring() {
		stopMonitoring();
		startService(new Intent(this, PollService.class));

	}

	protected void stopMonitoring() {
		stopService(new Intent(Constantes.Services.MONITORING));
	}

	protected void sair() {

		SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

		editor.remove(Constantes.Preferencias.USUARIO_LOGADO);

		editor.commit();

		stopMonitoring();

		Utilitario.cleanDirectory();

	}

	protected void alterarUltimaAtualizacaoPromocoes() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SharedPreferences.Editor editor = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).edit();

		editor.remove(Constantes.Preferencias.ULTIMA_ATUALIZACAO);

		editor.putString(Constantes.Preferencias.ULTIMA_ATUALIZACAO, dateFormat.format(new Date()));

		this.ultimaAtualizacao = dateFormat.format(new Date());

		editor.commit();
	}

	protected void initStrictMode() {

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
	

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Carregando...");
			return progressDialog;
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				int total = msg.arg1;
				progressDialog.setProgress(total);
				if (total >= 100) {
					try {
						dismissDialog(PROGRESS_DIALOG);
						progressThread.setState(ProgressThread.STATE_DONE);
					} catch (Exception ex) {
					}
				}
			}
		};

		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog.setProgress(0);
			progressThread = new ProgressThread(handler);
			progressThread.start();
		}
	}

	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			mState = STATE_RUNNING;
			total = 0;
			while (mState == STATE_RUNNING) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.e("ERROR", "Thread Interrupted");
				}
				Message msg = mHandler.obtainMessage();
				msg.arg1 = total;
				mHandler.sendMessage(msg);
				total++;
			}
		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}
	}
}
