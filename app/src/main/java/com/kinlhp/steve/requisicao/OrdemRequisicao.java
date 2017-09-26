package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.dto.OrdemDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.OrdemRecurso;
import com.kinlhp.steve.resposta.Colecao;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 9/21/17.
 */
public final class OrdemRequisicao implements Serializable {
	private static final long serialVersionUID = 2804764182738067288L;
	private static final OrdemRecurso RECURSO = Requisicao.criar(OrdemRecurso.class);

	private OrdemRequisicao() {
	}

	public static void get(@NonNull Callback<Colecao<OrdemDTO>> callback) {
		RECURSO.get().enqueue(callback);
	}

	public static void getCliente(@NonNull Callback<PessoaDTO> callback,
	                              @NonNull HRef cliente) {
		RECURSO.getCliente(cliente.getHref()).enqueue(callback);
	}

	public static void getItensOrdemServico(@NonNull Callback<Colecao<ItemOrdemServicoDTO>> callback,
	                                        @NonNull HRef itensOrdemServico) {
		RECURSO.getItensOrdemServico(itensOrdemServico.getHref()).enqueue(callback);
	}

	public static void getPaginado(@NonNull Callback<Colecao<OrdemDTO>> callback,
	                               @NonNull HRef pagina) {
		RECURSO.getPaginado(pagina.getHref()).enqueue(callback);
	}

	public static void getPorId(@NonNull Callback<OrdemDTO> callback,
	                            @NonNull BigInteger id) {
		RECURSO.getPorId(id).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull OrdemDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id, @NonNull OrdemDTO dto) {
		// TODO: 8/25/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
