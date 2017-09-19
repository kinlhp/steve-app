package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.CredencialRecurso;
import com.kinlhp.steve.resposta.Colecao;

import java.io.Serializable;
import java.math.BigInteger;

import okhttp3.RequestBody;
import retrofit2.Callback;

/**
 * Created by kin on 8/16/17.
 */
public final class CredencialRequisicao implements Serializable {
	private static final long serialVersionUID = 8172216809706123507L;
	private static final CredencialRecurso RECURSO = Requisicao
			.criar(CredencialRecurso.class);

	private CredencialRequisicao() {
	}

	public static void getFuncionario(@NonNull Callback<PessoaDTO> callback,
	                                  @NonNull HRef funcionario) {
		RECURSO.getFuncionario(funcionario.getHref()).enqueue(callback);
	}

	public static void getPaginado(@NonNull Callback<Colecao<CredencialDTO>> callback,
	                               @NonNull HRef pagina) {
		RECURSO.getPaginado(pagina.getHref()).enqueue(callback);
	}

	public static void getPorId(@NonNull Callback<CredencialDTO> callback,
	                            @NonNull BigInteger id) {
		RECURSO.getPorId(id).enqueue(callback);
	}

	public static void getPorUsuario(@NonNull Callback<CredencialDTO> callback,
	                                 @NonNull String usuario) {
		RECURSO.getPorUsuario(usuario).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull CredencialDTO dto) {
		// TODO: 9/18/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull CredencialDTO dto) {
		// TODO: 9/18/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}

	public static void putFuncionario(@NonNull Callback<Void> callback,
	                                  @NonNull BigInteger id,
	                                  @NonNull RequestBody uriList) {
		RECURSO.putFuncionario(id, uriList).enqueue(callback);
	}
}
