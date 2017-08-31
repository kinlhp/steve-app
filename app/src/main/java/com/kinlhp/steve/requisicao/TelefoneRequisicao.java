package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.recurso.TelefoneRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 8/28/17.
 */
public class TelefoneRequisicao implements Serializable {
	private static final long serialVersionUID = 9151029465060361896L;
	private static final TelefoneRecurso RECURSO = Requisicao
			.criar(TelefoneRecurso.class);

	private TelefoneRequisicao() {
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull TelefoneDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull TelefoneDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
