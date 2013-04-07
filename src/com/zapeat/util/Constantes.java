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
		String INTERNET = "internet";
		String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";
		String PROMOCAO_NOTIFICADA = "promocaoNotificada";

	}

	public interface GPS {
		int DISTANCIA = 100; // 100m
		int FREQUENCIA_TEMPO = 3 * 60 * 1000;// 3 min
		float DISTANCIA_ALERT_PROMOCAO = 100;
	}

	public interface Services {
		String MONITORING = "monitoring";
		int PERIODICIDADE = 1000 * 60 * 60 * 3;
	}

	public interface JsonProperties {

		String ID = "id";
		String DESCRICAO = "descricao";
		String LATITUDE = "latitude";
		String LONGITUDE = "longitude";
		String LOCALIDADE = "localidade";
		String DATA_INICIAL = "dataInicial";
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

		String URL_CHECKIN = "http://www.zapeat.com/ZapeatMobileDev/checkin";
		String URL_AUTH = "http://www.zapeat.com/ZapeatMobileDev/autenticar";
		String URL_PROMOCOES = "http://www.zapeat.com/ZapeatMobileDev/promocoes";
		String URL_CATEGORIAS = "http://www.zapeat.com/ZapeatMobileDev/categorias";
		String URL_ZAPEAT = "http://www.zapeat.com/ZapeatMobileDev/menu.xhtml";
		String URL_ZAPEAT_IMAGEM = "http://www.zapeat.com/img_zapeat/";
		String URL_LISTAGEM = "http://www.zapeat.com/ZapeatMobileDev/listagem.xhtml";
		String URL_ZAPEAT_PROMOCAO = "http://www.zapeat.com/ZapeatMobileDev/detalhamento.xhtml";
		String PARAMETRO_RETORNO = "PARAM_RETORNO";
		String PARAMETRO_EMAIL = "email";
		String PARAMETRO_SENHA = "senha";
		String URL_DOWNLOAD_IMAGE_FORNECEDOR = "http://www.iconlet.com/icons/nuvola/16x16/camera_test.png";
		// String URL_DOWNLOAD_IMAGE_FORNECEDOR =
		// "http://www.zapeat.com/ZapeatMobileDev/TestAndroid/imagemFornecedor";
	}

	public interface Storage {
		String EXTENSAO = ".png";
		String DIRETORIO_ZAPEAT = Environment.getExternalStorageDirectory().toString() + File.separator + "zapeat";
	}
}
