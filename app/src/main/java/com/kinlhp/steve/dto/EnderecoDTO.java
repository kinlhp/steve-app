package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.EnderecoDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 8/16/17.
 */
@Builder
@Getter
public class EnderecoDTO extends DTO {
	private static final long serialVersionUID = 3955029816275248266L;

	@SerializedName(value = "bairro")
	private String bairro;

	@SerializedName(value = "cep")
	private String cep;

	@SerializedName(value = "cidade")
	private String cidade;

	@SerializedName(value = "complemento")
	private String complemento;

	@SerializedName(value = "complemento2")
	private String complemento2;

	@SerializedName(value = "ibge")
	private Integer ibge;

	@SerializedName(value = "_links")
	private EnderecoDTOLinks links;

	@SerializedName(value = "logradouro")
	private String logradouro;

	@SerializedName(value = "pessoa")
	private String pessoa;

	@SerializedName(value = "nomeContato")
	private String nomeContato;

	@Builder.Default
	@SerializedName(value = "numero")
	private String numero = "s.n.º";

	@Builder.Default
	@SerializedName(value = "tipo")
	private TipoDTO tipo = TipoDTO.PRINCIPAL;

	@SerializedName(value = "uf")
	private String uf;

	@AllArgsConstructor
	@Getter
	public enum TipoDTO {
		ENTREGA("Entrega"),
		PESSOAL("Pessoal"),
		PRINCIPAL("Principal"),
		OUTRO("Outro"),
		TRABALHO("Trabalho");

		private final String descricao;
	}
}
