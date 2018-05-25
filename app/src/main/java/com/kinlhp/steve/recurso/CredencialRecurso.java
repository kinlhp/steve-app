package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.resposta.Colecao;

import java.math.BigInteger;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by kin on 8/13/17.
 */
public interface CredencialRecurso {

	@GET
	Call<PessoaDTO> getFuncionario(@NonNull @Url String href);

	@GET
	Call<Colecao<CredencialDTO>> getPaginado(@NonNull @Url String href);

	@GET(value = "credenciais/{id}")
	Call<CredencialDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@GET(value = "credenciais/search/usuario")
	Call<CredencialDTO> getPorUsuario(@NonNull @Query(value = "usuario") String usuario);

	@POST(value = "credenciais")
	Call<Void> post(@NonNull @Body CredencialDTO dto);

	@PUT(value = "credenciais/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body CredencialDTO dto);

	@PUT(value = "credenciais/{id}/funcionario")
	Call<Void> putFuncionario(@NonNull @Path(value = "id") BigInteger id,
	                          @Body RequestBody urlList);
}
