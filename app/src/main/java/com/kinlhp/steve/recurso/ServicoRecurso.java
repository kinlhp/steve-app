package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ServicoDTO;
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
public interface ServicoRecurso {

	@GET(value = "servicos?sort=descricao,asc&page=0&size=20")
	Call<Colecao<ServicoDTO>> get();

	@GET
	Call<Colecao<ServicoDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "servicos/{id}")
	Call<ServicoDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@PATCH(value = "servicos/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body ServicoDTO dto);

	@POST(value = "servicos")
	Call<Void> post(@NonNull @Body ServicoDTO dto);

	@PUT(value = "servicos/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body ServicoDTO dto);
}
