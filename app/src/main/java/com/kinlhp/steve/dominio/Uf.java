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
@EqualsAndHashCode(callSuper = false, of = {"sigla"})
@Getter
@NoArgsConstructor
@Setter
public class Uf extends Dominio<BigInteger> {
	private static final long serialVersionUID = 5642731995434411796L;
	private Integer ibge;
	private String nome;
	private Sigla sigla;

	@AllArgsConstructor
	@Getter
	public enum Sigla {
		AC("Acre"),
		AL("Alagoas"),
		AM("Amazonas"),
		AP("Amapá"),
		BA("Bahia"),
		CE("Ceará"),
		DF("Distrito Federal"),
		ES("Espírito Santo"),
		EX("Exterior"),
		GO("Goiás"),
		MA("Maranhão"),
		MG("Minas Gerais"),
		MS("Mato Grosso do Sul"),
		MT("Mato Grosso"),
		PA("Pará"),
		PB("Paraíba"),
		PE("Pernambuco"),
		PI("Piauí"),
		PR("Paraná"),
		RJ("Rio de Janeiro"),
		RN("Rio Grande do Norte"),
		RO("Rondônia"),
		RR("Roraima"),
		RS("Rio Grande do Sul"),
		SC("Santa Catarina"),
		SE("Sergipe"),
		SP("São Paulo"),
		TO("Tocantins");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
