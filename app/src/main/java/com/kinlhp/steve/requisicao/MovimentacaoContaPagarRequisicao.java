package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.MovimentacaoContaPagarDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.MovimentacaoContaPagarRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 5/22/18.
 */
public class MovimentacaoContaPagarRequisicao implements Serializable {
	private static final long serialVersionUID = -4924123242108564119L;
	private static final MovimentacaoContaPagarRecurso RECURSO = Requisicao
			.criar(MovimentacaoContaPagarRecurso.class);

	private MovimentacaoContaPagarRequisicao() {
	}

	public static void getCondicaoPagamento(@NonNull Callback<CondicaoPagamentoDTO> callback,
	                                        @NonNull HRef uf) {
		RECURSO.getCondicaoPagamento(uf.getHref()).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull MovimentacaoContaPagarDTO dto) {
		// TODO: 5/21/18 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id,
	                       @NonNull MovimentacaoContaPagarDTO dto) {
		// TODO: 5/21/18 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
