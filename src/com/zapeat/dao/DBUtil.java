package com.zapeat.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUtil extends SQLiteOpenHelper {

	private final String CREATE_TABLE_PROMOCAO = "CREATE TABLE PROMOCOES (ID INT,DESCRICAO TEXT,LATITUDE TEXT, LONGITUDE TEXT,LOCALIDADE TEXT,DATA_ANUNCIO DATE,DATA_INICIAL TEXT, DATA_FINAL TEXT,PRECO_ORIGINAL TEXT,PRECO_PROMOCIONAL TEXT);";
	private final String CREATE_TABLE_CONFIGURACAO = "CREATE TABLE CONFIGURACOES (USUARIO_ID INT,DISTANCIA INT,PERIODO INT);";
	
	public interface Tabelas {
		String PROMOCOES = "PROMOCOES";
		String CONFIGURACOES = "CONFIGURACOES";
	}

	private static DBUtil dbUtil;

	public static DBUtil getInstance(Context context) {
		if (dbUtil == null) {
			dbUtil = new DBUtil(context);
		}
		return dbUtil;
	}

	private DBUtil(Context context) {
		super(context, "zapeat", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CREATE_TABLE_PROMOCAO);
		db.execSQL(CREATE_TABLE_CONFIGURACAO);
		
	}

	public static void close(SQLiteOpenHelper db,Cursor cursor) {
		if(cursor!=null && !cursor.isClosed()) {
			cursor.close();
		}
		if(db!=null) {
			db.close();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
