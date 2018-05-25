package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by kin on 5/21/18.
 */
public interface MovimentacaoContaReceberRecurso {

	@GET
	Call<CondicaoPagamentoDTO> getCondicaoPagamento(@NonNull @Url String href);

	@POST(value = "movimentacoesContaReceber")
	Call<Void> post(@NonNull @Body MovimentacaoContaReceberDTO dto);

	@PUT(value = "movimentacoesContaReceber/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body MovimentacaoContaReceberDTO dto);
}
