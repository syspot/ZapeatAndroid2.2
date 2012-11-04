package com.zapeat.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zapeat.exception.ApplicationException;
import com.zapeat.model.Promocao;
import com.zapeat.model.Usuario;
import com.zapeat.util.Constantes;

public class HttpUtil {

	private HttpUtil() {

	}

	public static Usuario autenticar(Usuario usuario) throws ApplicationException {

		Usuario user = null;

		try {

			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 2000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair(Constantes.Http.PARAMETRO_EMAIL, usuario.getLogin()));
			nameValuePairs.add(new BasicNameValuePair(Constantes.Http.PARAMETRO_SENHA, usuario.getSenha()));

			HttpPost httppost = new HttpPost(Constantes.Http.URL_AUTH);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() != 200) {
				usuario = null;
			} else {
				user = readJson(response);
			}

		} catch (Exception ex) {
			throw new ApplicationException(ex);
		}

		return user;

	}

	public static List<Promocao> pesquisarPromocoes() throws ApplicationException {

		try {

			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 2000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

			HttpPost httppost = new HttpPost(Constantes.Http.URL_PROMOCOES);

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			return readPromocoes(response);

		} catch (Exception ex) {
			throw new ApplicationException(ex);
		}

	}

	public static Bitmap downloadBitmap(Long idFornecedor) {

		HttpParams httpParameters = new BasicHttpParams();

		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		HttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpGet getRequest = new HttpGet(Constantes.Http.URL_DOWNLOAD_IMAGE_FORNECEDOR + "?id=" + idFornecedor);

		try {
			HttpResponse response = httpclient.execute(getRequest);

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {

			getRequest.abort();

		}

		return null;
	}

	private static Usuario readJson(HttpResponse response) throws ApplicationException {

		Usuario usuario = null;

		try {

			StringBuilder builder = new StringBuilder();
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			String line;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			String json = builder.toString();

			if ("".equals(json)) {
				return null;
			}

			JSONObject jsonObject = new JSONObject(builder.toString());

			if (jsonObject.getBoolean(Constantes.JsonProperties.LOGADO)) {

				usuario = new Usuario();

				usuario.setId(jsonObject.getInt("id"));

				usuario.setNome(jsonObject.getString("nome"));

				usuario.setToken(jsonObject.getString("token"));

				usuario.setPromocoes(readJsonPromocao(jsonObject));

			}

		} catch (Exception ex) {
			throw new ApplicationException(ex);
		}
		return usuario;
	}

	private static List<Promocao> readPromocoes(HttpResponse response) throws JSONException, IOException {

		StringBuilder builder = new StringBuilder();
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		String line;

		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONObject jsonObject = new JSONObject(builder.toString());

		return readJsonPromocao(jsonObject);

	}

	private static List<Promocao> readJsonPromocao(JSONObject object) throws JSONException {

		List<Promocao> promocoes = new ArrayList<Promocao>();

		JSONArray array = object.getJSONArray("promocoes");

		JSONObject jsonPromo = null;

		Promocao promocao = null;

		for (int i = 0; i < array.length(); i++) {

			jsonPromo = array.getJSONObject(i);

			promocao = new Promocao();

			promocao.setId(jsonPromo.getLong(Constantes.JsonProperties.ID));

			promocao.setLocalidade(jsonPromo.getString(Constantes.JsonProperties.LOCALIDADE));

			promocao.setLatitude(jsonPromo.getDouble(Constantes.JsonProperties.LATITUDE));

			promocao.setLongitude(jsonPromo.getDouble(Constantes.JsonProperties.LONGITUDE));

			promocao.setDescricao(jsonPromo.getString(Constantes.JsonProperties.DESCRICAO));

			promocao.setDataInicial(jsonPromo.getString(Constantes.JsonProperties.DATA_INICIAL));

			promocao.setDataFinal(jsonPromo.getString(Constantes.JsonProperties.DATA_FINAL));

			promocao.setPrecoOriginal(jsonPromo.getString(Constantes.JsonProperties.PRECO_ORIGINAL));

			promocao.setPrecoPromocional(jsonPromo.getString(Constantes.JsonProperties.PRECO_PROMOCIONAL));

			promocoes.add(promocao);

		}

		return promocoes;

	}

}
