package com.kinlhp.steve.util;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by kin on 9/28/17.
 */
public final class Moeda implements Serializable {
	private static final long serialVersionUID = 4179502528895990196L;

	public static String comSifra(@NonNull BigDecimal valor) {
		/**
		 * O caracter ¤ (0xA4) é a representação do simbolo da moeda do país corrente
		 */
		DecimalFormat numeroFormatado = new DecimalFormat("¤ ###,###,##0.00##", new DecimalFormatSymbols(Locale.getDefault()));
		numeroFormatado.setParseBigDecimal(true);
		numeroFormatado.setDecimalSeparatorAlwaysShown(true);
		numeroFormatado.setMinimumFractionDigits(2);
		return numeroFormatado.format(valor);
	}
}
