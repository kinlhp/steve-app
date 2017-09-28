package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.CredencialDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Builder
@Getter
public class CredencialDTO extends DTO {
	private static final long serialVersionUID = -5621024289666202530L;

	@SerializedName(value = "funcionario")
	private String funcionario;

	@SerializedName(value = "_links")
	private CredencialDTOLinks links;

	@SerializedName(value = "perfilAdministrador")
	private boolean perfilAdministrador;

	@Builder.Default
	@SerializedName(value = "perfilPadrao")
	private boolean perfilPadrao = true;

	@SerializedName(value = "perfilSistema")
	private boolean perfilSistema;

	@SerializedName(value = "senha")
	private String senha;

	@Builder.Default
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
