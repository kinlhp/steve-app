package com.kinlhp.steve.dominio;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 8/13/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"usuario"})
@Getter
@NoArgsConstructor
@Setter
public class Credencial extends Dominio<BigInteger> {
	private static final long serialVersionUID = -8704164196482710770L;
	private Pessoa funcionario;
	private boolean perfilAdministrador;
	@Builder.Default
	private boolean perfilPadrao = true;
	private boolean perfilSistema;
	private String senha;
	@Builder.Default
	private Situacao situacao = Situacao.ATIVO;
	private String usuario;

	public String perfisToString() {
		StringBuilder perfis = new StringBuilder(isPerfilPadrao()
				? "Padrão" : "");
		perfis.append(isPerfilAdministrador()
				? perfis.length() > 0
				? " | Administrador"
				: ""
				: "");
		perfis.append(isPerfilSistema()
				? perfis.length() > 0
				? " | Sistema"
				: ""
				: "");
		return perfis.toString();
	}

	@AllArgsConstructor
	@Getter
	public enum Situacao {
		ATIVO("Ativo"),
		INATIVO("Inativo");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
