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
@EqualsAndHashCode(callSuper = true, of = {"pessoa", "tipo"})
@Getter
@NoArgsConstructor
@Setter
public class Email extends Dominio<BigInteger> {
	private static final long serialVersionUID = 998895710042275633L;
	private String enderecoEletronico;
	private String nomeContato;
	private Pessoa pessoa;
	@Builder.Default
	private Tipo tipo = Tipo.PRINCIPAL;

	@AllArgsConstructor
	@Getter
	public enum Tipo {
		NFE("NF-e"),
		PESSOAL("Pessoal"),
		PRINCIPAL("Principal"),
		OUTRO("Outro"),
		TRABALHO("Trabalho");

		private final String descricao;
	}
}
