package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.MovimentacaoContaReceberRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 5/22/18.
 */
public class MovimentacaoContaReceberRequisicao implements Serializable {
	private static final long serialVersionUID = 4615671719914120431L;
	private static final MovimentacaoContaReceberRecurso RECURSO = Requisicao
			.criar(MovimentacaoContaReceberRecurso.class);

	private MovimentacaoContaReceberRequisicao() {
	}

	public static void getCondicaoPagamento(@NonNull Callback<CondicaoPagamentoDTO> callback,
	                                        @NonNull HRef uf) {
		RECURSO.getCondicaoPagamento(uf.getHref()).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull MovimentacaoContaReceberDTO dto) {
		// TODO: 5/21/18 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id,
	                       @NonNull MovimentacaoContaReceberDTO dto) {
		// TODO: 5/21/18 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
