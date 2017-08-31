package com.kinlhp.steve.util;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kin on 8/23/17.
 */
public final class Data implements Serializable {
	private static final long serialVersionUID = -1168466286798549561L;
	private static final String DATA = "dd/MM/yyyy";
	private static final String DATA_TEMPO = "dd/MM/yyyy HH:mm:ss";
	private static final String TEMPO = "HH:mm:ss";

	public static Date deStringData(@NonNull String data)
			throws ParseException {
		return new SimpleDateFormat(DATA, Locale.getDefault()).parse(data);
	}

	public static String paraStringData(@NonNull Date data) {
		return new SimpleDateFormat(DATA, Locale.getDefault()).format(data);
	}

	public static String paraStringDataTempo(@NonNull Date data) {
		return new SimpleDateFormat(DATA_TEMPO, Locale.getDefault())
				.format(data);
	}

	public static String paraStringTempo(@NonNull Date data) {
		return new SimpleDateFormat(TEMPO, Locale.getDefault()).format(data);
	}
}
