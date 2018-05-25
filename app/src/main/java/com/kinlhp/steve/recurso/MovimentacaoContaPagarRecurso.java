package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.MovimentacaoContaPagarDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by kin on 5/22/18.
 */
public interface MovimentacaoContaPagarRecurso {

	@GET
	Call<CondicaoPagamentoDTO> getCondicaoPagamento(@NonNull @Url String href);

	@POST(value = "movimentacoesContaPagar")
	Call<Void> post(@NonNull @Body MovimentacaoContaPagarDTO dto);

	@PUT(value = "movimentacoesContaPagar/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body MovimentacaoContaPagarDTO dto);
}
