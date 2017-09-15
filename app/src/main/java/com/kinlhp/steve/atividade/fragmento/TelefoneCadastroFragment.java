package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class TelefoneCadastroFragment extends Fragment
		implements View.OnClickListener, View.OnFocusChangeListener,
		Serializable {
	private static final long serialVersionUID = -5259170939884758017L;
	private static final String TELEFONE = "telefone";
	private AdaptadorSpinner<Telefone.Tipo> mAdaptadorTipos;
	private OnTelefoneAdicionadoListener mOnTelefoneAdicionadoListener;
	private Telefone mTelefone;
	private ArrayList<Telefone.Tipo> mTipos;

	private AppCompatButton mButtonAdicionar;
	private TextInputEditText mInputNomeContato;
	private TextInputEditText mInputNumero;
	private TextInputLayout mLabelNumero;
	private ScrollView mScrollTelefoneCadastro;
	private AppCompatSpinner mSpinnerTipo;

	/**
	 * Construtor padrão é obrigatório
	 */
	public TelefoneCadastroFragment() {
	}

	public static TelefoneCadastroFragment newInstance(@NonNull Telefone telefone) {
		TelefoneCadastroFragment fragmento = new TelefoneCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(TELEFONE, telefone);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					iterarFormulario();
					if (mOnTelefoneAdicionadoListener != null) {
						mOnTelefoneAdicionadoListener
								.onTelefoneAdicionado(view, mTelefone);
					}
					getActivity().onBackPressed();
				} else {
					mScrollTelefoneCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mTelefone = (Telefone) savedInstanceState.getSerializable(TELEFONE);
		} else if (getArguments() != null) {
			mTelefone = (Telefone) getArguments().getSerializable(TELEFONE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_telefone_cadastro, container, false);
		mScrollTelefoneCadastro = (ScrollView) view;
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mInputNumero = view.findViewById(R.id.input_numero);
		mLabelNumero = view.findViewById(R.id.label_numero);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mTipos = new ArrayList<>(Arrays.asList(Telefone.Tipo.values()));
		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mButtonAdicionar.setOnClickListener(this);
		mInputNumero.setOnFocusChangeListener(this);

		return view;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_numero:
				if (!focused) {
					isNumeroValido();
				}
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.telefone_cadastro_titulo);
		limitarTiposDisponiveis();
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		iterarFormulario();
		outState.putSerializable(TELEFONE, mTelefone);
	}

	private boolean isFormularioValido() {
		return isNumeroValido();
	}

	private boolean isNumeroValido() {
		if (TextUtils.isEmpty(mInputNumero.getText())) {
			mLabelNumero.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!TextUtils.isDigitsOnly(mInputNumero.getText())
				|| TextUtils.getTrimmedLength(mInputNumero.getText()) < 8) {
			mLabelNumero.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelNumero.setError(null);
		mLabelNumero.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mTelefone.setTipo((Telefone.Tipo) mSpinnerTipo.getSelectedItem());
		mTelefone.setNumero(mInputNumero.getText().toString());
		mTelefone.setNomeContato(mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Telefone telefone : mTelefone.getPessoa().getTelefones()) {
			if (!telefone.equals(mTelefone)
					|| TextUtils.isEmpty(mTelefone.getNumero())) {
				mAdaptadorTipos.remove(telefone.getTipo());
			}
		}
		mAdaptadorTipos.notifyDataSetChanged();
	}

	private void preencherFormulario() {
		mSpinnerTipo.setSelection(mTelefone.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mTelefone.getTipo()));
		mInputNumero.setText(mTelefone.getNumero() == null
				? "" : mTelefone.getNumero());
		mInputNomeContato.setText(mTelefone.getNomeContato() == null
				? "" : mTelefone.getNomeContato());
		if (mTelefone.getPessoa().getTelefones().contains(mTelefone)) {
			mButtonAdicionar
					.setHint(R.string.telefone_cadastro_button_alterar_hint);
		}
		mInputNumero.requestFocus();
	}

	public void setOnTelefoneAdicionadoListener(@Nullable OnTelefoneAdicionadoListener ouvinte) {
		mOnTelefoneAdicionadoListener = ouvinte;
	}

	public void setTelefone(@NonNull Telefone telefone) {
		mTelefone = telefone;
		if (getArguments() != null) {
			getArguments().putSerializable(TELEFONE, mTelefone);
		}
	}

	public interface OnTelefoneAdicionadoListener {

		void onTelefoneAdicionado(@NonNull View view,
		                          @NonNull Telefone telefone);
	}
}
