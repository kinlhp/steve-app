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
import java.util.List;

public class EmailCadastroFragment extends Fragment
		implements View.OnClickListener, Serializable {
	private static final long serialVersionUID = -5951104757888021298L;
	private static final String EMAIL = "email";
	private AdaptadorSpinner<Email.Tipo> mAdaptador;
	private Email mEmail;
	private OnEmailAddedListener mEmailAddedListener;
	private ArrayList<Email.Tipo> mTipos =
			new ArrayList<>(Arrays.asList(Email.Tipo.values()));

	private AppCompatButton mButtonAdicionar;
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
				} else {
					((ScrollView) getActivity().findViewById(R.id.fragment_email_cadastro))
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
		mEmail = getArguments() != null
				? (Email) getArguments().getSerializable(EMAIL)
				: Email.builder().build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_email_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputEnderecoEletronico = view
				.findViewById(R.id.input_endereco_eletronico);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mLabelEnderecoEletronico = view
				.findViewById(R.id.label_endereco_eletronico);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mAdaptador = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptador);

		mButtonAdicionar.setOnClickListener(this);
		mInputEnderecoEletronico
				.setOnFocusChangeListener(enderecoEletronicoFocusChangeListener());

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

	private View.OnFocusChangeListener enderecoEletronicoFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isEnderecoEletronicoValido()) {
						mLabelEnderecoEletronico.setError(null);
						mLabelEnderecoEletronico.setErrorEnabled(false);
					}
				}
			}
		};
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
		return true;
	}

	private boolean isFormularioValido() {
		return isEnderecoEletronicoValido();
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
		mSpinnerTipo.setSelection(mEmail.getTipo() == null
				? 0 : mAdaptador.getPosition(mEmail.getTipo()));
		mInputEnderecoEletronico.setText(mEmail.getEnderecoEletronico() == null
				? "" : mEmail.getEnderecoEletronico());
		mInputNomeContato.setText(mEmail.getNomeContato() == null
				? "" : mEmail.getNomeContato());
		if (mEmail.getPessoa().getEmails().contains(mEmail)) {
			mButtonAdicionar
					.setHint(R.string.email_cadastro_button_alterar_hint);
		}

		// TODO: 9/9/17 corrigir essa gambiarra [problema com equals e hashCode]
			/*
			essa gambiarra foi necessária pois a validação acima não funciona
			quando o Tipo foi alterado, gerando assim inconsistência no hint do
			mButtonAdicionar.
			 */
		List<Email> emails =
				new ArrayList<>(mEmail.getPessoa().getEmails());
		int indice = emails.indexOf(mEmail);
		if (indice > -1) {
			mButtonAdicionar
					.setHint(R.string.email_cadastro_button_alterar_hint);
		}
	}

	public interface OnEmailAddedListener {
		void onEmailAdded(Email email);
	}
}
