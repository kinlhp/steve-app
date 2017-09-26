package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.dto.ServicoDTO;

import java.math.BigInteger;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by kin on 9/21/17.
 */
public interface ItemOrdemServicoRecurso {

	@GET
	Call<ServicoDTO> getServico(@NonNull @Url String href);

	@POST(value = "itensordemservico")
	Call<Void> post(@NonNull @Body ItemOrdemServicoDTO dto);

	@PUT(value = "itensordemservico/{id}")
	Call<Void> put(@NonNull @Path(value = "id") BigInteger id,
	               @NonNull @Body ItemOrdemServicoDTO dto);

	@PUT(value = "itensordemservico/{id}/servico")
	Call<Void> putServico(@NonNull @Path(value = "id") BigInteger id,
	                      @Body RequestBody urlList);
}
