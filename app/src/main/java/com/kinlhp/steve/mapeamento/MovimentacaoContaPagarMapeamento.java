package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dominio.MovimentacaoContaPagar;
import com.kinlhp.steve.dto.MovimentacaoContaPagarDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 5/22/18.
 */
public final class MovimentacaoContaPagarMapeamento implements Serializable {
	private static final long serialVersionUID = 2838617931004425580L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private MovimentacaoContaPagarMapeamento() {
	}

	@NonNull
	public static MovimentacaoContaPagar paraDominio(@NonNull MovimentacaoContaPagarDTO dto) {
		final MovimentacaoContaPagar dominio = MovimentacaoContaPagar.builder()
				.baseCalculo(dto.getBaseCalculo())
				.descontoConcedido(dto.getDescontoConcedido())
				.estornado(dto.isEstornado())
				.juroAplicado(dto.getJuroAplicado())
				.observacao(dto.getObservacao())
				.saldoDevedor(dto.getSaldoDevedor())
				.valorPago(dto.getValorPago())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static MovimentacaoContaPagar paraDominio(@NonNull MovimentacaoContaPagarDTO dto,
	                                                 @NonNull ContaPagar contaPagar) {
		final MovimentacaoContaPagar dominio = MovimentacaoContaPagar.builder()
				.baseCalculo(dto.getBaseCalculo())
				.contaPagar(contaPagar)
				.descontoConcedido(dto.getDescontoConcedido())
				.estornado(dto.isEstornado())
				.juroAplicado(dto.getJuroAplicado())
				.observacao(dto.getObservacao())
				.saldoDevedor(dto.getSaldoDevedor())
				.valorPago(dto.getValorPago())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<MovimentacaoContaPagar> paraDominios(@NonNull Set<MovimentacaoContaPagarDTO> dtos) {
		Set<MovimentacaoContaPagar> dominios = new LinkedHashSet<>();
		for (MovimentacaoContaPagarDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	@NonNull
	public static Set<MovimentacaoContaPagar> paraDominios(@NonNull Set<MovimentacaoContaPagarDTO> dtos,
	                                                       @NonNull ContaPagar contaPagar) {
		Set<MovimentacaoContaPagar> dominios = new LinkedHashSet<>();
		for (MovimentacaoContaPagarDTO dto : dtos) {
			dominios.add(paraDominio(dto, contaPagar));
		}
		return dominios;
	}

	public static MovimentacaoContaPagarDTO paraDTO(@NonNull MovimentacaoContaPagar dominio) {
		return MovimentacaoContaPagarDTO.builder()
				.baseCalculo(dominio.getBaseCalculo())
				.condicaoPagamento(URL_BASE + "condicoesPagamento/" + dominio.getCondicaoPagamento().getId().intValue())
				.contaPagar(URL_BASE + "contasPagar/" + dominio.getContaPagar().getId().intValue())
				.descontoConcedido(dominio.getDescontoConcedido())
				.estornado(dominio.isEstornado())
				.juroAplicado(dominio.getJuroAplicado())
				.observacao(dominio.getObservacao())
				.saldoDevedor(dominio.getSaldoDevedor())
				.valorPago(dominio.getValorPago())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
