package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ContaReceberDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kin on 9/28/17.
 */
public interface ContaReceberRecurso {

	@POST(value = "contasreceber")
	Call<Void> post(@NonNull @Body ContaReceberDTO dto);

	@PUT(value = "contasreceber/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body ContaReceberDTO dto);
}
