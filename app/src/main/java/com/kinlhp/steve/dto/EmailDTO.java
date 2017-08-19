package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.EmailDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kin on 8/16/17.
 */
@Getter
public class EmailDTO extends DTO {
	private static final long serialVersionUID = 3348452946558232706L;

	@SerializedName(value = "enderecoEletronico")
	private String enderecoEletronico;

	@SerializedName(value = "_links")
	private EmailDTOLinks links;

	@SerializedName(value = "nomeContato")
	private String nomeContato;

	@SerializedName(value = "tipo")
	private TipoDTO tipo = TipoDTO.PRINCIPAL;

	@AllArgsConstructor
	@Getter
	public enum TipoDTO {
		NFE("NF-e"),
		PESSOAL("Pessoal"),
		PRINCIPAL("Principal"),
		OUTRO("Outro"),
		TRABALHO("Trabalho");

		private final String descricao;
	}
}
