package com.zapeat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zapeat.dao.ConfiguracaoDAO;
import com.zapeat.model.Configuracao;

public class ConfiguracaoActivity extends DefaultActivity {

	private Button btSalvar;
	private EditText distancia;
	private EditText periodo;
	private Button btSair;
	private Button btBrowser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuracao);
		this.initComponents();
		this.initListeners();
		this.verificarExistenciaConfiguracao();
	}

	private void initComponents() {
		this.btSalvar = (Button) findViewById(R.id.btSalvar);
		this.distancia = (EditText) findViewById(R.id.distanciaText);
		this.periodo = (EditText) findViewById(R.id.periodoText);
		this.btSair = (Button) findViewById(R.id.btSair);
		this.btBrowser = (Button) findViewById(R.id.btBrowser);
	}

	private void initListeners() {
		OnClickListener onclick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validateFields()) {
					save();
					makeDefaultInfoMessage(ConfiguracaoActivity.this);
				}

			}
		};

		this.btSalvar.setOnClickListener(onclick);

		OnClickListener onClickSair = new OnClickListener() {
			@Override
			public void onClick(View v) {

				sair();

				Intent intent = new Intent(ConfiguracaoActivity.this, ZapeatAuthActivity.class);

				startActivity(intent);

				finish();
			}
		};

		OnClickListener onClickBrowser = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ConfiguracaoActivity.this, BrowserActivity.class);

				startActivity(intent);

				finish();

			}
		};

		this.btSair.setOnClickListener(onClickSair);

		this.btBrowser.setOnClickListener(onClickBrowser);
	}

	private void verificarExistenciaConfiguracao() {

		Configuracao configuracao = new Configuracao();

		configuracao.setUsuario(super.getUsuarioLogado());

		ConfiguracaoDAO configuracaoDAO = new ConfiguracaoDAO();

		configuracao = configuracaoDAO.obter(super.getUsuarioLogado(), this);

		if (configuracao != null) {

			this.distancia.setText(configuracao.getDistancia().toString());

			this.periodo.setText(configuracao.getPeriodo().toString());

		} else {

			makeInfoMessage(this, "Configure seu aplicativo para começar a usá-lo!");

		}

	}

	private void save() {

		Configuracao configuracao = new Configuracao();

		configuracao.setUsuario(super.getUsuarioLogado());

		configuracao.setPeriodo(Integer.valueOf(this.periodo.getText().toString()));

		configuracao.setDistancia(Integer.valueOf(this.distancia.getText().toString()));

		ConfiguracaoDAO configuracaoDAO = new ConfiguracaoDAO();

		if (configuracaoDAO.obter(configuracao.getUsuario(), this) == null) {
			configuracaoDAO.inserir(configuracao, this);
		} else {
			configuracaoDAO.alterar(configuracao, this);
		}

		startMonitoring();

	}

	private boolean validateFields() {

		if (this.distancia.getText() == null || this.distancia.getText().length() == 0) {
			Toast.makeText(this, "É obrigatório o preenchimento da distância !", Toast.LENGTH_LONG).show();
			return false;
		}

		if (this.periodo.getText() == null || this.periodo.getText().length() == 0) {
			Toast.makeText(this, "É obrigatório o preenchimento do período !", Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

}
