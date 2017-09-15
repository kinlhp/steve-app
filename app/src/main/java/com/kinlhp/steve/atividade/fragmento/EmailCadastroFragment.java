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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class EmailCadastroFragment extends Fragment
		implements View.OnClickListener, View.OnFocusChangeListener,
		Serializable {
	private static final long serialVersionUID = 2077237610120314776L;
	private static final String EMAIL = "email";
	private AdaptadorSpinner<Email.Tipo> mAdaptadorTipos;
	private Email mEmail;
	private OnEmailAdicionadoListener mOnEmailAdicionadoListener;
	private ArrayList<Email.Tipo> mTipos;

	private AppCompatButton mButtonAdicionar;
	private TextInputEditText mInputEnderecoEletronico;
	private TextInputEditText mInputNomeContato;
	private TextInputLayout mLabelEnderecoEletronico;
	private ScrollView mScrollEmailCadastro;
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
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					iterarFormulario();
					if (mOnEmailAdicionadoListener != null) {
						mOnEmailAdicionadoListener
								.onEmailAdicionado(view, mEmail);
					}
					getActivity().onBackPressed();
				} else {
					mScrollEmailCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mEmail = (Email) savedInstanceState.getSerializable(EMAIL);
		} else if (getArguments() != null) {
			mEmail = (Email) getArguments().getSerializable(EMAIL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_email_cadastro, container, false);
		mScrollEmailCadastro = (ScrollView) view;
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputEnderecoEletronico = view
				.findViewById(R.id.input_endereco_eletronico);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mLabelEnderecoEletronico = view
				.findViewById(R.id.label_endereco_eletronico);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mTipos = new ArrayList<>(Arrays.asList(Email.Tipo.values()));
		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mButtonAdicionar.setOnClickListener(this);
		mInputEnderecoEletronico.setOnFocusChangeListener(this);

		return view;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_endereco_eletronico:
				if (!focused) {
					isEnderecoEletronicoValido();
				}
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.email_cadastro_titulo);
		limitarTiposDisponiveis();
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		iterarFormulario();
		outState.putSerializable(EMAIL, mEmail);
	}

	private boolean isEnderecoEletronicoValido() {
		if (TextUtils.isEmpty(mInputEnderecoEletronico.getText())) {
			mLabelEnderecoEletronico
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!Patterns.EMAIL_ADDRESS.matcher(mInputEnderecoEletronico.getText()).matches()) {
			mLabelEnderecoEletronico
					.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelEnderecoEletronico.setError(null);
		mLabelEnderecoEletronico.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isEnderecoEletronicoValido();
	}

	private void iterarFormulario() {
		mEmail.setTipo((Email.Tipo) mSpinnerTipo.getSelectedItem());
		mEmail.setEnderecoEletronico(mInputEnderecoEletronico.getText().toString());
		mEmail.setNomeContato(mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Email email : mEmail.getPessoa().getEmails()) {
			if (!email.equals(mEmail)
					|| TextUtils.isEmpty(mEmail.getEnderecoEletronico())) {
				mAdaptadorTipos.remove(email.getTipo());
			}
		}
		mAdaptadorTipos.notifyDataSetChanged();
	}

	private void preencherFormulario() {
		mSpinnerTipo.setSelection(mEmail.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mEmail.getTipo()));
		mInputEnderecoEletronico.setText(mEmail.getEnderecoEletronico() == null
				? "" : mEmail.getEnderecoEletronico());
		mInputNomeContato.setText(mEmail.getNomeContato() == null
				? "" : mEmail.getNomeContato());
		if (mEmail.getPessoa().getEmails().contains(mEmail)) {
			mButtonAdicionar
					.setHint(R.string.email_cadastro_button_alterar_hint);
		}
		mInputEnderecoEletronico.requestFocus();
	}

	public void setEmail(@NonNull Email email) {
		mEmail = email;
		if (getArguments() != null) {
			getArguments().putSerializable(EMAIL, mEmail);
		}
	}

	public void setOnEmailAdicionadoListener(@Nullable OnEmailAdicionadoListener ouvinte) {
		mOnEmailAdicionadoListener = ouvinte;
	}

	public interface OnEmailAdicionadoListener {

		void onEmailAdicionado(@NonNull View view, @NonNull Email email);
	}
}
