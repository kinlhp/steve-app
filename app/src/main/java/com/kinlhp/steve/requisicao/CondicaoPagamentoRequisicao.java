package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.recurso.CondicaoPagamentoRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 9/25/17.
 */
public class CondicaoPagamentoRequisicao implements Serializable {
	private static final long serialVersionUID = -5414691413787456964L;
	private static final CondicaoPagamentoRecurso RECURSO = Requisicao
			.criar(CondicaoPagamentoRecurso.class);

	private CondicaoPagamentoRequisicao() {
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull CondicaoPagamentoDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id,
	                       @NonNull CondicaoPagamentoDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
