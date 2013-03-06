package com.zapeat.util;

import java.util.List;

import android.os.AsyncTask;

import com.zapeat.activity.CategoriaActivity;
import com.zapeat.exception.ApplicationException;
import com.zapeat.http.HttpUtil;
import com.zapeat.model.Categoria;

public class CategoriaDownloader extends AsyncTask<Double, Void, List<Categoria>> {

	private CategoriaActivity categoriaActivity;

	public CategoriaDownloader(CategoriaActivity categoriaActivity) {
		this.categoriaActivity = categoriaActivity;
	}

	@Override
	protected List<Categoria> doInBackground(Double... params) {
		Double latitude = null;
		Double longitude = null;

		if (params!=null & params.length > 0) {
			latitude = params[0];
			longitude = params[1];
		}
		try {
			return HttpUtil.pesquisarCategorias(latitude, longitude);
		} catch (ApplicationException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<Categoria> result) {
		categoriaActivity.init(result);
	}

}
