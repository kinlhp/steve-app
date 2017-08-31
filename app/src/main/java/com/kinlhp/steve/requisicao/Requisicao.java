package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by luis on 8/10/17.
 */
public abstract class Requisicao implements Serializable {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String LOCATION_HEADER = "Location";
	private static final long serialVersionUID = 5161316211256030915L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
	private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
			.baseUrl(URL_BASE)
			.addConverterFactory(GsonConverterFactory.create(gson));
	private static Retrofit retrofit = retrofitBuilder.build();
	private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient
			.Builder();
	private static Interceptor autorizacao;
	// TODO: 8/13/17 desabilitar logging em ambiente de produção
	private static Interceptor logging = new HttpLoggingInterceptor()
			.setLevel(HttpLoggingInterceptor.Level.BODY);

	static <T> T criar(@NonNull Class<T> requisicao) {
		String token = (String) Parametro.get(Parametro.Chave.TOKEN);
		return criar(requisicao, token);
	}

	private static <T> T criar(@NonNull Class<T> requisicao,
	                           @Nullable String token) {
		if (token != null && autorizacao == null) {
			autorizacao = new Interceptador(token);
			if (!httpClientBuilder.interceptors().contains(autorizacao)) {
				httpClientBuilder.addInterceptor(autorizacao);
				retrofitBuilder.client(httpClientBuilder.build());
				retrofit = retrofitBuilder.build();
			}
		}
		if (!httpClientBuilder.interceptors().contains(logging)) {
			httpClientBuilder.addInterceptor(logging);
			retrofitBuilder.client(httpClientBuilder.build());
			retrofit = retrofitBuilder.build();
		}
		return retrofit.create(requisicao);
	}
}
