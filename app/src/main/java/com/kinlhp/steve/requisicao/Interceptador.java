package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by luis on 8/10/17.
 */
public class Interceptador implements Interceptor, Serializable {
	private static final long serialVersionUID = -4338355350496357777L;
	private final String token;

	Interceptador(@NonNull String token) {
		this.token = token;
	}

	@Override
	public Response intercept(@NonNull Chain encadeamento) throws IOException {
		Request requisicaoOriginal = encadeamento.request();
		Request.Builder requestBuilder = requisicaoOriginal.newBuilder()
				.header(Requisicao.AUTHORIZATION_HEADER, token);
		Request novaRequisicao = requestBuilder.build();
		return encadeamento.proceed(novaRequisicao);
	}
}
