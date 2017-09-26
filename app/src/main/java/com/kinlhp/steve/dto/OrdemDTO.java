package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.OrdemDTOLinks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 9/21/17.
 */
@Builder
@Getter
public class OrdemDTO extends DTO {
	private static final long serialVersionUID = -3643537849709888889L;

	@SerializedName(value = "cliente")
	private String cliente;

	@SerializedName(value = "_links")
	private OrdemDTOLinks links;

	@SerializedName(value = "observacao")
	private String observacao;

	@Builder.Default
	@SerializedName(value = "situacao")
	private SituacaoDTO situacao = SituacaoDTO.ABERTO;

	@SerializedName(value = "tipo")
	private TipoDTO tipo = TipoDTO.ORDEM_SERVICO;

	@AllArgsConstructor
	@Getter
	public enum SituacaoDTO {
		ABERTO("Aberto"),
		CANCELADO("Cancelado"),
		FINALIZADO("Finalizado"),
		GERADO("Gerado");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}

	@AllArgsConstructor
	@Getter
	public enum TipoDTO {
		ORCAMENTO("Orçamento"),
		ORDEM_SERVICO("Ordem de Serviço");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
