package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.kinlhp.steve.dto.ErroDTO;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by kin on 8/13/17.
 */
public final class Falha implements Serializable {
	private static final long serialVersionUID = -8324750503500699538L;
	private static final String CONEXAO = "estabelecer conexão com servidor";
	private static final String DESSERIALIZACAO = "desserializar dados";
	private static final String INCONSISTENCIA = "identificar inconsistência";
	private static final String SUPORTE = "[suporte] Não foi possível %s";
	private static final String TEMPO_EXCEDIDO = "Tempo limite de conexão com servidor excedido";

	private Falha() {
	}

	public static void tratar(@NonNull View view, @NonNull Response resposta) {
		String mensagem = String.format(SUPORTE, INCONSISTENCIA);
		ResponseBody corpo = resposta.errorBody();
		if (corpo != null) {
			try {
				ErroDTO dto = new Gson()
						.fromJson(corpo.string(), ErroDTO.class);
				mensagem = dto.getMensagem();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG).show();
	}

	public static void tratar(@NonNull View view, @Nullable Throwable causa) {
		String mensagem = String.format(SUPORTE, CONEXAO);
		if (causa != null) {
			causa.printStackTrace();
			if (causa instanceof SocketTimeoutException) {
				mensagem = TEMPO_EXCEDIDO;
			} else if (causa instanceof JsonParseException) {
				mensagem = String.format(SUPORTE, DESSERIALIZACAO);
			}
		}
		Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG).show();
	}
}
