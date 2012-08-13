package com.zapeat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.zapeat.model.Configuracao;
import com.zapeat.model.Usuario;

public class ConfiguracaoDAO {

	public Configuracao obter(Usuario usuario, Context context) {

		Configuracao configuracao = null;

		DBUtil conexao = DBUtil.getInstance(context);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DBUtil.Tabelas.CONFIGURACOES);

		qb.appendWhere("USUARIO_ID = ?");

		String[] colunas = new String[] { "usuario_id", "distancia", "periodo" };
		Cursor cursor = qb.query(conexao.getReadableDatabase(), colunas, null, new String[] { usuario.getId().toString() }, null, null, null);

		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			configuracao = this.createConfiguracao(cursor);
		}

		DBUtil.close(conexao, cursor);

		return configuracao;

	}

	public void inserir(Configuracao configuracao, Context context) {

		DBUtil conexao = DBUtil.getInstance(context);
		ContentValues initialValues = new ContentValues();
		initialValues.put("USUARIO_ID", configuracao.getUsuario().getId());
		initialValues.put("DISTANCIA", configuracao.getDistancia());
		initialValues.put("PERIODO", configuracao.getPeriodo());

		SQLiteDatabase db = conexao.getWritableDatabase();
		db.insert(DBUtil.Tabelas.CONFIGURACOES, null, initialValues);

		DBUtil.close(conexao, null);

	}

	public void alterar(Configuracao configuracao, Context context) {

		DBUtil conexao = DBUtil.getInstance(context);
		ContentValues values = new ContentValues();
		values.put("DISTANCIA", configuracao.getDistancia());
		values.put("PERIODO", configuracao.getPeriodo());

		SQLiteDatabase db = conexao.getWritableDatabase();

		db.update(DBUtil.Tabelas.CONFIGURACOES, values, "USUARIO_ID = " + configuracao.getUsuario().getId(), null);

		DBUtil.close(conexao, null);

	}

	private Configuracao createConfiguracao(Cursor cursor) {

		Configuracao configuracao = new Configuracao();
		configuracao.setUsuario(new Usuario());
		configuracao.getUsuario().setId(cursor.getInt(0));
		configuracao.setDistancia(cursor.getInt(1));
		configuracao.setPeriodo(cursor.getInt(2));
		return configuracao;

	}

}
