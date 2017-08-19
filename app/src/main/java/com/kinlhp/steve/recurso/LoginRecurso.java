package com.kinlhp.steve.recurso;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dto.LoginDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by kin on 8/13/17.
 */
public interface LoginRecurso {

	@POST(value = "login")
	Call<Void> post(@NonNull @Body LoginDTO dto);
}
