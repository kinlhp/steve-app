package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 8/15/17.
 */
public final class PessoaMapeamento implements Serializable {
	private static final long serialVersionUID = -5696219413816116607L;

	private PessoaMapeamento() {
	}

	@NonNull
	public static Pessoa paraDominio(@NonNull PessoaDTO dto) {
		final Pessoa dominio = Pessoa.builder()
				.aberturaNascimento(dto.getAberturaNascimento())
				.cnpjCpf(dto.getCnpjCpf())
				.fantasiaSobrenome(dto.getFantasiaSobrenome())
				.ieRg(dto.getIeRg())
				.nomeRazao(dto.getNomeRazao())
				.perfilCliente(dto.isPerfilCliente())
				.perfilFornecedor(dto.isPerfilFornecedor())
				.perfilTransportador(dto.isPerfilTransportador())
				.perfilUsuario(dto.isPerfilUsuario())
				.situacao(Pessoa.Situacao.valueOf(dto.getSituacao().name()))
				.tipo(Pessoa.Tipo.valueOf(dto.getTipo().name()))
				.build();
		dominio.setDataAlteracao(dto.getDataAlteracao());
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Pessoa> paraDominios(@NonNull Set<PessoaDTO> dtos) {
		Set<Pessoa> dominios = new LinkedHashSet<>();
		for (PessoaDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static PessoaDTO paraDTO(@NonNull Pessoa dominio) {
		return PessoaDTO.builder()
				.aberturaNascimento(dominio.getAberturaNascimento())
				.cnpjCpf(dominio.getCnpjCpf())
				.fantasiaSobrenome(dominio.getFantasiaSobrenome())
				.ieRg(dominio.getIeRg())
				.nomeRazao(dominio.getNomeRazao())
				.perfilCliente(dominio.isPerfilCliente())
				.perfilFornecedor(dominio.isPerfilFornecedor())
				.perfilTransportador(dominio.isPerfilTransportador())
				.perfilUsuario(dominio.isPerfilUsuario())
				.situacao(PessoaDTO.SituacaoDTO.valueOf(dominio.getSituacao().name()))
				.tipo(PessoaDTO.TipoDTO.valueOf(dominio.getTipo().name()))
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
