package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ContaReceberDTO;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
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
 * Created by kin on 9/28/17.
 */
public interface ContaReceberRecurso {

	@GET(value = "contasreceber?sort=id,asc")
	Call<Colecao<ContaReceberDTO>> get();

	@GET
	Call<PessoaDTO> getSacado(@NonNull @Url String href);

	@GET
	Call<Colecao<MovimentacaoContaReceberDTO>> getMovimentacoes(@NonNull @Url String href);

	@GET
	Call<Colecao<ContaReceberDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "contasreceber/{id}")
	Call<ContaReceberDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@PATCH(value = "contasreceber/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body ContaReceberDTO dto);

	@POST(value = "contasreceber")
	Call<Void> post(@NonNull @Body ContaReceberDTO dto);

	@PUT(value = "contasreceber/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body ContaReceberDTO dto);
}
