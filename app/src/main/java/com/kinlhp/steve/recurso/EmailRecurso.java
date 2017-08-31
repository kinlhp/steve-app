package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EmailDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kin on 8/28/17.
 */
public interface EmailRecurso {

	@POST(value = "emails")
	Call<Void> post(@NonNull @Body EmailDTO dto);

	@PUT(value = "emails/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body EmailDTO dto);
}
