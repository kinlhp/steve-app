package com.kinlhp.steve.dominio;

import java.math.BigInteger;
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
 * Created by kin on 8/15/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, of = {"cnpjCpf"})
@Getter
@NoArgsConstructor
@Setter
public class Pessoa extends Dominio<BigInteger> {
	private static final long serialVersionUID = -8399368200365254075L;
	private Date aberturaNascimento;
	private String cnpjCpf;
	@Builder.Default
	private Set<Email> emails = new LinkedHashSet<>();
	@Builder.Default
	private Set<Endereco> enderecos = new LinkedHashSet<>();
	private String fantasiaSobrenome;
	private String ieRg;
	private String nomeRazao;
	@Builder.Default
	private boolean perfilCliente = true;
	private boolean perfilFornecedor;
	private boolean perfilTransportador;
	private boolean perfilUsuario;
	@Builder.Default
	private Situacao situacao = Situacao.ATIVO;
	@Builder.Default
	private Set<Telefone> telefones = new LinkedHashSet<>();
	private Tipo tipo;

	@AllArgsConstructor
	@Getter
	public enum Situacao {
		ATIVO("Ativo"),
		INATIVO("Inativo");

		private final String descricao;
	}

	@AllArgsConstructor
	@Getter
	public enum Tipo {
		FISICA("Física"),
		JURIDICA("Jurídica"),
		SISTEMA("Sistema");

		private final String descricao;

		@Override
		public String toString() {
			return getDescricao();
		}
	}
}
