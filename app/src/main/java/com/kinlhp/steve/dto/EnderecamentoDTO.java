package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by kin on 9/9/17.
 */
@Getter
public class EnderecamentoDTO extends DTO {
	private static final long serialVersionUID = 4589445497824448578L;

	@SerializedName(value = "bairro")
	private String bairro;

	@SerializedName(value = "cep")
	private String cep;

	@SerializedName(value = "complemento")
	private String complemento;

	@SerializedName(value = "erro")
	private boolean erro;

	@SerializedName(value = "ibge")
	private Integer ibge;

	@SerializedName(value = "localidade")
	private String localidade;

	@SerializedName(value = "logradouro")
	private String logradouro;

	@SerializedName(value = "uf")
	private UfDTO.SiglaDTO uf;
}
