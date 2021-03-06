package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.dto.ContaReceberDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 9/28/17.
 */
public final class ContaReceberMapeamento implements Serializable {
	private static final long serialVersionUID = 5547516998229087978L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private ContaReceberMapeamento() {
	}

	@NonNull
	public static ContaReceber paraDominio(@NonNull ContaReceberDTO dto) {
		final ContaReceber dominio = ContaReceber.builder()
				.dataVencimento(dto.getDataVencimento())
				.numeroParcela(dto.getNumeroParcela())
				.observacao(dto.getObservacao())
				.situacao(ContaReceber.Situacao.valueOf(dto.getSituacao().name()))
				.valor(dto.getValor())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<ContaReceber> paraDominios(@NonNull Set<ContaReceberDTO> dtos) {
		Set<ContaReceber> dominios = new LinkedHashSet<>();
		for (ContaReceberDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static ContaReceberDTO paraDTO(@NonNull ContaReceber dominio) {
		return ContaReceberDTO.builder()
				.condicaoPagamento(URL_BASE + "condicoespagamento/" + dominio.getCondicaoPagamento().getId().intValue())
				.dataVencimento(dominio.getDataVencimento())
				.numeroParcela(dominio.getNumeroParcela())
				.observacao(dominio.getObservacao())
				.ordem(URL_BASE + "ordens/" + dominio.getOrdem().getId().intValue())
				.sacado(URL_BASE + "pessoas/" + dominio.getSacado().getId().intValue())
				.situacao(ContaReceberDTO.SituacaoDTO.valueOf(dominio.getSituacao().name()))
				.valor(dominio.getValor())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
