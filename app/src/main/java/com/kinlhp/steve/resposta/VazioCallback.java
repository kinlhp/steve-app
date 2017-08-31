package com.kinlhp.steve.resposta;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kin on 8/25/17.
 */
public interface VazioCallback extends Callback<Void> {

	@Override
	void onFailure(@NonNull Call<Void> chamada, @NonNull Throwable causa);

	@Override
	void onResponse(@NonNull Call<Void> chamada,
	                @NonNull Response<Void> resposta);
}
