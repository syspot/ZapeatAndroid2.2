package com.zapeat.model;

import java.util.Date;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Promocao extends OverlayItem {

	public Promocao() {
		super(null, null, null);
	}

	public Promocao(GeoPoint point,Promocao promocao) {
		super(point, promocao.getLocalidade(), promocao.getDescricao());
		this.id = promocao.getId();
		this.localidade = promocao.getLocalidade();
		this.latitude = promocao.getLatitude();
		this.longitude = promocao.getLongitude();
		this.descricao = promocao.getDescricao();
		this.dataAnuncio = promocao.getDataAnuncio();
		this.dataFinal = promocao.getDataFinal();
		this.horaFinal = promocao.getHoraFinal();
		this.precoOriginal = promocao.getPrecoOriginal();
		this.precoPromocional = promocao.getPrecoPromocional();
		this.idFornecedor = promocao.getIdFornecedor();
	}

	private Long id;
	private String localidade;
	private double latitude;
	private double longitude;
	private String descricao;
	private Date dataAnuncio;
	private String dataFinal;
	private String horaFinal;
	private String precoOriginal;
	private String precoPromocional;
	private Long idFornecedor;

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataAnuncio() {
		return dataAnuncio;
	}

	public void setDataAnuncio(Date dataAnuncio) {
		this.dataAnuncio = dataAnuncio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(String horaFinal) {
		this.horaFinal = horaFinal;
	}

	public String getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(String dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getPrecoOriginal() {
		return precoOriginal;
	}

	public void setPrecoOriginal(String precoOriginal) {
		this.precoOriginal = precoOriginal;
	}

	public String getPrecoPromocional() {
		return precoPromocional;
	}

	public void setPrecoPromocional(String precoPromocional) {
		this.precoPromocional = precoPromocional;
	}

	public Long getIdFornecedor() {
		return idFornecedor;
	}

	public void setIdFornecedor(Long idFornecedor) {
		this.idFornecedor = idFornecedor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Promocao other = (Promocao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
