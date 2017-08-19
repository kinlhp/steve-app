package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Uf;
import com.kinlhp.steve.dto.UfDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by kin on 8/14/17.
 */
public final class UfMapeamento implements Serializable {
	private static final long serialVersionUID = 528608315121505588L;

	private UfMapeamento() {
	}

	public static Uf deDTO(@NonNull UfDTO dto) {
		final Uf dominio = Uf.builder()
				.ibge(dto.getIbge())
				.nome(dto.getNome())
				.sigla(Uf.Sigla.valueOf(dto.getSigla().name()))
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
