package com.kinlhp.steve.util;

import android.annotation.SuppressLint;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by luis on 8/11/17.
 */
public final class Criptografia implements Serializable {
	private static final long serialVersionUID = 1609723231570052768L;

	public static String sha512(@NonNull String valor)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		@SuppressLint("InlinedApi")
		MessageDigest digest = MessageDigest
				.getInstance(KeyProperties.DIGEST_SHA512);
		byte[] bytes = digest.digest(valor.getBytes("UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(Integer.toString((b & 0xFF) + 0x100, 16)
					.substring(1));
		}
		return builder.toString();
	}
}
