package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.ServicoRecurso;
import com.kinlhp.steve.resposta.Colecao;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 9/20/17.
 */
public final class ServicoRequisicao implements Serializable {
	private static final long serialVersionUID = -1124803413083930820L;
	private static final ServicoRecurso RECURSO = Requisicao
			.criar(ServicoRecurso.class);

	private ServicoRequisicao() {
	}

	public static void get(@NonNull Callback<Colecao<ServicoDTO>> callback) {
		RECURSO.get().enqueue(callback);
	}

	public static void getPaginado(@NonNull Callback<Colecao<ServicoDTO>> callback,
	                               @NonNull HRef pagina) {
		RECURSO.getPaginado(pagina.getHref()).enqueue(callback);
	}

	public static void getPorId(@NonNull Callback<ServicoDTO> callback,
	                            @NonNull BigInteger id) {
		RECURSO.getPorId(id).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull ServicoDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull ServicoDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
