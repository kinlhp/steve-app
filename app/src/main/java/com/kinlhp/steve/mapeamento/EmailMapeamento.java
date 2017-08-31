package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 8/14/17.
 */
public final class EmailMapeamento implements Serializable {
	private static final long serialVersionUID = 1735583592857036798L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private EmailMapeamento() {
	}

	@NonNull
	public static Email paraDominio(@NonNull EmailDTO dto,
	                                @NonNull final Pessoa pessoa) {
		final Email dominio = Email.builder()
				.enderecoEletronico(dto.getEnderecoEletronico())
				.nomeContato(dto.getNomeContato())
				.pessoa(pessoa)
				.tipo(Email.Tipo.valueOf(dto.getTipo().name()))
				.build();
		dominio.setDataAlteracao(dto.getDataAlteracao());
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Email> paraDominios(@NonNull Set<EmailDTO> dtos,
	                                      @NonNull final Pessoa pessoa) {
		Set<Email> dominios = new LinkedHashSet<>();
		for (EmailDTO dto : dtos) {
			dominios.add(paraDominio(dto, pessoa));
		}
		return dominios;
	}

	@NonNull
	public static EmailDTO paraDTO(@NonNull Email dominio) {
		return EmailDTO.builder()
				.enderecoEletronico(dominio.getEnderecoEletronico())
				.nomeContato(dominio.getNomeContato())
				.tipo(EmailDTO.TipoDTO.valueOf(dominio.getTipo().name()))
				.pessoa(URL_BASE + "pessoas/" + dominio.getPessoa().getId().intValue())
				.build();
	}

	@NonNull
	public static Set<EmailDTO> paraDTOs(@NonNull Set<Email> dominios) {
		Set<EmailDTO> dtos = new LinkedHashSet<>();
		for (Email dominio : dominios) {
			dtos.add(paraDTO(dominio));
		}
		return dtos;
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
