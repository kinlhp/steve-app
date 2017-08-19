package com.kinlhp.steve.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kin on 8/15/17.
 */
public final class Parametro implements Serializable {
	private static final long serialVersionUID = -6250421223394267895L;
	private static final Map<Chave, Object> parametros = new HashMap<>();

	public static Object get(@NonNull Chave chave) {
		return parametros.get(chave);
	}

	public static void put(@NonNull Chave chave, @Nullable Object valor) {
		parametros.put(chave, valor);
	}

	public enum Chave {
		CREDENCIAL,
		TOKEN
	}
}
