package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.CredencialDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class CredencialDTO extends DTO {
	private static final long serialVersionUID = -2659480090149114943L;

	@SerializedName(value = "_links")
	private CredencialDTOLinks links;

	@SerializedName(value = "perfilAdministrador")
	private boolean perfilAdministrador;

	@SerializedName(value = "perfilPadrao")
	private boolean perfilPadrao = true;

	@SerializedName(value = "perfilSistema")
	private boolean perfilSistema;

	@SerializedName(value = "senha")
	private String senha;

	@SerializedName(value = "situacao")
	private SituacaoDTO situacao = SituacaoDTO.ATIVO;

	@SerializedName(value = "usuario")
	private String usuario;

	@AllArgsConstructor
	@Getter
	public enum SituacaoDTO {
		ATIVO("Ativo"),
		INATIVO("Inativo");

		private final String descricao;
	}
}
