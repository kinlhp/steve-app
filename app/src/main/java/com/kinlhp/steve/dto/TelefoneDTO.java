package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.TelefoneDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 8/16/17.
 */
@Builder
@Getter
public class TelefoneDTO extends DTO {
	private static final long serialVersionUID = 1172565260944048167L;

	@SerializedName(value = "_links")
	private TelefoneDTOLinks links;

	@SerializedName(value = "nomeContato")
	private String nomeContato;

	@SerializedName(value = "numero")
	private String numero;

	@SerializedName(value = "pessoa")
	private String pessoa;

	@SerializedName(value = "tipo")
	private TipoDTO tipo = TipoDTO.PRINCIPAL;

	@AllArgsConstructor
	@Getter
	public enum TipoDTO {
		PESSOAL("Pessoal"),
		PRINCIPAL("Principal"),
		OUTRO("Outro"),
		TRABALHO("Trabalho");

		private final String descricao;
	}
}
