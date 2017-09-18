package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EnderecamentoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.EnderecamentoRecurso;

import java.io.Serializable;

import retrofit2.Callback;

/**
 * Created by kin on 9/9/17.
 */
public final class EnderecamentoRequisicao implements Serializable {
	private static final long serialVersionUID = 8353870877177203589L;
	private static final EnderecamentoRecurso RECURSO = Requisicao
			.criar(EnderecamentoRecurso.class);

	private EnderecamentoRequisicao() {
	}

	public static void getPorCep(@NonNull Callback<EnderecamentoDTO> callback,
	                             @NonNull HRef href) {
		RECURSO.getPorCep(href.getHref()).enqueue(callback);
	}
}
