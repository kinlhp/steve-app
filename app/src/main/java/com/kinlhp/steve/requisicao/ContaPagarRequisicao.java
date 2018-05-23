package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ContaPagarDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.ContaPagarRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 10/10/17.
 */
public final class ContaPagarRequisicao implements Serializable {
	private static final long serialVersionUID = -6292974246152147743L;
	private static final ContaPagarRecurso RECURSO = Requisicao
			.criar(ContaPagarRecurso.class);

	private ContaPagarRequisicao() {
	}

	public static void getCedente(@NonNull Callback<PessoaDTO> callback,
	                              @NonNull HRef cedente) {
		RECURSO.getCedente(cedente.getHref()).enqueue(callback);
	}

	public static void getPorId(@NonNull Callback<ContaPagarDTO> callback,
	                            @NonNull BigInteger id) {
		RECURSO.getPorId(id).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull ContaPagarDTO dto) {
		// TODO: 10/10/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}
}
