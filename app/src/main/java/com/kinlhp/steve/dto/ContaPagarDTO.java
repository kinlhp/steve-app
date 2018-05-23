package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.ContaPagarDTOLinks;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 10/10/17.
 */
@Builder
@Getter
public class ContaPagarDTO extends DTO {
	private static final long serialVersionUID = -591028128306225476L;

	@SerializedName(value = "cedente")
	private String cedente;

	@SerializedName(value = "dataEmissao")
	private Date dataEmissao;

	@SerializedName(value = "dataVencimento")
	private Date dataVencimento;

	@SerializedName(value = "fatura")
	private String fatura;

	@SerializedName(value = "_links")
	private ContaPagarDTOLinks links;

	@SerializedName(value = "mesReferente")
	private String mesReferente;

	@Builder.Default
	@SerializedName(value = "numeroParcela")
	private BigInteger numeroParcela = BigInteger.ZERO;

	@SerializedName(value = "observacao")
	private String observacao;

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
