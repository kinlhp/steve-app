package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.recurso.EnderecoRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 8/28/17.
 */
public class EnderecoRequisicao implements Serializable {
	private static final long serialVersionUID = -3881808115857261187L;
	private static final EnderecoRecurso RECURSO = Requisicao
			.criar(EnderecoRecurso.class);

	private EnderecoRequisicao() {
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull EnderecoDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull EnderecoDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
