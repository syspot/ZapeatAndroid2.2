package com.zapeat.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.entity.mime.content.ByteArrayBody;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.zapeat.http.HttpUtil;
import com.zapeat.model.FornecedorCheckInModel;
import com.zapeat.util.Constantes;

public class CheckInActivity extends Activity {

	private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private final int MEDIA_TYPE_IMAGE = 1;
	private Long id;
	private String tokenUsuario;
	private AlertDialog dialog;
	private Thread worker;
	private FornecedorCheckInModel checkInModel;
	private Bitmap bmp;
	private boolean facebook = false;
	private boolean postarFacebook = false;

	private void initDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Aguarde...");
		ProgressBar bar = new ProgressBar(this);
		builder.setView(bar);
		this.dialog = builder.create();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_in);

		((TextView) findViewById(R.checkin.titulo)).setText("Check-in " + getIntent().getStringExtra("nome"));

		id = getIntent().getLongExtra("id", 0);

		tokenUsuario = getIntent().getStringExtra("token");

		this.initDialog();

		this.initThread();

		checkInModel = new FornecedorCheckInModel();

	}

	private void initThread() {
		worker = new Thread(new Runnable() {

			@Override
			public void run() {
				HttpUtil.comentar(checkInModel);

				if (!postarFacebook) {
					hideDialog();
					Intent intent = new Intent(CheckInActivity.this, BrowserActivity.class);
					intent.putExtra("checkin", true);
					startActivity(intent);
					finish();
				} else {
					publish();
				}
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_check_in, menu);
		return true;
	}

	public void checkin(View view) {

		checkInModel.setFornecedor(id);

		checkInModel.setTokenUsuario(tokenUsuario);

		String texto = ((EditText) findViewById(R.checkin.texto)).getText().toString();

		checkInModel.setTexto(texto);

		this.showDialog();

		CheckBox chk = (CheckBox) findViewById(R.checkin.checkBox);

		this.postarFacebook = chk.isChecked();

		this.initThread();

		this.worker.start();

	}

	private void showDialog() {
		this.dialog.show();
	}

	private void hideDialog() {
		this.dialog.dismiss();
	}

	public void finalizadoCheckin() {
		this.hideDialog();
		Toast.makeText(this, "Check-in realizado com sucesso!", Toast.LENGTH_SHORT).show();
	}

	public void takePhoto(View view) {

		facebook = false;

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private File getOutputMediaFile(int type) {

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Zapeat");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		File mediaFile = null;

		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");
		}

		return mediaFile;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (facebook) {

			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

		} else {

			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

				if (resultCode == RESULT_OK) {

					InputStream imageStream = null;

					try {

						imageStream = getContentResolver().openInputStream(fileUri);

						bmp = BitmapFactory.decodeStream(imageStream);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();

						bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

						byte[] b = stream.toByteArray();

						String nomeImagem = new Date().getTime() + "checkin.png";

						ByteArrayBody bab = new ByteArrayBody(b, nomeImagem);

						checkInModel.setImage(bab);

						checkInModel.setImagem(nomeImagem);

						imageStream.close();

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}

				}
			}
		}
	}

	private void publish() {

		facebook = true;

		Session session = Session.getActiveSession();

		if (session != null) {

			Bundle postParams = new Bundle();

			postParams.putString("name", "Zapeat");

			TextView txt = (TextView) findViewById(R.checkin.titulo);

			postParams.putString("caption", txt.getText().toString());

			if (!TextUtils.isEmpty(this.checkInModel.getTexto())) {

				postParams.putString("description", this.checkInModel.getTexto());

			}

			if (!TextUtils.isEmpty(checkInModel.getImagem())) {

				postParams.putString("picture", Constantes.Http.URL_ZAPEAT_IMAGEM + checkInModel.getImagem());

			} else {

				postParams.putString("picture", "http://www.zapeat.com/site/img/marca.png");

			}

			postParams.putString("link", "http://www.zapeat.com/site/");

			Request.Callback reqcallback = new Request.Callback() {

				public void onCompleted(Response response) {

					FacebookRequestError error = response.getError();

					if (error != null) {

						Toast.makeText(getApplicationContext(), "Não foi possível publicar no facebook no momento, tente mais tarde", Toast.LENGTH_SHORT).show();

					}

					hideDialog();

					Intent intent = new Intent(CheckInActivity.this, BrowserActivity.class);

					intent.putExtra("checkin", true);

					startActivity(intent);

					finish();
				}

			};

			try {

				Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, reqcallback);

				RequestAsyncTask task = new RequestAsyncTask(request);

				task.execute();

			} catch (Exception ex) {
				Toast.makeText(getApplicationContext(), "Não foi possível publicar no facebook no momento, tente mais tarde", Toast.LENGTH_SHORT).show();
			}

		} else {

			Session.openActiveSession(this, true, callback);

		}

	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (session.isOpened()) {

				// make request to the /me API
				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

					// callback after Graph API response with user object
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							publish();
						}
					}
				});
			}
		}
	};

}
