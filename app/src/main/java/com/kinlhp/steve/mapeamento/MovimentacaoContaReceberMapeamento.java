package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.MovimentacaoContaReceber;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 5/21/18.
 */
public final class MovimentacaoContaReceberMapeamento implements Serializable {
	private static final long serialVersionUID = 7891713688753618492L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private MovimentacaoContaReceberMapeamento() {
	}

	@NonNull
	public static MovimentacaoContaReceber paraDominio(@NonNull MovimentacaoContaReceberDTO dto) {
		final MovimentacaoContaReceber dominio = MovimentacaoContaReceber.builder()
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
	public static Set<MovimentacaoContaReceber> paraDominios(@NonNull Set<MovimentacaoContaReceberDTO> dtos) {
		Set<MovimentacaoContaReceber> dominios = new LinkedHashSet<>();
		for (MovimentacaoContaReceberDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static MovimentacaoContaReceberDTO paraDTO(@NonNull MovimentacaoContaReceber dominio) {
		return MovimentacaoContaReceberDTO.builder()
				.baseCalculo(dominio.getBaseCalculo())
				.condicaoPagamento(URL_BASE + "condicoespagamento/" + dominio.getCondicaoPagamento().getId().intValue())
				.contaReceber(URL_BASE + "contasreceber/" + dominio.getContaReceber().getId().intValue())
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
