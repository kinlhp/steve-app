package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.CondicaoPagamentoDTOLinks;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 9/25/17.
 */
@Builder
@Getter
public class CondicaoPagamentoDTO extends DTO {
	private static final long serialVersionUID = 6474527718913748449L;

	@SerializedName(value = "descricao")
	private String descricao;

	@SerializedName(value = "formaPagamento")
	private String formaPagamento;

	@SerializedName(value = "_links")
	private CondicaoPagamentoDTOLinks links;

	@SerializedName(value = "periodoEntreParcelas")
	private BigInteger periodoEntreParcelas;

	@SerializedName(value = "quantidadeParcelas")
	private BigInteger quantidadeParcelas;
}
