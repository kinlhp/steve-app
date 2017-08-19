package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.PessoaDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by kin on 8/13/17.
 */
public interface CredencialRecurso {

	@GET
	Call<PessoaDTO> getFuncionario(@NonNull @Url String href);

	@GET(value = "credenciais/search/findByUsuario")
	Call<CredencialDTO> getPorUsuario(@NonNull @Query(value = "usuario") String usuario);
}
