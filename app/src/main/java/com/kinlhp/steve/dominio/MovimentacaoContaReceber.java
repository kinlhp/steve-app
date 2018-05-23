package com.kinlhp.steve.dominio;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 5/21/18.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@Setter
public class MovimentacaoContaReceber extends Dominio<BigInteger> {
	private static final long serialVersionUID = -1112915697040477620L;
	@Builder.Default
	private BigDecimal baseCalculo = BigDecimal.ZERO;
	private CondicaoPagamento condicaoPagamento;
	private ContaReceber contaReceber;
	@Builder.Default
	private BigDecimal descontoConcedido = BigDecimal.ZERO;
	private boolean estornado;
	@Builder.Default
	private BigDecimal juroAplicado = BigDecimal.ZERO;
	private String observacao;
	@Builder.Default
	private BigDecimal saldoDevedor = BigDecimal.ZERO;
	@Builder.Default
	private BigDecimal valorPago = BigDecimal.ZERO;
}
