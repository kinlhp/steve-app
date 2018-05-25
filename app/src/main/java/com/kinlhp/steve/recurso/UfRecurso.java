package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.UfDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kin on 9/9/17.
 */
public interface UfRecurso {

	@GET(value = "ufs/search/sigla")
	Call<UfDTO> getPorSigla(@NonNull @Query(value = "sigla") UfDTO.SiglaDTO siglaDTO);
}
