package com.zapeat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SemConexaoActivity extends DefaultActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sem_conexao);

		Button botao = (Button) findViewById(R.id.btSemConexao);

		botao.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tentarNovamente();

			}
		});

	}

	private void tentarNovamente() {
		if (isOnline()) {
			this.startActivity(new Intent(this, BrowserActivity.class));
			this.finish();
		}
	}

}
