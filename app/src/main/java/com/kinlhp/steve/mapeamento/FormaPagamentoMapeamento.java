package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dto.FormaPagamentoDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 9/20/17.
 */
public final class FormaPagamentoMapeamento implements Serializable {
	private static final long serialVersionUID = -8966409466064813777L;

	private FormaPagamentoMapeamento() {
	}

	@NonNull
	public static FormaPagamento paraDominio(@NonNull FormaPagamentoDTO dto) {
		final FormaPagamento dominio = FormaPagamento.builder()
				.descricao(dto.getDescricao())
				.build();
		dominio.setDataAlteracao(dto.getDataAlteracao());
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<FormaPagamento> paraDominios(@NonNull Set<FormaPagamentoDTO> dtos) {
		Set<FormaPagamento> dominios = new LinkedHashSet<>();
		for (FormaPagamentoDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static FormaPagamentoDTO paraDTO(@NonNull FormaPagamento dominio) {
		return FormaPagamentoDTO.builder()
				.descricao(dominio.getDescricao())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
