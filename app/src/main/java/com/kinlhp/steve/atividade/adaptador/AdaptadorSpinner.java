package com.kinlhp.steve.atividade.adaptador;

import android.content.Context;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kinlhp.steve.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 8/20/17.
 */
public class AdaptadorSpinner<T> extends ArrayAdapter<T>
		implements Serializable {
	private static final long serialVersionUID = 6911828571979155506L;

	public AdaptadorSpinner(@NonNull Context contexto, @NonNull List<T> itens) {
		super(contexto, 0, itens);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@NonNull
	@Override
	public View getView(int posicao, @Nullable View view,
	                    @NonNull ViewGroup parent) {
		T item = getItem(posicao);
		if (view == null) {
			view = LayoutInflater.from(getContext())
					.inflate(android.R.layout.simple_spinner_item, parent, false);
		}
		((TextView) view).setText(item.toString());
		/*
		Retrieve a dimensional for a particular resource ID.
		Unit conversions are based on the current DisplayMetrics associated with
		the resources.
		 */
		float fonte = getContext().getResources()
				.getDimension(R.dimen.input_textSize);
		float metrica = getContext().getResources().getDisplayMetrics().density;
		((TextView) view).setTextSize(Dimension.SP, fonte / metrica);
		return view;
	}
}
