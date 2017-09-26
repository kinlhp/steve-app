package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.ItemOrdemServicoRecurso;

import java.io.Serializable;
import java.math.BigInteger;

import okhttp3.RequestBody;
import retrofit2.Callback;

/**
 * Created by kin on 9/21/17.
 */
public class ItemOrdemServicoRequisicao implements Serializable {
	private static final long serialVersionUID = -6477921392905248649L;
	private static final ItemOrdemServicoRecurso RECURSO = Requisicao.criar(ItemOrdemServicoRecurso.class);

	private ItemOrdemServicoRequisicao() {
	}

	public static void getServico(@NonNull Callback<ServicoDTO> callback,
	                              @NonNull HRef uf) {
		RECURSO.getServico(uf.getHref()).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull ItemOrdemServicoDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id,
	                       @NonNull ItemOrdemServicoDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}

	public static void putServico(@NonNull Callback<Void> callback,
	                              @NonNull BigInteger id,
	                              @NonNull RequestBody uriList) {
		RECURSO.putServico(id, uriList).enqueue(callback);
	}
}
