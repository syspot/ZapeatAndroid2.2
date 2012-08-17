package com.zapeat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilitario {

	private Utilitario() {

	}

	public static String format(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	public static String format(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	public static Date parse(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(date);
	}

	public static boolean isAfterToday(String date) {
		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date data = format.parse(date);
			Date hoje = new Date();
			String strHoje = format.format(hoje);
			return format.parse(strHoje).before(data);

		} catch (Exception ex) {
			return false;
		}

	}

	public static String formatBrasil(String date) throws ParseException {
		SimpleDateFormat formatBrasil = new SimpleDateFormat("dd/MM/yyyy");
		return formatBrasil.format(Utilitario.parse(date));
	}

}
