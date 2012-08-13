package com.zapeat.util;

public class Constantes {

	private Constantes() {

	}

	public interface Preferencias {

		String PREFERENCE_DEFAULT = "preferenceDefault";
		String USUARIO_LOGADO = "usuarioLogado";
		String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";

	}

	public interface GPS {
		int DISTANCIA = 1000; // metros
		float DISTANCIA_ALERT_PROMOCAO = 1000;
	}

	public interface Services {
		String MONITORING = "monitoring";
		int TRES_HORAS = 1000 * 60 * 60 * 3;
	}

	public interface JsonProperties {

		String ID = "id";
		String DESCRICAO = "descricao";
		String LATITUDE = "latitude";
		String LONGITUDE = "longitude";
		String LOCALIDADE = "localidade";
		String PROMOCOES = "promocoes";
		String LOGADO = "logged";
		String USUARIO_ID = "usuarioId";

	}

	public interface Http {

		public interface Mensagens {
			String VALIDACAO_USUARIO = "Usuário não pode estar nulo!";
			String FALHA_AUTENTICACAO = "A autenticação falhou por motivos técnicos. Tente novamente mais tarde";
		}

		String URL_AUTH = "http://192.168.1.102:8080/TestAndroid/autenticar";
		String URL_PROMOCOES = "http://192.168.1.102:8080/TestAndroid/promocoes";
		String URL_ZAPEAT = "http://www.google.com.br";
		String PARAMETRO_RETORNO = "PARAM_RETORNO";
		String PARAMETRO_LOGIN = "login";
		String PARAMETRO_SENHA = "senha";
	}
}
