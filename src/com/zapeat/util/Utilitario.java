package com.zapeat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zapeat.exception.ApplicationException;

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

	public static boolean isBeforeToday(String date) {
		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date data = format.parse(date);
			Date hoje = new Date();
			String strHoje = format.format(hoje);
			return format.parse(strHoje).after(data);

		} catch (Exception ex) {
			return false;
		}

	}

	public static String formatBrasil(String date) throws ParseException {
		SimpleDateFormat formatBrasil = new SimpleDateFormat("dd/MM/yyyy");
		return formatBrasil.format(Utilitario.parse(date));
	}

	public static void storeImage(Bitmap image, Long idPromocao) {

		if (!new File(Constantes.Storage.DIRETORIO_ZAPEAT).isDirectory()) {
			new File(Constantes.Storage.DIRETORIO_ZAPEAT).mkdir();
		}

		String fileName = idPromocao.toString() + Constantes.Storage.EXTENSAO;
		File file = new File(Constantes.Storage.DIRETORIO_ZAPEAT, fileName);

		if (file.exists()) {
			file.delete();
		}

		try {

			if (file.createNewFile()) {
				FileOutputStream out = new FileOutputStream(file);
				image.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public static void cleanDirectory() {

		File diretorio = new File(Constantes.Storage.DIRETORIO_ZAPEAT);

		if (diretorio.isDirectory()) {
			File[] arquivos = diretorio.listFiles();
			if (arquivos != null) {

				for (File arquivo : arquivos) {
					if (arquivo.isFile()) {
						arquivo.delete();
					}
				}

			}

			diretorio.delete();
		}

	}

	public static boolean existsImage(Long idPromocao) {
		String fileName = idPromocao.toString() + Constantes.Storage.EXTENSAO;
		return new File(Constantes.Storage.DIRETORIO_ZAPEAT, fileName).exists();
	}

	public static Bitmap getImage(Long idPromocao) {

		if (idPromocao == null) {
			return null;
		}

		File imgFile = new File(Constantes.Storage.DIRETORIO_ZAPEAT + File.separator + idPromocao.toString() + Constantes.Storage.EXTENSAO);

		if (imgFile.exists()) {

			Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

			return bmp;

		}

		return null;

	}

	public static String gerarHash(String plainText) throws ApplicationException {

		MessageDigest mdAlgorithm;

		StringBuffer hexString = new StringBuffer();

		try {

			mdAlgorithm = MessageDigest.getInstance("md5");

			mdAlgorithm.update(plainText.getBytes());

			byte[] digest = mdAlgorithm.digest();

			for (int i = 0; i < digest.length; i++) {

				plainText = Integer.toHexString(0xFF & digest[i]);

				if (plainText.length() < 2) {

					plainText = "0" + plainText;
				}

				hexString.append(plainText);
			}

		} catch (Exception e) {

			throw new ApplicationException(e.getMessage());
		}

		return hexString.toString();
	}
}
