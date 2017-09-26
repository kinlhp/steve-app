package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CondicaoPagamentoDTO;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kin on 9/25/17.
 */
public interface CondicaoPagamentoRecurso {

	@POST(value = "condicoespagamento")
	Call<Void> post(@NonNull @Body CondicaoPagamentoDTO dto);

	@PUT(value = "condicoespagamento/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body CondicaoPagamentoDTO dto);
}
