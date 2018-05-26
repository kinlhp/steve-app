package com.kinlhp.steve.dominio;

import com.kinlhp.steve.util.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

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
	private static final long serialVersionUID = -111650151714802034L;
	private CondicaoPagamento condicaoPagamento;
	private Date dataVencimento;
	@Builder.Default
	private Set<MovimentacaoContaReceber> movimentacoes = new LinkedHashSet<>();
	@Builder.Default
	private BigInteger numeroParcela = BigInteger.ZERO;
	private String observacao;
	private Ordem ordem;
	private Pessoa sacado;
	@Builder.Default
	private Situacao situacao = Situacao.ABERTO;
	private BigDecimal valor;

	public BigDecimal getMontantePago() {
		if (movimentacoes == null || movimentacoes.isEmpty()) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
		}
		BigDecimal montantePago = BigDecimal.ZERO;
		for (MovimentacaoContaReceber movimentacao : movimentacoes) {
			if (!movimentacao.isEstornado()) {
				montantePago = montantePago.add(movimentacao.getValorPago());
			}
		}
		return montantePago.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getSaldoDevedor() {
		if (movimentacoes == null || movimentacoes.isEmpty()) {
			return valor;
		}
		MovimentacaoContaReceber movimentacaoMaisRecente = MovimentacaoContaReceber
				.builder().build();
		for (MovimentacaoContaReceber movimentacao : movimentacoes) {
			if (!movimentacao.isEstornado()) {
				if (movimentacaoMaisRecente.getDataCriacao() == null) {
					movimentacaoMaisRecente = movimentacao;
				} else if (movimentacao.getDataCriacao().compareTo(movimentacaoMaisRecente.getDataCriacao()) > 0) {
					movimentacaoMaisRecente = movimentacao;
				}
			}
		}
		return movimentacaoMaisRecente.getSaldoDevedor();
	}

	public boolean hasMontantePago() {
		final BigDecimal montantePago = getMontantePago();
		return BigDecimal.ZERO.compareTo(montantePago) < 0;
	}

	public boolean hasSaldoDevedor() {
		final BigDecimal saldoDevedor = getSaldoDevedor();
		return BigDecimal.ZERO.compareTo(saldoDevedor) < 0;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(sacado != null && sacado.getNomeRazao() != null ? sacado.getNomeRazao() : "")
				.append(dataVencimento != null ? " " + Data.paraStringData(dataVencimento) : "")
				.toString();
	}

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
