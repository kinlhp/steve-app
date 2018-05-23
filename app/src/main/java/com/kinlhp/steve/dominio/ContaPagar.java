package com.kinlhp.steve.dominio;

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
 * Created by kin on 10/5/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"fatura", "cedente", "numeroParcela"})
@Getter
@NoArgsConstructor
@Setter
public class ContaPagar extends Dominio<BigInteger> {
	private static final long serialVersionUID = 9179186536139228508L;
	private Pessoa cedente;
	private Date dataEmissao;
	private Date dataVencimento;
	private String fatura;
	private String mesReferente;
	@Builder.Default
	private Set<MovimentacaoContaPagar> movimentacoes = new LinkedHashSet<>();
	@Builder.Default
	private BigInteger numeroParcela = BigInteger.ZERO;
	private String observacao;
	@Builder.Default
	private Situacao situacao = Situacao.ABERTO;
	private BigDecimal valor;

	public BigDecimal getMontantePago() {
		if (movimentacoes == null || movimentacoes.isEmpty()) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
		}
		BigDecimal montantePago = BigDecimal.ZERO;
		for (MovimentacaoContaPagar movimentacao : movimentacoes) {
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
		MovimentacaoContaPagar movimentacaoMaisRecente = MovimentacaoContaPagar
				.builder().build();
		for (MovimentacaoContaPagar movimentacao : movimentacoes) {
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
