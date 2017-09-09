package com.kinlhp.steve.atividade.fragmento;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class TelefoneCadastroFragment extends Fragment
		implements View.OnClickListener, Serializable {
	private static final long serialVersionUID = -3027180351761693710L;
	private static final String TELEFONE = "telefone";
	ArrayList<Telefone.Tipo> mTipos =
			new ArrayList<>(Arrays.asList(Telefone.Tipo.values()));
	private Telefone mTelefone;
	private OnTelefoneAddedListener mTelefoneAddedListener;
	private AdaptadorSpinner<Telefone.Tipo> mAdaptador;

	private TextInputEditText mInputNomeContato;
	private TextInputEditText mInputNumero;
	private TextInputLayout mLabelNumero;
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
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnTelefoneAddedListener) {
			mTelefoneAddedListener = (OnTelefoneAddedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnTelefoneAddedListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					iterarFormulario();
					mTelefoneAddedListener.onTelefoneAdded(mTelefone);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTelefone = getArguments() != null
				? (Telefone) getArguments().getSerializable(TELEFONE)
				: Telefone.builder().build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_telefone_cadastro, container, false);
		AppCompatButton mButtonAdicionar = view
				.findViewById(R.id.button_adicionar);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mInputNumero = view.findViewById(R.id.input_numero);
		mLabelNumero = view.findViewById(R.id.label_numero);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mAdaptador = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptador);

		mButtonAdicionar.setOnClickListener(this);

		limitarTiposDisponiveis();
		preencherFormulario();
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mTelefoneAddedListener = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.telefone_cadastro_titulo);
	}

	private boolean isFormularioValido() {
		if (TextUtils.isEmpty(mInputNumero.getText())) {
			mLabelNumero.setError(getString(R.string.input_invalido));
			mInputNumero.requestFocus();
			return false;
		} else {
			mLabelNumero.setError(null);
			mLabelNumero.setErrorEnabled(false);
		}
		return true;
	}

	private void iterarFormulario() {
		mTelefone.setTipo((Telefone.Tipo) mSpinnerTipo.getSelectedItem());
		mTelefone.setNumero(mInputNumero.getText().toString());
		mTelefone.setNomeContato(TextUtils.isEmpty(mInputNomeContato.getText())
				? null : mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Telefone telefone : mTelefone.getPessoa().getTelefones()) {
			if (!telefone.equals(mTelefone)
					|| TextUtils.isEmpty(mTelefone.getNumero())) {
				mAdaptador.remove(telefone.getTipo());
			}
		}
		mAdaptador.notifyDataSetChanged();
	}

	private void preencherFormulario() {
		mSpinnerTipo
				.setSelection(!TextUtils.isEmpty(mTelefone.getNumero())
						? mAdaptador.getPosition(mTelefone.getTipo()) : 0);
		mInputNumero.setText(mTelefone.getNumero() == null
				? "" : mTelefone.getNumero());
		mInputNomeContato.setText(mTelefone.getNomeContato() == null
				? "" : mTelefone.getNomeContato());
	}

	public interface OnTelefoneAddedListener {
		void onTelefoneAdded(Telefone telefone);
	}
}
