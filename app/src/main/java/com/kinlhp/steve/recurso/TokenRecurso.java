package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.TokenDTO;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by kin on 5/12/18.
 */
public interface TokenRecurso {

	@FormUrlEncoded
	@POST(value = "oauth/token")
	Call<TokenDTO> post(@NonNull @Field(value = "grant_type") String grantType,
	                    @NonNull @Header(value = "refresh_token") String refreshToken);

	@FormUrlEncoded
	@POST(value = "oauth/token")
	Call<TokenDTO> post(@NonNull @Field(value = "grant_type") String grantType,
	                    @NonNull @Field(value = "password") String password,
	                    @NonNull @Field(value = "username") String username);
}
