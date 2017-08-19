package com.kinlhp.steve.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.Serializable;

/**
 * Created by kin on 8/16/17.
 */
public final class Teclado implements Serializable {
	private static final long serialVersionUID = -973871103267696057L;

	public static void ocultar(@NonNull Context contexto, @NonNull View view) {
		InputMethodManager manager = (InputMethodManager) contexto
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
