package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.TelefoneDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kin on 8/28/17.
 */
public interface TelefoneRecurso {

	@POST(value = "telefones")
	Call<Void> post(@NonNull @Body TelefoneDTO dto);

	@PUT(value = "telefones/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body TelefoneDTO dto);
}
