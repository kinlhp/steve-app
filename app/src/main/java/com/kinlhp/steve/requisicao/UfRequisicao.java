package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.UfDTO;
import com.kinlhp.steve.recurso.UfRecurso;

import java.io.Serializable;

import retrofit2.Callback;

/**
 * Created by kin on 9/9/17.
 */
public final class UfRequisicao implements Serializable {
	private static final long serialVersionUID = 748666258738857443L;
	private static final UfRecurso RECURSO = Requisicao.criar(UfRecurso.class);

	private UfRequisicao() {
	}

	public static void getPorSigla(@NonNull Callback<UfDTO> callback,
	                               @NonNull UfDTO.SiglaDTO siglaDTO) {
		RECURSO.getPorSigla(siglaDTO).enqueue(callback);
	}
}
