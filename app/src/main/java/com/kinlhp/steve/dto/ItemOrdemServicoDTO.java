package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.ItemOrdemServicoDTOLinks;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 9/21/17.
 */
@Builder
@Getter
public class ItemOrdemServicoDTO extends DTO {
	private static final long serialVersionUID = -2555006770627111281L;

	@SerializedName(value = "dataFinalizacaoPrevista")
	private Date dataFinalizacaoPrevista;

	@SerializedName(value = "descricao")
	private String descricao;

	@SerializedName(value = "_links")
	private ItemOrdemServicoDTOLinks links;

	@SerializedName(value = "ordem")
	private String ordem;

	@SerializedName(value = "servico")
	private String servico;

	@Builder.Default
	@SerializedName(value = "situacao")
	private SituacaoDTO situacao = SituacaoDTO.ABERTO;

	@SerializedName(value = "valorOrcamento")
	private BigDecimal valorOrcamento;

	@SerializedName(value = "valorServico")
	private BigDecimal valorServico;

	@AllArgsConstructor
	@Getter
	public enum SituacaoDTO {
		ABERTO("Aberto"),
		CANCELADO("Cancelado"),
		FINALIZADO("Finalizado");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
