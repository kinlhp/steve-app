package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.MovimentacaoContaReceberDTOLinks;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 5/21/18.
 */
@Builder
@Getter
public class MovimentacaoContaReceberDTO extends DTO {
	private static final long serialVersionUID = 2564519630754288197L;

	@Builder.Default
	@SerializedName(value = "baseCalculo")
	private BigDecimal baseCalculo = BigDecimal.ZERO;

	@SerializedName(value = "condicaoPagamento")
	private String condicaoPagamento;

	@SerializedName(value = "contaReceber")
	private String contaReceber;

	@Builder.Default
	@SerializedName(value = "descontoConcedido")
	private BigDecimal descontoConcedido = BigDecimal.ZERO;

	@SerializedName(value = "estornado")
	private boolean estornado;

	@Builder.Default
	@SerializedName(value = "juroAplicado")
	private BigDecimal juroAplicado = BigDecimal.ZERO;

	@SerializedName(value = "_links")
	private MovimentacaoContaReceberDTOLinks links;

	@SerializedName(value = "observacao")
	private String observacao;

	@Builder.Default
	@SerializedName(value = "saldoDevedor")
	private BigDecimal saldoDevedor = BigDecimal.ZERO;

	@Builder.Default
	@SerializedName(value = "valorPago")
	private BigDecimal valorPago = BigDecimal.ZERO;
}
