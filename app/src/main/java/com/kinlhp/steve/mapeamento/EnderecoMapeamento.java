package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 8/14/17.
 */
public final class EnderecoMapeamento implements Serializable {
	private static final long serialVersionUID = 8229964908900339986L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private EnderecoMapeamento() {
	}

	@NonNull
	public static Endereco paraDominio(@NonNull EnderecoDTO dto,
	                                   @NonNull final Pessoa pessoa) {
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
				.pessoa(pessoa)
				.tipo(Endereco.Tipo.valueOf(dto.getTipo().name()))
				.build();
		dominio.setDataAlteracao(dto.getDataAlteracao());
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Endereco> paraDominios(@NonNull Set<EnderecoDTO> dtos,
	                                         @NonNull final Pessoa pessoa) {
		Set<Endereco> dominios = new LinkedHashSet<>();
		for (EnderecoDTO dto : dtos) {
			dominios.add(paraDominio(dto, pessoa));
		}
		return dominios;
	}

	@NonNull
	public static EnderecoDTO paraDTO(@NonNull Endereco dominio) {
		return EnderecoDTO.builder()
				.bairro(dominio.getBairro())
				.cep(dominio.getCep())
				.cidade(dominio.getCidade())
				.complemento(dominio.getComplemento())
				.complemento2(dominio.getComplemento2())
				.ibge(dominio.getIbge())
				.logradouro(dominio.getLogradouro())
				.pessoa(URL_BASE + "pessoas/" + dominio.getPessoa().getId().intValue())
				.nomeContato(dominio.getNomeContato())
				.numero(dominio.getNumero())
				.tipo(EnderecoDTO.TipoDTO.valueOf(dominio.getTipo().name()))
				.uf(URL_BASE + "ufs/" + dominio.getUf().getId().intValue())
				.build();
	}

	@NonNull
	public static Set<EnderecoDTO> paraDTOs(@NonNull Set<Endereco> dominios) {
		Set<EnderecoDTO> dtos = new LinkedHashSet<>();
		for (Endereco dominio : dominios) {
			dtos.add(paraDTO(dominio));
		}
		return dtos;
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
