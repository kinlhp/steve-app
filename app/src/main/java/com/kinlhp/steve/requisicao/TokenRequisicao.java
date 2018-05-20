package com.kinlhp.steve.requisicao;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.TokenDTO;
import com.kinlhp.steve.recurso.TokenRecurso;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import retrofit2.Callback;

/**
 * Created by kin on 5/13/18.
 */
public final class TokenRequisicao implements Serializable {
	private static final long serialVersionUID = -1120329707678842490L;
	private static final TokenRecurso RECURSO = Requisicao
			.criar(TokenRecurso.class);

	private TokenRequisicao() {
	}

	public static void post(@NonNull Callback<TokenDTO> callback,
	                        @NonNull String password,
	                        @NonNull String username) {
		RECURSO.post(GrantType.PASSWORD.descricao, password, username)
				.enqueue(callback);
	}

	public static void post(@NonNull Callback<TokenDTO> callback,
	                        @NonNull String refreshToken) {
		RECURSO.post(GrantType.REFRESH_TOKEN.descricao, refreshToken)
				.enqueue(callback);
	}

	@AllArgsConstructor
	@Getter
	public enum GrantType {
		PASSWORD("password"),
		REFRESH_TOKEN("refresh_token"),
		REVOKE_TOKEN("revoke_token");

		private final String descricao;

		@Override
		public String toString() {
			return descricao;
		}
	}
}
