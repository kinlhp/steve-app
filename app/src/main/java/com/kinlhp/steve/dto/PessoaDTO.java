package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.PessoaDTOLinks;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kin on 8/15/17.
 */
@Getter
public class PessoaDTO extends DTO {
	private static final long serialVersionUID = -8812673431932202280L;

	@SerializedName(value = "aberturaNascimento")
	private Date aberturaNascimento;

	@SerializedName(value = "cnpjCpf")
	private String cnpjCpf;

	@SerializedName(value = "fantasiaSobrenome")
	private String fantasiaSobrenome;

	@SerializedName(value = "ieRg")
	private String ieRg;

	@SerializedName(value = "_links")
	private PessoaDTOLinks links;

	@SerializedName(value = "nomeRazao")
	private String nomeRazao;

	@SerializedName(value = "perfilCliente")
	private boolean perfilCliente = true;

	@SerializedName(value = "perfilFornecedor")
	private boolean perfilFornecedor;

	@SerializedName(value = "perfilTransportador")
	private boolean perfilTransportador;

	@SerializedName(value = "perfilUsuario")
	private boolean perfilUsuario;

	@SerializedName(value = "situacao")
	private SituacaoDTO situacao = SituacaoDTO.ATIVO;

	@SerializedName(value = "tipo")
	private TipoDTO tipo;

	@AllArgsConstructor
	@Getter
	public enum SituacaoDTO {
		ATIVO("Ativo"),
		INATIVO("Inativo");

		private final String descricao;
	}

	@AllArgsConstructor
	@Getter
	public enum TipoDTO {
		FISICA("Física"),
		JURIDICA("Jurídica"),
		SISTEMA("Sistema");

		private final String descricao;
	}
}
