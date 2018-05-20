package com.kinlhp.steve.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.MGF1ParameterSpec;

/**
 * Created by luis on 8/11/17.
 */
public final class Criptografia implements Serializable {
	private static final long serialVersionUID = 9021400587803182354L;

	public static String sha512(@NonNull String valor)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		@SuppressLint("InlinedApi")
		MessageDigest digest = MessageDigest
				.getInstance(MGF1ParameterSpec.SHA512.getDigestAlgorithm());
		/*
//		Field requires API level 19
		byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
		*/
		byte[] bytes = digest.digest(valor.getBytes("UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(Integer.toString((b & 0xFF) + 0x100, 16)
					.substring(1));
		}
		return builder.toString();
	}

	public static String base64(@NonNull String valor)
			throws UnsupportedEncodingException {
		/*
//		Field requires API level 19
		byte[] bytes = valor.getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(bytes);
		*/
		byte[] bytes = valor.getBytes("UTF-8");
		return Base64.encodeToString(bytes, Base64.NO_WRAP);
	}
}
