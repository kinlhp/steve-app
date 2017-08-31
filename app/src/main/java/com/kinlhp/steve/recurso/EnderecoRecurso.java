package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EnderecoDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kin on 8/28/17.
 */
public interface EnderecoRecurso {

	@POST(value = "enderecos")
	Call<Void> post(@NonNull @Body EnderecoDTO dto);

	@PUT(value = "enderecos/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body EnderecoDTO dto);
}
