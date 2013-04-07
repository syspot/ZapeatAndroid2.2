package com.zapeat.model;

import java.io.Serializable;

import org.apache.http.entity.mime.content.ByteArrayBody;

@SuppressWarnings("serial")
public class FornecedorCheckInModel implements Serializable {

	private Long fornecedor;
	private String tokenUsuario;
	private String texto;
	private boolean postarFacebook;
	private ByteArrayBody image;
	private String imagem;

	public Long getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Long fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getTokenUsuario() {
		return tokenUsuario;
	}

	public void setTokenUsuario(String tokenUsuario) {
		this.tokenUsuario = tokenUsuario;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public boolean getPostarFacebook() {
		return postarFacebook;
	}

	public void setPostarFacebook(boolean postarFacebook) {
		this.postarFacebook = postarFacebook;
	}

	public ByteArrayBody getImage() {
		return image;
	}

	public void setImage(ByteArrayBody image) {
		this.image = image;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

}
