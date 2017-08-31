package com.kinlhp.steve.componente;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import com.kinlhp.steve.util.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kin on 8/23/17.
 */
public class DialogoCalendario extends DialogFragment
		implements DatePickerDialog.OnDateSetListener, Serializable {
	private static final long serialVersionUID = -4066864071609094930L;
	private static final String ANO = "ANO";
	private static final String DIA = "DIA";
	private static final String MES = "MES";
	private static final String ID_VIEW = "VIEW";

	public static DialogoCalendario newInstance(@IdRes int viewId, int ano,
	                                            int mes, int dia) {
		DialogoCalendario dialogo = new DialogoCalendario();
		Bundle argumentos = new Bundle();
		argumentos.putInt(ANO, ano);
		argumentos.putInt(DIA, dia);
		argumentos.putInt(MES, mes);
		argumentos.putInt(ID_VIEW, viewId);
		dialogo.setArguments(argumentos);
		return dialogo;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int ano = 0;
		int dia = 0;
		int mes = 0;
		Bundle bundle = getArguments();
		if (bundle != null) {
			ano = bundle.getInt(ANO);
			dia = bundle.getInt(DIA);
			mes = bundle.getInt(MES);
		}
		if (ano == 0 || mes < 0 || dia == 0) {
			Calendar calendario = Calendar.getInstance(Locale.getDefault());
			ano = calendario.get(Calendar.YEAR);
			mes = calendario.get(Calendar.MONTH);
			dia = calendario.get(Calendar.DAY_OF_MONTH);
		}
		return new DatePickerDialog(getActivity(), this, ano, mes, dia);
	}

	@Override
	public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
		int idView = getArguments().getInt(ID_VIEW);
		if (idView != 0) {
			EditText view;
			try {
				view = getActivity().findViewById(idView);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			Calendar calendario = Calendar.getInstance(Locale.getDefault());
			calendario.set(ano, mes, dia);
			view.setText(Data.paraStringData(calendario.getTime()));
		}
	}
}
