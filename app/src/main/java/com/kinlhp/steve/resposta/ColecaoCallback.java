package com.kinlhp.steve.resposta;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kin on 8/25/17.
 */
public interface ColecaoCallback<T extends Colecao> extends Callback<T> {

	@Override
	void onFailure(@NonNull Call<T> chamada, @NonNull Throwable causa);

	@Override
	void onResponse(@NonNull Call<T> chamada, @NonNull Response<T> resposta);
}
