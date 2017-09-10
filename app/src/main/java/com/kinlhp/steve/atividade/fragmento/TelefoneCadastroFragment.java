package com.kinlhp.steve.atividade.fragmento;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import java.util.List;

public class TelefoneCadastroFragment extends Fragment
		implements View.OnClickListener, Serializable {
	private static final long serialVersionUID = 5036312639921542373L;
	private static final String TELEFONE = "telefone";
	ArrayList<Telefone.Tipo> mTipos =
			new ArrayList<>(Arrays.asList(Telefone.Tipo.values()));
	private AdaptadorSpinner<Telefone.Tipo> mAdaptador;
	private Telefone mTelefone;
	private OnTelefoneAddedListener mTelefoneAddedListener;

	private AppCompatButton mButtonAdicionar;
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
				} else {
					((ScrollView) getActivity().findViewById(R.id.fragment_telefone_cadastro))
							.fullScroll(View.FOCUS_UP);
					Snackbar.make(mButtonAdicionar, getString(R.string.form_mensagem_invalido), Snackbar.LENGTH_LONG)
							.show();
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
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mInputNumero = view.findViewById(R.id.input_numero);
		mLabelNumero = view.findViewById(R.id.label_numero);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mAdaptador = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptador);

		mButtonAdicionar.setOnClickListener(this);
		mInputNumero.setOnFocusChangeListener(numeroFocusChangeListener());

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

	private View.OnFocusChangeListener numeroFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isNumeroValido()) {
						mLabelNumero.setError(null);
						mLabelNumero.setErrorEnabled(false);
					}
				}
			}
		};
	}

	private void preencherFormulario() {
		mSpinnerTipo.setSelection(mTelefone.getTipo() == null
				? 0 : mAdaptador.getPosition(mTelefone.getTipo()));
		mInputNumero.setText(mTelefone.getNumero() == null
				? "" : mTelefone.getNumero());
		mInputNomeContato.setText(mTelefone.getNomeContato() == null
				? "" : mTelefone.getNomeContato());
		if (mTelefone.getPessoa().getTelefones().contains(mTelefone)) {
			mButtonAdicionar
					.setHint(R.string.telefone_cadastro_button_alterar_hint);
		}

		// TODO: 9/9/17 corrigir essa gambiarra [problema com equals e hashCode]
			/*
			essa gambiarra foi necessária pois a validação acima não funciona
			quando o Tipo foi alterado, gerando assim inconsistência no hint do
			mButtonAdicionar.
			 */
		List<Telefone> telefones =
				new ArrayList<>(mTelefone.getPessoa().getTelefones());
		int indice = telefones.indexOf(mTelefone);
		if (indice > -1) {
			mButtonAdicionar
					.setHint(R.string.telefone_cadastro_button_alterar_hint);
		}
	}

	public interface OnTelefoneAddedListener {
		void onTelefoneAdded(Telefone telefone);
	}
}
