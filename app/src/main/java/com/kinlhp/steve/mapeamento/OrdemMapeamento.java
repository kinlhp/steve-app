package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dto.OrdemDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 9/21/17.
 */
public final class OrdemMapeamento implements Serializable {
	private static final long serialVersionUID = -4527950219277426145L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private OrdemMapeamento() {
	}

	@NonNull
	public static Ordem paraDominio(@NonNull OrdemDTO dto) {
		final Ordem dominio = Ordem.builder()
				.observacao(dto.getObservacao())
				.situacao(Ordem.Situacao.valueOf(dto.getSituacao().name()))
				.tipo(Ordem.Tipo.valueOf(dto.getTipo().name()))
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Ordem> paraDominios(@NonNull Set<OrdemDTO> dtos) {
		Set<Ordem> dominios = new LinkedHashSet<>();
		for (OrdemDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static OrdemDTO paraDTO(@NonNull Ordem dominio) {
		return OrdemDTO.builder()
				.observacao(dominio.getObservacao())
				.situacao(OrdemDTO.SituacaoDTO.valueOf(dominio.getSituacao().name()))
				.tipo(OrdemDTO.TipoDTO.valueOf(dominio.getTipo().name()))
				.cliente(URL_BASE + "pessoas/" + dominio.getCliente().getId().intValue())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
