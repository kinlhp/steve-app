package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.PessoaRecurso;
import com.kinlhp.steve.resposta.Resposta;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 8/16/17.
 */
public final class PessoaRequisicao implements Serializable {
	private static final long serialVersionUID = -790844688057871488L;
	private static final PessoaRecurso RECURSO = Requisicao
			.criar(PessoaRecurso.class);

	private PessoaRequisicao() {
	}

	public static void getEmails(@NonNull Callback<Resposta<EmailDTO>> callback,
	                             @NonNull HRef emails) {
		RECURSO.getEmails(emails.getHref()).enqueue(callback);
	}

	public static void getEnderecos(@NonNull Callback<Resposta<EnderecoDTO>> callback,
	                                @NonNull HRef enderecos) {
		RECURSO.getEnderecos(enderecos.getHref()).enqueue(callback);
	}

	public static void getPorId(@NonNull Callback<PessoaDTO> callback,
	                            @NonNull BigInteger id) {
		RECURSO.getPorId(id).enqueue(callback);
	}

	public static void getTelefones(@NonNull Callback<Resposta<TelefoneDTO>> callback,
	                                @NonNull HRef telefones) {
		RECURSO.getTelefones(telefones.getHref()).enqueue(callback);
	}

	public static void patch(@NonNull Callback<Void> callback,
	                         @NonNull BigInteger id, @NonNull PessoaDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.patch(id, dto).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull PessoaDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull PessoaDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
