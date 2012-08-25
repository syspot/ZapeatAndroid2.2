package com.zapeat.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zapeat.adapter.ListPromocaoAdapter;
import com.zapeat.dao.PromocaoDAO;
import com.zapeat.http.HttpUtil;
import com.zapeat.model.Promocao;
import com.zapeat.util.Constantes;
import com.zapeat.util.Utilitario;

public class PromocaoListActivity extends DefaultActivity implements OnClickListener {

	private ListView listViewPromocoes;
	private ListPromocaoAdapter adapter;
	private Button btAtualizar;
	private AlertDialog dialog;
	private String distanciaFiltro;
	private final String KM_1 = "1 Km";
	private final String KM_3 = "3 Km";
	private final String KM_5 = "5 Km";
	private final String SEM_DISTANCIA = "Ilimitada";
	private Promocao promocaoSelecionada;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promocao_list);

		this.listViewPromocoes = (ListView) findViewById(R.id.list__promocoes);
		this.listViewPromocoes.setCacheColorHint(Color.TRANSPARENT);

		this.btAtualizar = (Button) findViewById(R.id.btAtualizarPromocao);
		this.btAtualizar.setOnClickListener(this);
		this.initPromocoes();
		this.initListeners();
		this.initDialog();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		this.ultimaAtualizacao = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).getString(Constantes.Preferencias.ULTIMA_ATUALIZACAO, dateFormat.format(new Date()));
		this.btAtualizar.setText("Atualizado às \n" + this.ultimaAtualizacao);
	}

	private void initDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(Constantes.ASK_DISTANCIA_MAXIMA);

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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_promocao, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.filtrar:

			this.dialog.show();

			return true;

		case R.id.mapa:

			Intent intentMapa = new Intent(PromocaoListActivity.this, MapViewActivity.class);

			startActivity(intentMapa);

			finish();

			return true;

		case R.id.web:

			Intent intent = new Intent(PromocaoListActivity.this, BrowserActivity.class);

			startActivity(intent);

			finish();

			return true;

		case R.id.sair:

			super.sair();

			Intent intentSair = new Intent(PromocaoListActivity.this, AuthActivity.class);

			startActivity(intentSair);

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initPromocoes() {

		List<Promocao> promocoes = new PromocaoDAO().pesquisarTodas(getApplicationContext());

		this.adapter = new ListPromocaoAdapter(this, promocoes);

		this.listViewPromocoes.setAdapter(this.adapter);

	}

	private void initListeners() {
		OnItemClickListener itemClick = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				promocaoSelecionada = (Promocao) listViewPromocoes.getItemAtPosition(position);

				showDialogPromocao();

			}
		};

		this.listViewPromocoes.setOnItemClickListener(itemClick);

	}

	private void showDialogPromocao() {
		final Dialog dialog = new Dialog(PromocaoListActivity.this);
		dialog.setContentView(R.layout.dialog_promocao);
		dialog.setTitle("Detalhes da promoção");

		TextView text = (TextView) dialog.findViewById(R.id.textDialogPromocao);
		text.setText(promocaoSelecionada.getLocalidade() + "\n\n" + promocaoSelecionada.getDescricao() + "\n de " + promocaoSelecionada.getPrecoOriginal() + " por " + promocaoSelecionada.getPrecoPromocional() + "\n\n Esta promoção encerra em " + promocaoSelecionada.getDataFinal() + " às " + promocaoSelecionada.getHoraFinal() + "\n\n");

		Button dialogButton = (Button) dialog.findViewById(R.id.btFecharDialogPromocao);

		Button btDialogVerMapa = (Button) dialog.findViewById(R.id.btVerNoMapas);

		ImageView image = (ImageView) dialog.findViewById(R.id.imageDialogPromocao);

		Bitmap bmp = Utilitario.getImage(promocaoSelecionada.getId());

		if (bmp == null) {

			image.setImageResource(R.drawable.ic_launcher);

		} else {
			image.setImageBitmap(bmp);
			image.setMaxHeight(130);
			image.setMinimumHeight(130);
			image.setMinimumWidth(130);
			image.setMaxWidth(130);
		}

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btDialogVerMapa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(PromocaoListActivity.this, MapViewActivity.class);

				intent.putExtra("lat", promocaoSelecionada.getLatitude());
				intent.putExtra("lon", promocaoSelecionada.getLongitude());

				startActivity(intent);

			}
		});

		dialog.show();
	}

	@Override
	public void onClick(View v) {

		super.initStrictMode();
		List<Promocao> promocoesNovas = null;
		List<Promocao> promocoesAtuais = null;
		Promocao nova = null;
		PromocaoDAO dao = new PromocaoDAO();

		if (getUsuarioLogado() != null) {

			try {

				promocoesNovas = HttpUtil.pesquisarPromocoes(getUsuarioLogado());

				promocoesAtuais = dao.pesquisarTodas(getApplicationContext());

				Iterator<Promocao> iterador = promocoesNovas.iterator();

				while (iterador.hasNext()) {

					nova = iterador.next();

					if (promocoesAtuais.contains(nova)) {
						iterador.remove();
					}
				}

				dao.inserir(promocoesNovas, getApplicationContext());

				this.initPromocoes();

				super.alterarUltimaAtualizacaoPromocoes();

				this.btAtualizar.setText("Atualizado às \n" + this.ultimaAtualizacao);

			} catch (Exception ex) {
				super.makeInfoMessage(this, "Ocorreu um erro ao atualizar as promoções, tente novamente mais tarde");
			}

		}

		this.startActivity(new Intent(this, PromocaoListActivity.class));

		this.finish();

	}

	private void filtrar() {

		if (this.distanciaFiltro != null && !"".equals(distanciaFiltro)) {

			List<Promocao> promocoes = new PromocaoDAO().pesquisarTodas(getApplicationContext());

			Iterator<Promocao> iterador = promocoes.iterator();

			Promocao promo = null;

			float distancia = this.getDistanciaFiltro();

			float[] distanciaCalculada = new float[4];

			Location location = PollService.getLastLocation();

			if (location != null) {

				while (iterador.hasNext()) {

					promo = iterador.next();

					Location.distanceBetween(promo.getLatitude(), promo.getLongitude(), location.getLatitude(), location.getLongitude(), distanciaCalculada);

					if (distanciaCalculada != null && distanciaCalculada.length >= 0 && distanciaCalculada[0] > distancia) {
						iterador.remove();
					}

				}

				this.adapter = new ListPromocaoAdapter(this, promocoes);

				this.listViewPromocoes.setAdapter(this.adapter);

				if (promocoes.isEmpty()) {
					super.makeInfoMessage(this, Constantes.NENHUMA_PROMOÇÃO_ENCONTRADA);
				}

			} else {

				super.makeInfoMessage(this, Constantes.SEM_LOCALIZACAO);

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
}
