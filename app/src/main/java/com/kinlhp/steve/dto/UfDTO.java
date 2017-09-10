package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.UfDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kin on 8/16/17.
 */
@Getter
public class UfDTO extends DTO {
	private static final long serialVersionUID = 1012648384072417701L;

	@SerializedName(value = "ibge")
	private Integer ibge;

	@SerializedName(value = "_links")
	private UfDTOLinks links;

	@SerializedName(value = "nome")
	private String nome;

	@SerializedName(value = "sigla")
	private SiglaDTO sigla;

	@AllArgsConstructor
	@Getter
	public enum SiglaDTO {
		AC("Acre"),
		AL("Alagoas"),
		AM("Amazonas"),
		AP("Amapá"),
		BA("Bahia"),
		CE("Ceará"),
		DF("Distrito Federal"),
		ES("Espírito Santo"),
		EX("Exterior"),
		GO("Goiás"),
		MA("Maranhão"),
		MG("Minas Gerais"),
		MS("Mato Grosso do Sul"),
		MT("Mato Grosso"),
		PA("Pará"),
		PB("Paraíba"),
		PE("Pernambuco"),
		PI("Piauí"),
		PR("Paraná"),
		RJ("Rio de Janeiro"),
		RN("Rio Grande do Norte"),
		RO("Rondônia"),
		RR("Roraima"),
		RS("Rio Grande do Sul"),
		SC("Santa Catarina"),
		SE("Sergipe"),
		SP("São Paulo"),
		TO("Tocantins");

		private final String descricao;
	}
}
