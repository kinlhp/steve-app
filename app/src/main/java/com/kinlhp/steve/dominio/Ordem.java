package com.kinlhp.steve.dominio;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 9/20/17.
 */
@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class Ordem extends Dominio<BigInteger> {
	private static final long serialVersionUID = -8847348933929825763L;
	private Pessoa cliente;
	@Builder.Default
	private Set<ItemOrdemServico> itens = new LinkedHashSet<>();
	private String observacao;
	@Builder.Default
	private Situacao situacao = Situacao.ABERTO;
	@Builder.Default
	private Tipo tipo = Tipo.ORDEM_SERVICO;

	@Override
	public String toString() {
		StringBuilder ordem = new StringBuilder();
		ordem.append(id != null ? id : "");
		ordem.append(tipo != null && ordem.length() > 0 ? " - " : "");
		ordem.append(tipo != null ? tipo.toString() : "");
		ordem.append(cliente != null && ordem.length() > 0 ? " - " : "");
		ordem.append(cliente != null ? cliente.toString() : "");
		return ordem.toString();
	}

	@AllArgsConstructor
	@Getter
	public enum Situacao {
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
	public enum Tipo {
		ORCAMENTO("Orçamento"),
		ORDEM_SERVICO("Ordem de Serviço");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
