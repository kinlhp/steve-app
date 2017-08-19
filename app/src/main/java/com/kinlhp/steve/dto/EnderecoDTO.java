package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.EnderecoDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kin on 8/16/17.
 */
@Getter
public class EnderecoDTO extends DTO {
	private static final long serialVersionUID = -3957576172168130815L;

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

	@SerializedName(value = "nomeContato")
	private String nomeContato;

	@SerializedName(value = "numero")
	private String numero = "s.n.º";

	@SerializedName(value = "tipo")
	private TipoDTO tipo = TipoDTO.PRINCIPAL;

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
