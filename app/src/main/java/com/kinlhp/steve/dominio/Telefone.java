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
public class Telefone extends Dominio<BigInteger> {
	private static final long serialVersionUID = -8506406724030254042L;
	private String nomeContato;
	private String numero;
	private Pessoa pessoa;
	private Tipo tipo = Tipo.PRINCIPAL;

	@AllArgsConstructor
	@Getter
	public enum Tipo {
		PESSOAL("Pessoal"),
		PRINCIPAL("Principal"),
		OUTRO("Outro"),
		TRABALHO("Trabalho");

		private final String descricao;
	}
}
