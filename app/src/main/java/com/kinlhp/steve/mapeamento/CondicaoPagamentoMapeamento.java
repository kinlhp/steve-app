package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 9/25/17.
 */
public final class CondicaoPagamentoMapeamento implements Serializable {
	private static final long serialVersionUID = 2468486353480912744L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private CondicaoPagamentoMapeamento() {
	}

	@NonNull
	public static CondicaoPagamento paraDominio(@NonNull CondicaoPagamentoDTO dto,
	                                            @NonNull final FormaPagamento formaPagamento) {
		final CondicaoPagamento dominio = CondicaoPagamento.builder()
				.descricao(dto.getDescricao())
				.formaPagamento(formaPagamento)
				.periodoEntreParcelas(dto.getPeriodoEntreParcelas())
				.quantidadeParcelas(dto.getQuantidadeParcelas())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<CondicaoPagamento> paraDominios(@NonNull Set<CondicaoPagamentoDTO> dtos,
	                                                  @NonNull final FormaPagamento formaPagamento) {
		Set<CondicaoPagamento> dominios = new LinkedHashSet<>();
		for (CondicaoPagamentoDTO dto : dtos) {
			dominios.add(paraDominio(dto, formaPagamento));
		}
		return dominios;
	}

	@NonNull
	public static CondicaoPagamentoDTO paraDTO(@NonNull CondicaoPagamento dominio) {
		return CondicaoPagamentoDTO.builder()
				.descricao(dominio.getDescricao())
				.periodoEntreParcelas(dominio.getPeriodoEntreParcelas())
				.quantidadeParcelas(dominio.getQuantidadeParcelas())
				.formaPagamento(URL_BASE + "formaspagamento/" + dominio.getFormaPagamento().getId().intValue())
				.build();
	}

	@NonNull
	public static Set<CondicaoPagamentoDTO> paraDTOs(@NonNull Set<CondicaoPagamento> dominios) {
		Set<CondicaoPagamentoDTO> dtos = new LinkedHashSet<>();
		for (CondicaoPagamento dominio : dominios) {
			dtos.add(paraDTO(dominio));
		}
		return dtos;
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
