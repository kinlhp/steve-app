package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.util.Parametro;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kin on 8/10/17.
 */
public class Interceptador implements Interceptor, Serializable {
	private static final long serialVersionUID = -6343916940478176520L;

	@Override
	public Response intercept(@NonNull Chain encadeamento) throws IOException {
		String token = (String) Parametro.get(Parametro.Chave.TOKEN);
		Request requisicaoOriginal = encadeamento.request();
		Request.Builder requestBuilder = requisicaoOriginal.newBuilder()
				.header(Requisicao.AUTHORIZATION_HEADER, token);
		Request novaRequisicao = requestBuilder.build();
		return encadeamento.proceed(novaRequisicao);
	}
}
