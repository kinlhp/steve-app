package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Telefone;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by kin on 8/14/17.
 */
public final class TelefoneMapeamento implements Serializable {
	private static final long serialVersionUID = -6101823022445309247L;

	private TelefoneMapeamento() {
	}

	public static Telefone deDTO(@NonNull TelefoneDTO dto) {
		final Telefone dominio = Telefone.builder()
				.nomeContato(dto.getNomeContato())
				.numero(dto.getNumero())
				.tipo(Telefone.Tipo.valueOf(dto.getTipo().name()))
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
