package com.zapeat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.zapeat.adapter.ListPromocaoAdapter;
import com.zapeat.model.Categoria;
import com.zapeat.util.CategoriaDownloader;

public class CategoriaActivity extends Activity {

	private AlertDialog dialog;
	private ListView categorias;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categoria);

		this.initDialog();

		ZapeatApp app = (ZapeatApp) getApplication();

		new CategoriaDownloader(this).execute(app.getLatitudeAtual(), app.getLongitudeAtual());

		categorias = (ListView) findViewById(R.categorias.listView);

		categorias.setAdapter(new ListPromocaoAdapter(this, new ArrayList<Categoria>()));

		dialog.show();

		this.initListeners();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_categoria, menu);
		return true;
	}

	private void initDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Aguarde...");
		ProgressBar bar = new ProgressBar(this);
		builder.setView(bar);
		this.dialog = builder.create();
	}

	public void init(List<Categoria> categorias) {

		dialog.dismiss();

		if (categorias != null) {

			((ListPromocaoAdapter) this.categorias.getAdapter()).setCategorias(categorias);

			((ListPromocaoAdapter) this.categorias.getAdapter()).notifyDataSetChanged();
			
			findViewById(R.categorias.btCategorias).setVisibility(Button.VISIBLE);

		}

	}

	private void initListeners() {
		OnItemClickListener itemClick = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Categoria categoria = (Categoria) categorias.getItemAtPosition(position);

				ZapeatApp app = (ZapeatApp) getApplication();

				Intent intent = new Intent(CategoriaActivity.this, BrowserActivity.class);

				intent.putExtra("id", categoria.getId());

				intent.putExtra("latitude", app.getLatitudeAtual());

				intent.putExtra("longitude", app.getLongitudeAtual());

				startActivity(intent);

				finish();

			}
		};

		this.categorias.setOnItemClickListener(itemClick);

	}

}
