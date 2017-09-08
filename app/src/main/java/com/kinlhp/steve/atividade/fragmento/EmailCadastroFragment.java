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
import com.kinlhp.steve.dominio.Email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class EmailCadastroFragment extends Fragment
		implements View.OnClickListener, Serializable {
	private static final long serialVersionUID = -8330461273752691738L;
	private static final String EMAIL = "email";
	ArrayList<Email.Tipo> mTipos =
			new ArrayList<>(Arrays.asList(Email.Tipo.values()));
	private Email mEmail;
	private OnEmailAddedListener mEmailAddedListener;
	private AdaptadorSpinner<Email.Tipo> mAdaptador;

	private TextInputEditText mInputEnderecoEletronico;
	private TextInputEditText mInputNomeContato;
	private TextInputLayout mLabelEnderecoEletronico;
	private AppCompatSpinner mSpinnerTipo;

	/**
	 * Construtor padrão é obrigatório
	 */
	public EmailCadastroFragment() {
	}

	public static EmailCadastroFragment newInstance(@NonNull Email email) {
		EmailCadastroFragment fragmento = new EmailCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(EMAIL, email);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnEmailAddedListener) {
			mEmailAddedListener = (OnEmailAddedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnEmailAddedListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					iterarFormulario();
					mEmailAddedListener.onEmailAdded(mEmail);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEmail = getArguments() != null
				? (Email) getArguments().getSerializable(EMAIL)
				: Email.builder().build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_email_cadastro, container, false);
		AppCompatButton mButtonAdicionar = view
				.findViewById(R.id.button_adicionar);
		mInputEnderecoEletronico = view
				.findViewById(R.id.input_endereco_eletronico);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mLabelEnderecoEletronico = view
				.findViewById(R.id.label_endereco_eletronico);
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
		mEmailAddedListener = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.email_cadastro_titulo);
	}

	private boolean isFormularioValido() {
		if (TextUtils.isEmpty(mInputEnderecoEletronico.getText())) {
			mLabelEnderecoEletronico.setError(getString(R.string.input_invalido));
			mInputEnderecoEletronico.requestFocus();
			return false;
		} else {
			mLabelEnderecoEletronico.setError(null);
			mLabelEnderecoEletronico.setErrorEnabled(false);
		}
		return true;
	}

	private void iterarFormulario() {
		mEmail.setTipo((Email.Tipo) mSpinnerTipo.getSelectedItem());
		mEmail.setEnderecoEletronico(mInputEnderecoEletronico.getText().toString());
		mEmail.setNomeContato(TextUtils.isEmpty(mInputNomeContato.getText())
				? null : mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Email email : mEmail.getPessoa().getEmails()) {
			if (!email.equals(mEmail)
					|| TextUtils.isEmpty(mEmail.getEnderecoEletronico())) {
				mAdaptador.remove(email.getTipo());
			}
		}
		mAdaptador.notifyDataSetChanged();
	}

	private void preencherFormulario() {
		mSpinnerTipo
				.setSelection(!TextUtils.isEmpty(mEmail.getEnderecoEletronico())
						? mAdaptador.getPosition(mEmail.getTipo()) : 0);
		mInputEnderecoEletronico.setText(mEmail.getEnderecoEletronico() == null
				? "" : mEmail.getEnderecoEletronico());
		mInputNomeContato.setText(mEmail.getNomeContato() == null
				? "" : mEmail.getNomeContato());
	}

	public interface OnEmailAddedListener {
		void onEmailAdded(Email email);
	}
}
