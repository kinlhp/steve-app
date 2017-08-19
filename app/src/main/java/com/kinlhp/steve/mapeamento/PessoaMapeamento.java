package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by kin on 8/15/17.
 */
public final class PessoaMapeamento implements Serializable {
	private static final long serialVersionUID = 9159284225891663601L;

	private PessoaMapeamento() {
	}

	public static Pessoa deDTO(@NonNull PessoaDTO dto) {
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

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
