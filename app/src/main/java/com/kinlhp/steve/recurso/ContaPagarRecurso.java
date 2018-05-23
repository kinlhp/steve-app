package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ContaPagarDTO;
import com.kinlhp.steve.dto.MovimentacaoContaPagarDTO;
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
 * Created by kin on 10/10/17.
 */
public interface ContaPagarRecurso {

	@GET(value = "contaspagar?sort=id,asc")
	Call<Colecao<ContaPagarDTO>> get();

	@GET
	Call<PessoaDTO> getCedente(@NonNull @Url String href);

	@GET
	Call<Colecao<MovimentacaoContaPagarDTO>> getMovimentacoes(@NonNull @Url String href);

	@GET
	Call<Colecao<ContaPagarDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "contaspagar/{id}")
	Call<ContaPagarDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@PATCH(value = "contaspagar/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body ContaPagarDTO dto);

	@POST(value = "contaspagar")
	Call<Void> post(@NonNull @Body ContaPagarDTO dto);

	@PUT(value = "contaspagar/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body ContaPagarDTO dto);
}
