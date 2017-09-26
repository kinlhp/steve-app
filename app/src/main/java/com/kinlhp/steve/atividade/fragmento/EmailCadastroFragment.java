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
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.mapeamento.EmailMapeamento;
import com.kinlhp.steve.requisicao.EmailRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EmailCadastroFragment extends Fragment
		implements View.OnClickListener, View.OnFocusChangeListener,
		Serializable {
	private static final long serialVersionUID = -7069977812418503931L;
	private static final String EMAIL = "email";
	private static final String EMAIL_AUXILIAR = "emailAuxiliar";
	private AdaptadorSpinner<Email.Tipo> mAdaptadorTipos;
	private Email mEmail;
	private Email mEmailAuxiliar;
	private OnEmailAdicionadoListener mOnEmailAdicionadoListener;
	private int mTarefasPendentes;
	private ArrayList<Email.Tipo> mTipos;

	private AppCompatButton mButtonAdicionar;
	private TextInputEditText mInputEnderecoEletronico;
	private TextInputEditText mInputNomeContato;
	private TextInputLayout mLabelEnderecoEletronico;
	private ProgressBar mProgressBarAdicionar;
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
					submeterFormulario();
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
			mEmailAuxiliar = (Email) savedInstanceState
					.getSerializable(EMAIL_AUXILIAR);
		}
		if (getArguments() != null) {
			mEmail = (Email) getArguments().getSerializable(EMAIL);
		}
		if (mEmailAuxiliar == null) {
			mEmailAuxiliar = Email.builder().tipo(null).build();
			transcreverEmail();
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
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mTipos = new ArrayList<>(Arrays.asList(Email.Tipo.values()));
		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mButtonAdicionar.setOnClickListener(this);
		mInputEnderecoEletronico.setOnFocusChangeListener(this);

		mInputEnderecoEletronico.requestFocus();
		mScrollEmailCadastro.fullScroll(View.FOCUS_UP);

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
	public void onPause() {
		super.onPause();
		iterarFormulario();
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
		outState.putSerializable(EMAIL, mEmail);
		outState.putSerializable(EMAIL_AUXILIAR, mEmailAuxiliar);
	}

	private void alternarButtonAdicionar() {
		// TODO: 9/15/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Email> emails = new ArrayList<>(mEmail.getPessoa().getEmails());
		if (emails.contains(mEmail)) {
			mButtonAdicionar.setHint(mEmail.getId() == null
					? R.string.email_cadastro_button_alterar_hint
					: R.string.email_cadastro_button_salvar_hint);
		}
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(!mEmail.getPessoa().isPerfilUsuario()
				|| credencialLogado.isPerfilAdministrador()
				|| credencialLogado.getFuncionario().getId().equals(mEmail.getPessoa().getId())
				? View.VISIBLE : View.INVISIBLE);
	}

	private VazioCallback callbackEmailPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					transcreverEmailAuxiliar();
					if (mOnEmailAdicionadoListener != null) {
						mOnEmailAdicionadoListener
								.onEmailAdicionado(mButtonAdicionar, mEmail);
					}
					getActivity().onBackPressed();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirEmailPUT() {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		EmailDTO dto = EmailMapeamento.paraDTO(mEmailAuxiliar);
		++mTarefasPendentes;
		EmailRequisicao.put(callbackEmailPUT(), mEmail.getId(), dto);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
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
		mEmailAuxiliar.setTipo((Email.Tipo) mSpinnerTipo.getSelectedItem());
		mEmailAuxiliar
				.setEnderecoEletronico(mInputEnderecoEletronico.getText().toString());
		mEmailAuxiliar.setNomeContato(mInputNomeContato.getText().toString());
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

	private void limparErros() {
		mLabelEnderecoEletronico.setError(null);
		mLabelEnderecoEletronico.setErrorEnabled(false);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
		}
	}

	private void preencherFormulario() {
		limparErros();
		mSpinnerTipo.setSelection(mEmailAuxiliar.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mEmailAuxiliar.getTipo()));
		mInputEnderecoEletronico
				.setText(mEmailAuxiliar.getEnderecoEletronico() == null
						? "" : mEmailAuxiliar.getEnderecoEletronico());
		mInputNomeContato.setText(mEmailAuxiliar.getNomeContato() == null
				? "" : mEmailAuxiliar.getNomeContato());
		alternarButtonAdicionar();
	}

	public void setEmail(@NonNull Email email) {
		mEmail = email;
		if (getArguments() != null) {
			getArguments().putSerializable(EMAIL, mEmail);
		}
		if (mEmailAuxiliar != null) {
			transcreverEmail();
		}
	}

	public void setOnEmailAdicionadoListener(@Nullable OnEmailAdicionadoListener ouvinte) {
		mOnEmailAdicionadoListener = ouvinte;
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mEmail.getId() != null) {
			consumirEmailPUT();
		} else {
			transcreverEmailAuxiliar();
			if (mOnEmailAdicionadoListener != null) {
				mOnEmailAdicionadoListener
						.onEmailAdicionado(mButtonAdicionar, mEmail);
			}
			getActivity().onBackPressed();
		}
	}

	private void transcreverEmail() {
		mEmailAuxiliar.setEnderecoEletronico(mEmail.getEnderecoEletronico());
		mEmailAuxiliar.setId(mEmail.getId());
		mEmailAuxiliar.setNomeContato(mEmail.getNomeContato());
		mEmailAuxiliar.setPessoa(mEmail.getPessoa());
		mEmailAuxiliar.setTipo(mEmail.getTipo());
	}

	private void transcreverEmailAuxiliar() {
		mEmail.setEnderecoEletronico(mEmailAuxiliar.getEnderecoEletronico());
		mEmail.setNomeContato(mEmailAuxiliar.getNomeContato());
		mEmail.setTipo(mEmailAuxiliar.getTipo());
	}

	public interface OnEmailAdicionadoListener {

		void onEmailAdicionado(@NonNull View view, @NonNull Email email);
	}
}
