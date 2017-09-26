package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.FormaPagamentoDTO;
import com.kinlhp.steve.resposta.Colecao;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by kin on 9/20/17.
 */
public interface FormaPagamentoRecurso {

	@GET(value = "formaspagamento?sort=descricao,asc")
	Call<Colecao<FormaPagamentoDTO>> get();

	@GET
	Call<Colecao<CondicaoPagamentoDTO>> getCondicoesPagamento(@NonNull @Url String href);

	@GET
	Call<Colecao<FormaPagamentoDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "formaspagamento/{id}")
	Call<FormaPagamentoDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@PATCH(value = "formaspagamento/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body FormaPagamentoDTO dto);

	@POST(value = "formaspagamento")
	Call<Void> post(@NonNull @Body FormaPagamentoDTO dto);

	@PUT(value = "formaspagamento/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body FormaPagamentoDTO dto);
}
