package com.kinlhp.steve.dominio;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 9/27/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"ordem", "numeroParcela"})
@Getter
@NoArgsConstructor
@Setter
public class ContaReceber extends Dominio<BigInteger> {
	private static final long serialVersionUID = 7140515277095491605L;
	private CondicaoPagamento condicaoPagamento;
	private Date dataVencimento;
	@Builder.Default
	private BigDecimal montantePago = BigDecimal.ZERO;
	@Builder.Default
	private BigInteger numeroParcela = BigInteger.ZERO;
	private String observacao;
	private Ordem ordem;
	private Pessoa sacado;
	private BigDecimal saldoDevedor;
	@Builder.Default
	private Situacao situacao = Situacao.ABERTO;
	private BigDecimal valor;

	@AllArgsConstructor
	@Getter
	public enum Situacao {
		ABERTO("Aberto"),
		AMORTIZADO("Amortizado"),
		BAIXADO("Baixado"),
		CANCELADO("Cancelado");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
