package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Servico;
import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 9/20/17.
 */
public final class ServicoMapeamento implements Serializable {
	private static final long serialVersionUID = 1925527742514492926L;

	private ServicoMapeamento() {
	}

	@NonNull
	public static Servico paraDominio(@NonNull ServicoDTO dto) {
		final Servico dominio = Servico.builder()
				.descricao(dto.getDescricao())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Servico> paraDominios(@NonNull Set<ServicoDTO> dtos) {
		Set<Servico> dominios = new LinkedHashSet<>();
		for (ServicoDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static ServicoDTO paraDTO(@NonNull Servico dominio) {
		return ServicoDTO.builder()
				.descricao(dominio.getDescricao())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
