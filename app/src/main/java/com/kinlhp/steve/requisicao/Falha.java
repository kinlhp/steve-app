package com.kinlhp.steve.requisicao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.kinlhp.steve.R;
import com.kinlhp.steve.dto.ErroDTO;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by kin on 8/13/17.
 */
public final class Falha implements Serializable {
	private static final long serialVersionUID = -8324750503500699538L;

	private Falha() {
	}

	private static View.OnClickListener acaoClickListener(@NonNull final Context contexto,
	                                                      @NonNull final ErroDTO erro) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new AlertDialog.Builder(contexto)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage(erro.getMensagem())
						.show();
			}
		};
	}

	private static void exibirMensagem(@NonNull View view,
	                                   @NonNull String mensagem,
	                                   @Nullable ErroDTO erro) {
		final Context contexto = view.getContext();
		Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG);
		if (erro != null) {
			// TODO: 8/26/17 parametrizar tempo padrão
			snackbar.setDuration(5000);
			snackbar.setAction(contexto.getString(R.string.resposta_mensagem_label_acao), acaoClickListener(contexto, erro));
		}
		snackbar.show();
	}

	private static ErroDTO obterDTO(@Nullable ResponseBody corpo) {
		try {
			return new Gson().fromJson(corpo.string(), ErroDTO.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void tratar(@NonNull View view, @NonNull Response resposta) {
		Context contexto = view.getContext();
		String mensagem;
		ErroDTO erro = null;
		switch (resposta.code()) {
			case HttpURLConnection.HTTP_BAD_REQUEST:
				mensagem = contexto
						.getString(R.string.resposta_mensagem_bad_request);
				erro = obterDTO(resposta.errorBody());
				break;
			case HttpURLConnection.HTTP_CONFLICT:
				mensagem = contexto
						.getString(R.string.resposta_mensagem_conflict);
				erro = obterDTO(resposta.errorBody());
				break;
			case HttpURLConnection.HTTP_NOT_FOUND:
				mensagem = contexto
						.getString(R.string.resposta_mensagem_not_found);
				erro = obterDTO(resposta.errorBody());
				break;
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				mensagem = contexto
						.getString(R.string.resposta_mensagem_unauthorized);
				erro = obterDTO(resposta.errorBody());
				break;
			default:
				mensagem = contexto
						.getString(R.string.suporte_mensagem_nao_identificado);
		}
		exibirMensagem(view, mensagem, erro);
	}

	public static void tratar(@NonNull View view, @Nullable Throwable causa) {
		Context contexto = view.getContext();
		String mensagem = contexto.getString(R.string.suporte_mensagem_conexao);
		if (causa != null) {
			causa.printStackTrace();
			if (causa instanceof SocketTimeoutException) {
				mensagem = contexto
						.getString(R.string.resposta_mensagem_timeout);
			} else if (causa instanceof JsonParseException) {
				mensagem = contexto
						.getString(R.string.suporte_mensagem_desserializacao);
			} else {
				mensagem = causa.getMessage();
			}
		}
		exibirMensagem(view, mensagem, null);
	}
}
