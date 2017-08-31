package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Telefone;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 8/14/17.
 */
public final class TelefoneMapeamento implements Serializable {
	private static final long serialVersionUID = 2755118327948801266L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private TelefoneMapeamento() {
	}

	@NonNull
	public static Telefone paraDominio(@NonNull TelefoneDTO dto,
	                                   @NonNull final Pessoa pessoa) {
		final Telefone dominio = Telefone.builder()
				.nomeContato(dto.getNomeContato())
				.numero(dto.getNumero())
				.pessoa(pessoa)
				.tipo(Telefone.Tipo.valueOf(dto.getTipo().name()))
				.build();
		dominio.setDataAlteracao(dto.getDataAlteracao());
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Telefone> paraDominios(@NonNull Set<TelefoneDTO> dtos,
	                                         @NonNull final Pessoa pessoa) {
		Set<Telefone> dominios = new LinkedHashSet<>();
		for (TelefoneDTO dto : dtos) {
			dominios.add(paraDominio(dto, pessoa));
		}
		return dominios;
	}

	@NonNull
	public static TelefoneDTO paraDTO(@NonNull Telefone dominio) {
		return TelefoneDTO.builder()
				.nomeContato(dominio.getNomeContato())
				.numero(dominio.getNumero())
				.pessoa(URL_BASE + "pessoas/" + dominio.getPessoa().getId().intValue())
				.tipo(TelefoneDTO.TipoDTO.valueOf(dominio.getTipo().name()))
				.build();
	}

	@NonNull
	public static Set<TelefoneDTO> paraDTOs(@NonNull Set<Telefone> dominios) {
		Set<TelefoneDTO> dtos = new LinkedHashSet<>();
		for (Telefone dominio : dominios) {
			dtos.add(paraDTO(dominio));
		}
		return dtos;
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
