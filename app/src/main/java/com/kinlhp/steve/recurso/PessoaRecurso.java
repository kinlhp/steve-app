package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.resposta.Resposta;

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
 * Created by kin on 8/13/17.
 */
public interface PessoaRecurso {

	@GET
	Call<Resposta<EmailDTO>> getEmails(@NonNull @Url String href);

	@GET
	Call<Resposta<EnderecoDTO>> getEnderecos(@NonNull @Url String href);

	@GET(value = "pessoas/{id}")
	Call<PessoaDTO> getPorId(@NonNull @Path(value = "id") BigInteger id);

	@GET
	Call<Resposta<TelefoneDTO>> getTelefones(@NonNull @Url String href);

	@PATCH(value = "pessoas/{id}")
	Call<Void> patch(@NonNull @Path(value = "id") BigInteger id,
	                 @NonNull @Body PessoaDTO dto);

	@POST(value = "pessoas")
	Call<Void> post(@NonNull @Body PessoaDTO dto);

	@PUT(value = "pessoas/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body PessoaDTO dto);
}
