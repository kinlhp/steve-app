package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dto.ContaPagarDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 10/10/17.
 */
public final class ContaPagarMapeamento implements Serializable {
	private static final long serialVersionUID = 7270351346634036031L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private ContaPagarMapeamento() {
	}

	@NonNull
	public static ContaPagar paraDominio(@NonNull ContaPagarDTO dto) {
		final ContaPagar dominio = ContaPagar.builder()
				.dataEmissao(dto.getDataEmissao())
				.dataVencimento(dto.getDataVencimento())
				.fatura(dto.getFatura())
				.mesReferente(dto.getMesReferente())
				.numeroParcela(dto.getNumeroParcela())
				.observacao(dto.getObservacao())
				.situacao(ContaPagar.Situacao.valueOf(dto.getSituacao().name()))
				.valor(dto.getValor())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<ContaPagar> paraDominios(@NonNull Set<ContaPagarDTO> dtos) {
		Set<ContaPagar> dominios = new LinkedHashSet<>();
		for (ContaPagarDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static ContaPagarDTO paraDTO(@NonNull ContaPagar dominio) {
		return ContaPagarDTO.builder()
				.cedente(URL_BASE + "pessoas/" + dominio.getCedente().getId().intValue())
				.dataEmissao(dominio.getDataEmissao())
				.dataVencimento(dominio.getDataVencimento())
				.fatura(dominio.getFatura())
				.mesReferente(dominio.getMesReferente())
				.numeroParcela(dominio.getNumeroParcela())
				.observacao(dominio.getObservacao())
				.situacao(ContaPagarDTO.SituacaoDTO.valueOf(dominio.getSituacao().name()))
				.valor(dominio.getValor())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
