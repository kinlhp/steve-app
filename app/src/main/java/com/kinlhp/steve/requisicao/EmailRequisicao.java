package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.recurso.EmailRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 8/28/17.
 */
public class EmailRequisicao implements Serializable {
	private static final long serialVersionUID = 4166492576796913655L;
	private static final EmailRecurso RECURSO = Requisicao
			.criar(EmailRecurso.class);

	private EmailRequisicao() {
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull EmailDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull EmailDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
