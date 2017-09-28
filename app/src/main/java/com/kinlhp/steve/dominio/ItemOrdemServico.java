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
 * Created by kin on 9/20/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@Setter
public class ItemOrdemServico extends Dominio<BigInteger> {
	private static final long serialVersionUID = -2361584473302572890L;
	private Date dataFinalizacaoPrevista;
	private String descricao;
	private Ordem ordem;
	private Servico servico;
	@Builder.Default
	private Situacao situacao = Situacao.ABERTO;
	@Builder.Default
	private BigDecimal valorOrcamento = BigDecimal.ZERO;
	@Builder.Default
	private BigDecimal valorServico = BigDecimal.ZERO;

	@AllArgsConstructor
	@Getter
	public enum Situacao {
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
