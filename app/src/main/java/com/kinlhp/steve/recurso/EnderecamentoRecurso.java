package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.EnderecamentoDTO;
import com.kinlhp.steve.dto.UfDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by kin on 9/9/17.
 */
public interface EnderecamentoRecurso {

	@GET
	Call<EnderecamentoDTO> getPorCep(@NonNull @Url String href);

	@GET
	Call<UfDTO> getUf(@NonNull @Url String href);
}
