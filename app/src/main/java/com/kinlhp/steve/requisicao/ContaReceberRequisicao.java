package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ContaReceberDTO;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
import com.kinlhp.steve.dto.OrdemDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.recurso.ContaReceberRecurso;
import com.kinlhp.steve.resposta.Colecao;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Callback;

/**
 * Created by kin on 9/28/17.
 */
public class ContaReceberRequisicao implements Serializable {
	private static final long serialVersionUID = 3256616368321599660L;
	private static final ContaReceberRecurso RECURSO = Requisicao
			.criar(ContaReceberRecurso.class);

	private ContaReceberRequisicao() {
	}

	public static void getMovimentacoes(@NonNull Callback<Colecao<MovimentacaoContaReceberDTO>> callback,
	                                    @NonNull HRef movimentacoes) {
		RECURSO.getMovimentacoes(movimentacoes.getHref()).enqueue(callback);
	}

	public static void getOrdem(@NonNull Callback<OrdemDTO> callback,
	                            @NonNull HRef ordem) {
		RECURSO.getOrdem(ordem.getHref()).enqueue(callback);
	}

	public static void getPaginado(@NonNull Callback<Colecao<ContaReceberDTO>> callback,
	                               @NonNull HRef pagina) {
		RECURSO.getPaginado(pagina.getHref()).enqueue(callback);
	}

	public static void getSacado(@NonNull Callback<PessoaDTO> callback,
	                             @NonNull HRef sacado) {
		RECURSO.getSacado(sacado.getHref()).enqueue(callback);
	}

	public static void post(@NonNull Callback<Void> callback,
	                        @NonNull ContaReceberDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.post(dto).enqueue(callback);
	}

	public static void put(@NonNull Callback<Void> callback,
	                       @NonNull BigInteger id,
	                       @NonNull ContaReceberDTO dto) {
		// TODO: 8/28/17 mapear aqui e não na activity
		RECURSO.put(id, dto).enqueue(callback);
	}
}
