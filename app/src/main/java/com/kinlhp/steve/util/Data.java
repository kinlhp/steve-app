package com.kinlhp.steve.util;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kin on 8/23/17.
 */
public final class Data implements Serializable {
	private static final long serialVersionUID = 7180335929231166771L;
	private static final String DATA = "dd/MM/yyyy";
	private static final String DATA_TEMPO = "dd/MM/yyyy HH:mm:ss";
	private static final String TEMPO = "HH:mm:ss";

	public static Date deStringData(@NonNull String data)
			throws ParseException {
		return new SimpleDateFormat(DATA, Locale.getDefault()).parse(data);
	}

	/**
	 * @return {@link Date} representando a data atual em seu último milissegundo
	 */
	public static Date fimDoDia() {
		Calendar fimDoDia = Calendar.getInstance(Locale.getDefault());
		fimDoDia.set(Calendar.HOUR_OF_DAY, 23);
		fimDoDia.set(Calendar.MINUTE, 59);
		fimDoDia.set(Calendar.SECOND, 59);
		fimDoDia.set(Calendar.MILLISECOND, 999);
		return fimDoDia.getTime();
	}

	/**
	 * @return {@link Date} representando a data atual em seu primeiro milissegundo
	 */
	public static Date inicioDoDia() {
		Calendar inicioDoDia = Calendar.getInstance(Locale.getDefault());
		inicioDoDia.set(Calendar.HOUR_OF_DAY, 0);
		inicioDoDia.set(Calendar.MINUTE, 0);
		inicioDoDia.set(Calendar.SECOND, 0);
		inicioDoDia.set(Calendar.MILLISECOND, 0);
		return inicioDoDia.getTime();
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
