package com.zapeat.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class PromocaoListActivity extends DefaultActivity implements OnClickListener {

	private ListView listViewPromocoes;
	private ListPromocaoAdapter adapter;
	private Button btAtualizar;
	private Button btSair;
	private Button btWeb;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promocao_list);

		this.listViewPromocoes = (ListView) findViewById(R.id.list__promocoes);
		this.listViewPromocoes.setCacheColorHint(Color.TRANSPARENT);

		this.btAtualizar = (Button) findViewById(R.id.btAtualizarPromocao);
		this.btAtualizar.setOnClickListener(this);
		this.btSair = (Button) findViewById(R.id.btSairList);
		this.btWeb = (Button) findViewById(R.id.btWeb);
		this.initPromocoes();
		this.initListeners();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		this.ultimaAtualizacao = getSharedPreferences(Constantes.Preferencias.PREFERENCE_DEFAULT, 0).getString(Constantes.Preferencias.ULTIMA_ATUALIZACAO, dateFormat.format(new Date()));
		this.btAtualizar.setText("Atualizado às \n" + this.ultimaAtualizacao);
	}

	private void initPromocoes() {

		List<Promocao> promocoes = new PromocaoDAO().pesquisarTodas(getApplicationContext());

		this.adapter = new ListPromocaoAdapter(this, promocoes);

		this.listViewPromocoes.setAdapter(this.adapter);

	}

	private void initListeners() {

		OnClickListener sair = new OnClickListener() {

			@Override
			public void onClick(View v) {

				sair();

				Intent intent = new Intent(PromocaoListActivity.this, AuthActivity.class);

				startActivity(intent);

				finish();

			}
		};

		this.btSair.setOnClickListener(sair);

		OnClickListener web = new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(PromocaoListActivity.this, BrowserActivity.class);

				startActivity(intent);

				finish();

			}
		};

		this.btWeb.setOnClickListener(web);

		OnItemClickListener itemClick = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Promocao promocao = (Promocao) listViewPromocoes.getItemAtPosition(position);

				showDialogPromocao(promocao);

			}
		};

		this.listViewPromocoes.setOnItemClickListener(itemClick);

	}

	private void showDialogPromocao(Promocao promocao) {
		final Dialog dialog = new Dialog(PromocaoListActivity.this);
		dialog.setContentView(R.layout.dialog_promocao);
		dialog.setTitle("Detalhes da promoção");

		TextView text = (TextView) dialog.findViewById(R.id.textDialogPromocao);
		text.setText(promocao.getLocalidade() + "\n\n" + promocao.getDescricao()
						+ "\n de " + promocao.getPrecoOriginal() + " por " + promocao.getPrecoPromocional() 
						+ "\n\n Esta promoção encerra em " + promocao.getDataFinal() + " às " + promocao.getHoraFinal() + "\n\n");

		Button dialogButton = (Button) dialog.findViewById(R.id.btFecharDialogPromocao);
		
		ImageView image = (ImageView) dialog.findViewById(R.id.imageDialogPromocao);

		image.setImageResource(R.drawable.ic_launcher);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
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

			this.startActivity(new Intent(this, PromocaoListActivity.class));

			this.finish();
		}

	}

}
