package com.zapeat.util;

import java.io.File;

import android.os.Environment;

public class Constantes {

	private Constantes() {

	}
	
	public static final String ASK_DISTANCIA_MAXIMA = "Qual distância máxima?";
	public static final String SEM_LOCALIZACAO = "Não foi possível obter localização atual, verifique as configurações de localização";
	public static final String NENHUMA_PROMOÇÃO_ENCONTRADA = "Nenhuma promoção ENCONTRADA!";

	public interface Preferencias {

		String PREFERENCE_DEFAULT = "preferenceDefault";
		String USUARIO_LOGADO = "usuarioLogado";
		String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";

	}

	public interface GPS {
		int DISTANCIA = 1;// ;000; // metros
		int FREQUENCIA_TEMPO = 500;// 1 minuto
		float DISTANCIA_ALERT_PROMOCAO = 2000;
	}

	public interface Services {
		String MONITORING = "monitoring";
		int PERIODICIDADE = 1000 * 60; // * 60 * 3;
	}

	public interface JsonProperties {

		String ID = "id";
		String DESCRICAO = "descricao";
		String LATITUDE = "latitude";
		String LONGITUDE = "longitude";
		String LOCALIDADE = "localidade";
		String HORA_FINAL = "horaFinal";
		String DATA_FINAL = "dataFinal";
		String PRECO_ORIGINAL = "precoOriginal";
		String PRECO_PROMOCIONAL = "precoPromocional";
		String PROMOCOES = "promocoes";
		String LOGADO = "logged";
		String USUARIO_ID = "usuarioId";
		String ID_FORNECEDOR = "idFornecedor";

	}

	public interface Http {

		public interface Mensagens {
			String VALIDACAO_USUARIO = "Usuário não pode estar nulo!";
			String FALHA_AUTENTICACAO = "A autenticação falhou por motivos técnicos. Tente novamente mais tarde";
		}

		String URL_AUTH = "http://192.168.0.17:8080/TestAndroid/autenticar";
		String URL_PROMOCOES = "http://192.168.0.17:8080/TestAndroid/promocoes";
		String URL_ZAPEAT = "http://www.google.com.br";
		String PARAMETRO_RETORNO = "PARAM_RETORNO";
		String PARAMETRO_LOGIN = "login";
		String PARAMETRO_SENHA = "senha";
		String URL_DOWNLOAD_IMAGE_FORNECEDOR = "http://www.iconlet.com/icons/nuvola/16x16/camera_test.png";
		// String URL_DOWNLOAD_IMAGE_FORNECEDOR =
		// "http://192.168.0.17:8080/TestAndroid/imagemFornecedor";
	}

	public interface Storage {
		String EXTENSAO = ".png";
		String DIRETORIO_ZAPEAT = Environment.getExternalStorageDirectory().toString() + File.separator + "zapeat";
	}
}
