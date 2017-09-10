package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;

/**
 * Created by kin on 9/9/17.
 */
@Getter
public class EnderecamentoDTO implements Serializable {
	private static final long serialVersionUID = -1916334220912484281L;

	@SerializedName(value = "bairro")
	private String bairro;

	@SerializedName(value = "cep")
	private String cep;

	@SerializedName(value = "complemento")
	private String complemento;

	@SerializedName(value = "ibge")
	private Integer ibge;

	@SerializedName(value = "localidade")
	private String localidade;

	@SerializedName(value = "logradouro")
	private String logradouro;

	@SerializedName(value = "uf")
	private UfDTO.SiglaDTO uf;
}
