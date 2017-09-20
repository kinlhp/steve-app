package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.FormaPagamentoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.FormaPagamentoRecurso;
import com.kinlhp.steve.resposta.Colecao;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 9/20/17.
 */
public final class FormaPagamentoRequisicao implements Serializable {
	private static final long serialVersionUID = 2195253635630104876L;
	private static final FormaPagamentoRecurso RECURSO = Requisicao
			.criar(FormaPagamentoRecurso.class);

	private FormaPagamentoRequisicao() {
	}

	public static void get(@NonNull Callback<Colecao<FormaPagamentoDTO>> callback) {
		RECURSO.get().enqueue(callback);
	}

//	public static void getCondicoesPagamento(@NonNull Callback<Colecao<CondicaoPagamentoDTO>> callback,
//	                                         @NonNull HRef condicoesPagamento) {
//		RECURSO.getCondicoesPagamento(condicoesPagamento.getHref()).enqueue(callback);
//	}

	public static void getPaginado(@NonNull Callback<Colecao<FormaPagamentoDTO>> callback,
	                               @NonNull HRef pagina) {
		RECURSO.getPaginado(pagina.getHref()).enqueue(callback);
	}

	public static void getPorId(@NonNull Callback<FormaPagamentoDTO> callback,
	                            @NonNull BigInteger id) {
		RECURSO.getPorId(id).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull FormaPagamentoDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id,
	                       @NonNull FormaPagamentoDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
