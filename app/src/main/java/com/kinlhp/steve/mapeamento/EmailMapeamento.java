package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by kin on 8/14/17.
 */
public final class EmailMapeamento implements Serializable {
	private static final long serialVersionUID = 3212763768890330565L;

	private EmailMapeamento() {
	}

	public static Email deDTO(@NonNull EmailDTO dto) {
		final Email dominio = Email.builder()
				.enderecoEletronico(dto.getEnderecoEletronico())
				.nomeContato(dto.getNomeContato())
				.tipo(Email.Tipo.valueOf(dto.getTipo().name()))
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
