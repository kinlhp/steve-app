package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.CredencialRecurso;

import java.io.Serializable;

import retrofit2.Callback;

/**
 * Created by kin on 8/16/17.
 */
public final class CredencialRequisicao implements Serializable {
	private static final long serialVersionUID = -493610235266452079L;
	private static final CredencialRecurso RECURSO = Requisicao
			.criar(CredencialRecurso.class);

	private CredencialRequisicao() {
	}

	public static void getFuncionario(@NonNull Callback<PessoaDTO> callback,
	                                  @NonNull HRef funcionario) {
		RECURSO.getFuncionario(funcionario.getHref()).enqueue(callback);
	}

	public static void getPorUsuario(@NonNull Callback<CredencialDTO> callback,
	                                 @NonNull String usuario) {
		RECURSO.getPorUsuario(usuario).enqueue(callback);
	}
}
