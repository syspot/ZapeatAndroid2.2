package com.zapeat.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.zapeat.model.Promocao;

public class PromocaoDAO {

	public List<Promocao> pesquisar(Context context) {

		List<Promocao> promocoes = new ArrayList<Promocao>();

		DBUtil conexao = DBUtil.getInstance(context);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(DBUtil.Tabelas.PROMOCOES);
		qb.appendWhere("DATA_ANUNCIO < DATE('now') OR DATA_ANUNCIO IS NULL");

		String[] colunas = new String[] { "id", "descricao", "latitude", "longitude", "localidade" };
		Cursor cursor = qb.query(conexao.getReadableDatabase(), colunas, null, null, null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			promocoes.add(this.createPromocao(cursor));
			cursor.moveToNext();
		}

		DBUtil.close(conexao, cursor);

		return promocoes;

	}
	
	public List<Promocao> pesquisarTodas(Context context) {

		List<Promocao> promocoes = new ArrayList<Promocao>();

		DBUtil conexao = DBUtil.getInstance(context);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(DBUtil.Tabelas.PROMOCOES);
		
		String[] colunas = new String[] { "id", "descricao", "latitude", "longitude", "localidade" };
		Cursor cursor = qb.query(conexao.getReadableDatabase(), colunas, null, null, null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			promocoes.add(this.createPromocao(cursor));
			cursor.moveToNext();
		}

		DBUtil.close(conexao, cursor);

		return promocoes;

	}

	public void marcarEnviada(Promocao promocao, Context context) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		DBUtil conexao = DBUtil.getInstance(context);
		ContentValues values = new ContentValues();
		values.put("DATA_ANUNCIO", dateFormat.format(new Date()));

		SQLiteDatabase db = conexao.getWritableDatabase();

		db.update(DBUtil.Tabelas.PROMOCOES, values, "ID = " + promocao.getId(), null);

		DBUtil.close(conexao, null);
	}

	private Promocao createPromocao(Cursor cursor) {

		Promocao promocao = new Promocao();
		promocao.setId(cursor.getLong(0));
		promocao.setDescricao(cursor.getString(1));
		promocao.setLatitude(cursor.getDouble(2));
		promocao.setLongitude(cursor.getDouble(3));
		promocao.setLocalidade(cursor.getString(4));
		return promocao;

	}

	public void inserir(List<Promocao> promocoes, Context context) {

		DBUtil conexao = DBUtil.getInstance(context);
		ContentValues initialValues = null;
		SQLiteDatabase db = conexao.getWritableDatabase();

		for (Promocao promocao : promocoes) {

			initialValues = new ContentValues();
			initialValues.put("ID", promocao.getId());
			initialValues.put("LOCALIDADE", promocao.getLocalidade());
			initialValues.put("DESCRICAO", promocao.getDescricao());
			initialValues.put("LATITUDE", promocao.getLatitude());
			initialValues.put("LONGITUDE", promocao.getLongitude());

			db.insert(DBUtil.Tabelas.PROMOCOES, null, initialValues);

		}

		DBUtil.close(conexao, null);

	}

	public void clean(Context context) {
		DBUtil conexao = DBUtil.getInstance(context);
		SQLiteDatabase db = conexao.getWritableDatabase();
		db.delete(DBUtil.Tabelas.PROMOCOES, null, null);
		DBUtil.close(conexao, null);
	}

}
