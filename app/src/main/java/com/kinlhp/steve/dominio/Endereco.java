package com.kinlhp.steve.dominio;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 8/16/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"pessoa", "tipo"})
@Getter
@NoArgsConstructor
@Setter
public class Endereco extends Dominio<BigInteger> {
	private static final long serialVersionUID = -8194589352442654461L;
	private String bairro;
	private String cep;
	private String cidade;
	private String complemento;
	private String complemento2;
	private Integer ibge;
	private String logradouro;
	private String nomeContato;
	@Builder.Default
	private String numero = "s.n.º";
	private Pessoa pessoa;
	@Builder.Default
	private Tipo tipo = Tipo.PRINCIPAL;
	private Uf uf;

	@Override
	public String toString() {
		return new StringBuilder()
				.append(logradouro != null ? logradouro : "")
				.append(numero != null ? ", " + numero : "")
				.append(cidade != null ? " - " + cidade : "")
				.append(uf != null && uf.getSigla() != null
						? "/" + uf.getSigla() : "")
				.toString();
	}

	@AllArgsConstructor
	@Getter
	public enum Tipo {
		ENTREGA("Entrega"),
		PESSOAL("Pessoal"),
		PRINCIPAL("Principal"),
		OUTRO("Outro"),
		TRABALHO("Trabalho");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
