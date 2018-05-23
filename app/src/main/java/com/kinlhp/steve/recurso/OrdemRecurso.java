package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.dto.OrdemDTO;
import com.kinlhp.steve.dto.PessoaDTO;
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
 * Created by kin on 9/21/17.
 */
public interface OrdemRecurso {

	@GET(value = "ordens?sort=id,asc")
	Call<Colecao<OrdemDTO>> get();

	@GET
	Call<PessoaDTO> getCliente(@NonNull @Url String href);

	@GET
	Call<Colecao<ItemOrdemServicoDTO>> getItens(@NonNull @Url String href);

	@GET
	Call<Colecao<OrdemDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "ordens/{id}")
	Call<OrdemDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@PATCH(value = "ordens/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body OrdemDTO dto);

	@POST(value = "ordens")
	Call<Void> post(@NonNull @Body OrdemDTO dto);

	@PUT(value = "ordens/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body OrdemDTO dto);
}
