package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by kin on 8/14/17.
 */
public final class EnderecoMapeamento implements Serializable {
	private static final long serialVersionUID = -3969170210664323421L;

	private EnderecoMapeamento() {
	}

	public static Endereco deDTO(@NonNull EnderecoDTO dto) {
		final Endereco dominio = Endereco.builder()
				.bairro(dto.getBairro())
				.cep(dto.getCep())
				.cidade(dto.getCidade())
				.complemento(dto.getComplemento())
				.complemento2(dto.getComplemento2())
				.ibge(dto.getIbge())
				.logradouro(dto.getLogradouro())
				.nomeContato(dto.getNomeContato())
				.numero(dto.getNumero())
				.tipo(Endereco.Tipo.valueOf(dto.getTipo().name()))
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
