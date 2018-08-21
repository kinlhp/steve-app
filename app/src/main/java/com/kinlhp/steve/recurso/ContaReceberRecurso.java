package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ContaReceberDTO;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
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
 * Created by kin on 9/28/17.
 */
public interface ContaReceberRecurso {

	@PUT(value = "contasReceber/{id}/estorno")
	Call<Void> estorno(@NonNull @Path(value = "id") BigInteger id);

	@GET(value = "contasReceber?sort=contaReceber,asc&sort=numeroParcela,asc&page=0&size=20")
	Call<Colecao<ContaReceberDTO>> get();

	@GET
	Call<Colecao<MovimentacaoContaReceberDTO>> getMovimentacoes(@NonNull @Url String href);

	@GET
	Call<OrdemDTO> getOrdem(@NonNull @Url String href);

	@GET
	Call<Colecao<ContaReceberDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "contasReceber/{id}")
	Call<ContaReceberDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@GET
	Call<PessoaDTO> getSacado(@NonNull @Url String href);

	@PATCH(value = "contasReceber/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body ContaReceberDTO dto);

	@POST(value = "contasReceber")
	Call<Void> post(@NonNull @Body ContaReceberDTO dto);

	@PUT(value = "contasReceber/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body ContaReceberDTO dto);
}
