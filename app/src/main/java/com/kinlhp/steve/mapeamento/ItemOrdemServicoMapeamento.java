package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 9/21/17.
 */
public final class ItemOrdemServicoMapeamento implements Serializable {
	private static final long serialVersionUID = -3694689548022885583L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private ItemOrdemServicoMapeamento() {
	}

	@NonNull
	public static ItemOrdemServico paraDominio(@NonNull ItemOrdemServicoDTO dto,
	                                           @NonNull Ordem ordem) {
		final ItemOrdemServico dominio = ItemOrdemServico.builder()
				.dataFinalizacaoPrevista(dto.getDataFinalizacaoPrevista())
				.descricao(dto.getDescricao())
				.ordem(ordem)
				.situacao(ItemOrdemServico.Situacao.valueOf(dto.getSituacao().name()))
				.valorOrcamento(dto.getValorOrcamento())
				.valorServico(dto.getValorServico())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<ItemOrdemServico> paraDominios(@NonNull Set<ItemOrdemServicoDTO> dtos,
	                                                 @NonNull Ordem ordem) {
		Set<ItemOrdemServico> dominios = new LinkedHashSet<>();
		for (ItemOrdemServicoDTO dto : dtos) {
			dominios.add(paraDominio(dto, ordem));
		}
		return dominios;
	}

	@NonNull
	public static ItemOrdemServicoDTO paraDTO(@NonNull ItemOrdemServico dominio) {
		return ItemOrdemServicoDTO.builder()
				.dataFinalizacaoPrevista(dominio.getDataFinalizacaoPrevista())
				.descricao(dominio.getDescricao())
				.ordem(URL_BASE + "ordens/" + dominio.getOrdem().getId().intValue())
				.servico(URL_BASE + "servicos/" + dominio.getServico().getId().intValue())
				.situacao(ItemOrdemServicoDTO.SituacaoDTO.valueOf(dominio.getSituacao().name()))
				.valorOrcamento(dominio.getValorOrcamento())
				.valorServico(dominio.getValorServico())
				.build();
	}

	@NonNull
	public static Set<ItemOrdemServicoDTO> paraDTOs(@NonNull Set<ItemOrdemServico> dominios) {
		Set<ItemOrdemServicoDTO> dtos = new LinkedHashSet<>();
		for (ItemOrdemServico dominio : dominios) {
			dtos.add(paraDTO(dominio));
		}
		return dtos;
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
