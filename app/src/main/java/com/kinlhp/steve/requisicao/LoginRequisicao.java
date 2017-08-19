package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.LoginDTO;
import com.kinlhp.steve.recurso.LoginRecurso;

import java.io.Serializable;

import retrofit2.Callback;

/**
 * Created by kin on 8/16/17.
 */
public final class LoginRequisicao implements Serializable {
	private static final long serialVersionUID = 5764985144031175248L;
	private static final LoginRecurso RECURSO = Requisicao
			.criar(LoginRecurso.class);

	private LoginRequisicao() {
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull LoginDTO dto) {
		RECURSO.post(dto).enqueue(callback);
	}
}
