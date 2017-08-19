package com.kinlhp.steve.dominio;

import java.math.BigInteger;
import java.util.Date;
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
	private Set<Email> emails;
	private Set<Endereco> enderecos;
	private String fantasiaSobrenome;
	private String ieRg;
	private String nomeRazao;
	private boolean perfilCliente = true;
	private boolean perfilFornecedor;
	private boolean perfilTransportador;
	private boolean perfilUsuario;
	private Situacao situacao = Situacao.ATIVO;
	private Set<Telefone> telefones;
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
	}
}
