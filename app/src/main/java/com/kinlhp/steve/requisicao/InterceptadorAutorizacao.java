package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kinlhp.steve.dto.TokenDTO;
import com.kinlhp.steve.util.Criptografia;
import com.kinlhp.steve.util.Parametro;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kin on 5/13/18.
 */
public class InterceptadorAutorizacao implements Interceptor, Serializable {
	private static final long serialVersionUID = 180169443921679736L;
	private static final String BASIC_PREFIX = "Basic ";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	public Response intercept(@NonNull Chain encadeamento) throws IOException {
		Request requisicaoOriginal = encadeamento.request();
		Request.Builder requestBuilder = requisicaoOriginal.newBuilder()
				.header(Requisicao.AUTHORIZATION_HEADER, definirCabecalhoAutorizacao(requisicaoOriginal));
		Request novaRequisicao = requestBuilder.build();
		return encadeamento.proceed(novaRequisicao);
	}

	private String definirCabecalhoAutorizacao(@NonNull Request requisicao)
			throws UnsupportedEncodingException {
		if (requisicao.url().toString().endsWith("/oauth/token")) {
			String nomeCliente = (String) Parametro
					.get(Parametro.Chave.NOME_CLIENTE);
			String senhaCliente = (String) Parametro
					.get(Parametro.Chave.SENHA_CLIENTE);
			return BASIC_PREFIX + Criptografia
					.base64(nomeCliente + ":" + senhaCliente);
		}
		TokenDTO token = (TokenDTO) Parametro.get(Parametro.Chave.TOKEN);
		if (token != null && !TextUtils.isEmpty(token.getAccessToken())) {
			return BEARER_PREFIX + token.getAccessToken();
		}
		return "";
	}
}
