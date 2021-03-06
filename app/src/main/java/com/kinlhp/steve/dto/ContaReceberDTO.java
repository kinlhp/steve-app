package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.ContaReceberDTOLinks;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 9/27/17.
 */
@Builder
@Getter
public class ContaReceberDTO extends DTO {
	private static final long serialVersionUID = 3500448869811941450L;

	@SerializedName(value = "condicaoPagamento")
	private String condicaoPagamento;

	@SerializedName(value = "dataVencimento")
	private Date dataVencimento;

	@SerializedName(value = "_links")
	private ContaReceberDTOLinks links;

	@Builder.Default
	@SerializedName(value = "numeroParcela")
	private BigInteger numeroParcela = BigInteger.ZERO;

	@SerializedName(value = "observacao")
	private String observacao;

	@SerializedName(value = "ordem")
	private String ordem;

	@SerializedName(value = "sacado")
	private String sacado;

	@Builder.Default
	@SerializedName(value = "situacao")
	private SituacaoDTO situacao = SituacaoDTO.ABERTO;

	@SerializedName(value = "valor")
	private BigDecimal valor;

	@AllArgsConstructor
	@Getter
	public enum SituacaoDTO {
		ABERTO("Aberto"),
		AMORTIZADO("Amortizado"),
		BAIXADO("Baixado"),
		CANCELADO("Cancelado");

		private final String descricao;
	}
}
