package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.LoginDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CredencialMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.CredencialRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.LoginRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Criptografia;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class CredencialCadastroFragment extends Fragment
		implements CompoundButton.OnCheckedChangeListener,
		View.OnClickListener, TextView.OnEditorActionListener,
		View.OnFocusChangeListener, Serializable {
	private static final long serialVersionUID = 7579224148305940311L;
	private static final String CREDENCIAL = "credencial";
	private static final String CREDENCIAL_AUXILIAR = "credencialAuxiliar";
	private OnCredenciaisPesquisaListener mOnCredenciaisPesquisaListener;
	private OnCredencialAdicionadoListener mOnCredencialAdicionadoListener;
	private OnFuncionariosPesquisaListener mOnFuncionariosPesquisaListener;
	private OnReferenciaCredencialAlteradoListener mOnReferenciaCredencialAlteradoListener;
	private Credencial mCredencial;
	private Credencial mCredencialAuxiliar;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonCredenciaisPesquisa;
	private TextInputEditText mInputConfirmacaoSenha;
	private TextInputEditText mInputFuncionario;
	private TextInputEditText mInputId;
	private TextInputEditText mInputSenha;
	private TextInputEditText mInputUsuario;
	private TextInputLayout mLabelConfirmacaoSenha;
	private TextInputLayout mLabelFuncionario;
	private TextInputLayout mLabelId;
	private TextInputLayout mLabelSenha;
	private TextInputLayout mLabelUsuario;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirCredenciais;
	private ProgressBar mProgressBarConsumirPorId;
	private ScrollView mScrollCredencialCadastro;
	private SwitchCompat mSwitchPerfilAdministrador;
	private SwitchCompat mSwitchSituacao;

	/**
	 * Construtor padrão é obrigatório
	 */
	public CredencialCadastroFragment() {
	}

	public static CredencialCadastroFragment newInstance(@NonNull Credencial credencial) {
		CredencialCadastroFragment fragmento = new CredencialCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(CREDENCIAL, credencial);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked) {
		switch (view.getId()) {
			case R.id.switch_situacao:
				mSwitchSituacao.setText(checked
						? Credencial.Situacao.ATIVO.toString()
						: Credencial.Situacao.INATIVO.toString());
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					submeterFormulario();
				} else {
					mScrollCredencialCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.button_consumir_por_id:
				if (mCredencial.getId() == null) {
					consumirCredencialGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_credenciais_pesquisa:
				if (mOnCredenciaisPesquisaListener != null) {
					mOnCredenciaisPesquisaListener.onCredenciaisPesquisa(view);
				}
				break;
			case R.id.input_funcionario:
				if (mOnFuncionariosPesquisaListener != null) {
					mOnFuncionariosPesquisaListener
							.onFuncionariosPesquisa(view);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCredencialAuxiliar = (Credencial) savedInstanceState
					.getSerializable(CREDENCIAL_AUXILIAR);
		}
		if (getArguments() != null) {
			mCredencial = (Credencial) getArguments()
					.getSerializable(CREDENCIAL);
		}
		if (mCredencialAuxiliar == null) {
			mCredencialAuxiliar = Credencial.builder().build();
			transcreverCredencial();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_credencial_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonCredenciaisPesquisa = view
				.findViewById(R.id.button_credenciais_pesquisa);
		mInputConfirmacaoSenha = view
				.findViewById(R.id.input_confirmacao_senha);
		mInputFuncionario = view.findViewById(R.id.input_funcionario);
		mInputId = view.findViewById(R.id.input_id);
		mInputSenha = view.findViewById(R.id.input_senha);
		mInputUsuario = view.findViewById(R.id.input_usuario);
		mLabelConfirmacaoSenha = view
				.findViewById(R.id.label_confirmacao_senha);
		mLabelFuncionario = view.findViewById(R.id.label_funcionario);
		mLabelId = view.findViewById(R.id.label_id);
		mLabelSenha = view.findViewById(R.id.label_senha);
		mLabelUsuario = view.findViewById(R.id.label_usuario);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirCredenciais = view
				.findViewById(R.id.progress_bar_consumir_credenciais);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mScrollCredencialCadastro = view
				.findViewById(R.id.scroll_credencial_cadastro);
		mSwitchPerfilAdministrador = view
				.findViewById(R.id.switch_perfil_administrador);
		mSwitchSituacao = view.findViewById(R.id.switch_situacao);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirPorId.setOnClickListener(this);
		mButtonCredenciaisPesquisa.setOnClickListener(this);

		mInputFuncionario.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputUsuario.setOnFocusChangeListener(this);
		mInputSenha.setOnFocusChangeListener(this);
		mInputConfirmacaoSenha.setOnFocusChangeListener(this);
		mSwitchSituacao.setOnCheckedChangeListener(this);

		mInputUsuario.requestFocus();
		mScrollCredencialCadastro.fullScroll(View.FOCUS_UP);

		return view;
	}

	@Override
	public boolean onEditorAction(TextView view, int id, KeyEvent event) {
		switch (id) {
			case EditorInfo.IME_ACTION_SEARCH:
				mButtonConsumirPorId.performClick();
				break;
		}
		return false;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_confirmacao_senha:
				if (!focused) {
					isConfirmacaoSenhaValido();
				}
				break;
			case R.id.input_senha:
				if (!focused) {
					isSenhaValido();
				}
				break;
			case R.id.input_usuario:
				if (!focused) {
					isUsuarioValido();
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
		getActivity().setTitle(R.string.credencial_cadastro_titulo);
		mButtonCredenciaisPesquisa.setVisibility(View.VISIBLE);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CREDENCIAL, mCredencial);
		outState.putSerializable(CREDENCIAL_AUXILIAR, mCredencialAuxiliar);
	}

	private void alternarButtonAdicionar() {
		mButtonAdicionar.setHint(mCredencial.getId() == null
				? R.string.credencial_cadastro_button_adicionar_hint
				: R.string.credencial_cadastro_button_salvar_hint);
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(credencialLogado.isPerfilAdministrador()
				|| credencialLogado.getId().equals(mCredencial.getId())
				? View.VISIBLE : View.INVISIBLE);
	}

	private void alternarInputFuncionario() {
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mInputFuncionario.setEnabled(credencialLogado.isPerfilAdministrador()
				|| mCredencial.getId() == null);
	}

	private void alternarInputId() {
		mInputId.setEnabled(mCredencial.getId() == null);
		mButtonConsumirPorId.setImageResource(mCredencial.getId() == null
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
		alternarButtonAdicionar();
	}

	private void alternarSituacao() {
		mSwitchSituacao
				.setChecked(mCredencialAuxiliar.getSituacao().equals(Credencial.Situacao.ATIVO));
	}

	private void alternarSwitchPerfilAdministrador() {
		mSwitchPerfilAdministrador
				.setChecked(mCredencialAuxiliar.isPerfilAdministrador());
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mSwitchPerfilAdministrador
				.setEnabled(credencialLogado.isPerfilAdministrador());
	}

	private ItemCallback<PessoaDTO> callbackCredencialGETFuncionario() {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonCredenciaisPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonCredenciaisPesquisa, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa funcionario = PessoaMapeamento.paraDominio(dto);
					mCredencial.setFuncionario(funcionario);
					transcreverFuncionarioCredencial();
					preencherViewFuncionario();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ItemCallback<CredencialDTO> callbackCredencialGETPorId() {
		return new ItemCallback<CredencialDTO>() {

			@Override
			public void onFailure(@NonNull Call<CredencialDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonCredenciaisPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<CredencialDTO> chamada,
			                       @NonNull Response<CredencialDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonCredenciaisPesquisa, resposta);
					mInputId.getText().clear();
				} else {
					CredencialDTO dto = resposta.body();
					Credencial credencial = CredencialMapeamento
							.paraDominio(dto);
					setCredencial(credencial);
					if (mOnReferenciaCredencialAlteradoListener != null) {
						mOnReferenciaCredencialAlteradoListener
								.onReferenciaCredencialAlterado(mCredencial);
					}
					preencherFormulario();
					mInputUsuario.requestFocus();
					mScrollCredencialCadastro.fullScroll(View.FOCUS_UP);

					consumirCredencialGETFuncionario();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private VazioCallback callbackCredencialPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonCredenciaisPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonCredenciaisPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mCredencial
							.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					transcreverCredencialAuxiliar();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackCredencialPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonCredenciaisPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonCredenciaisPesquisa, resposta);
				} else {
					transcreverCredencialAuxiliar();
					Credencial credencialLogado = (Credencial) Parametro
							.get(Parametro.Chave.CREDENCIAL);
					if (credencialLogado.getId().equals(mCredencial.getId())) {
						consumirLoginPOST();
					}
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackCredencialPUTFuncionario() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonCredenciaisPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonCredenciaisPesquisa, resposta);
				} else {
					transcreverFuncionarioCredencialAuxiliar();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackLoginPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonCredenciaisPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonCredenciaisPesquisa, resposta);
				} else {
					String token = resposta.headers()
							.get(Requisicao.AUTHORIZATION_HEADER);
					Parametro.put(Parametro.Chave.TOKEN, token);
					Parametro.put(Parametro.Chave.CREDENCIAL, mCredencial);
					if (mCredencial.isPerfilAdministrador()) {
						consumirCredencialPUTFuncionario();
					}
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirCredencialGETFuncionario() {
		// TODO: 9/18/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("credenciais/%d/funcionario");
		HRef href = new HRef(String.format(url, mCredencial.getId()));
		++mTarefasPendentes;
		CredencialRequisicao
				.getFuncionario(callbackCredencialGETFuncionario(), href);
	}

	private void consumirCredencialGETPorId() {
		mTarefasPendentes = 0;
		mPressionarVoltar = false;
		exibirProgresso(mProgressBarConsumirPorId, null);
		mButtonConsumirPorId.setVisibility(View.INVISIBLE);
		if (!TextUtils.isEmpty(mInputId.getText())) {
			BigInteger id = new BigInteger(mInputId.getText().toString());
			if (id.compareTo(BigInteger.ONE) < 0) {
				mInputId.getText().clear();
			} else {
				mLabelId.setError(null);
				mLabelId.setErrorEnabled(false);
				Teclado.ocultar(getActivity(), mButtonConsumirPorId);
				++mTarefasPendentes;
				CredencialRequisicao.getPorId(callbackCredencialGETPorId(), id);
			}
		}
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
	}

	private void consumirCredencialPOST() {
		try {
			mCredencialAuxiliar
					.setSenha(Criptografia.sha512(mCredencialAuxiliar.getSenha()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			String mensagem =
					getString(R.string.suporte_mensagem_criptografia_senha);
			Snackbar.make(mButtonCredenciaisPesquisa, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		CredencialDTO dto = CredencialMapeamento.paraDTO(mCredencialAuxiliar);
		++mTarefasPendentes;
		CredencialRequisicao.post(callbackCredencialPOST(), dto);
	}

	private void consumirCredencialPUT() {
		try {
			mCredencialAuxiliar
					.setSenha(Criptografia.sha512(mCredencialAuxiliar.getSenha()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			String mensagem =
					getString(R.string.suporte_mensagem_criptografia_senha);
			Snackbar.make(mButtonCredenciaisPesquisa, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		CredencialDTO dto = CredencialMapeamento.paraDTO(mCredencialAuxiliar);
		++mTarefasPendentes;
		CredencialRequisicao
				.put(callbackCredencialPUT(), mCredencial.getId(), dto);
	}

	private void consumirCredencialPUTFuncionario() {
		// TODO: 9/17/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d");
		// TODO: 9/17/17 corrigir hard-coded
		RequestBody uriList = RequestBody
				.create(MediaType.parse("text/uri-list"), String.format(url, mCredencialAuxiliar.getFuncionario().getId()));
		++mTarefasPendentes;
		CredencialRequisicao
				.putFuncionario(callbackCredencialPUTFuncionario(), mCredencial.getId(), uriList);
	}

	private void consumirLoginPOST() {
		String usuario = mCredencial.getUsuario();
		String senha = mCredencial.getSenha();
		LoginDTO dto = LoginDTO.builder().usuario(usuario).senha(senha).build();
		++mTarefasPendentes;
		LoginRequisicao.post(callbackLoginPOST(), dto);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isConfirmacaoSenhaValido() {
		if (TextUtils.isEmpty(mInputConfirmacaoSenha.getText())) {
			mLabelConfirmacaoSenha
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!mInputConfirmacaoSenha.getText().toString().equals(mInputSenha.getText().toString())) {
			mLabelConfirmacaoSenha.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelConfirmacaoSenha.setError(null);
		mLabelConfirmacaoSenha.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isFuncionarioValido()
				&& isUsuarioValido()
				&& isSenhaValido()
				&& isConfirmacaoSenhaValido();
	}

	private boolean isFuncionarioValido() {
		if (mCredencial.getFuncionario() == null) {
			mLabelFuncionario.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!mCredencial.getFuncionario().isPerfilUsuario()) {
			mLabelFuncionario
					.setError(getString(R.string.credencial_cadastro_input_funcionario_nao_usuario));
			return false;
		}
		mLabelFuncionario.setError(null);
		mLabelFuncionario.setErrorEnabled(false);
		return true;
	}

	private boolean isSenhaValido() {
		if (TextUtils.isEmpty(mInputSenha.getText())) {
			mLabelSenha.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelSenha.setError(null);
		mLabelSenha.setErrorEnabled(false);
		return true;
	}

	private boolean isUsuarioValido() {
		if (TextUtils.isEmpty(mInputUsuario.getText())) {
			mLabelUsuario.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelUsuario.setError(null);
		mLabelUsuario.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mCredencialAuxiliar.setUsuario(mInputUsuario.getText().toString());
		mCredencialAuxiliar.setSenha(mInputSenha.getText().toString());
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mCredencialAuxiliar
				.setPerfilAdministrador(credencialLogado.isPerfilAdministrador()
						&& mSwitchPerfilAdministrador.isChecked());
		mCredencialAuxiliar.setSituacao(mSwitchSituacao.isChecked()
				? Credencial.Situacao.ATIVO
				: Credencial.Situacao.INATIVO);
		transcreverFuncionarioCredencial();
	}

	private void limparErros() {
		mLabelId.setError(null);
		mLabelId.setErrorEnabled(false);
		mLabelFuncionario.setError(null);
		mLabelFuncionario.setErrorEnabled(false);
		mLabelUsuario.setError(null);
		mLabelUsuario.setErrorEnabled(false);
		mLabelSenha.setError(null);
		mLabelSenha.setErrorEnabled(false);
		mLabelConfirmacaoSenha.setError(null);
		mLabelConfirmacaoSenha.setErrorEnabled(false);
	}

	private void limparFormulario() {
		setCredencial(Credencial.builder().build());
		if (mOnReferenciaCredencialAlteradoListener != null) {
			mOnReferenciaCredencialAlteradoListener
					.onReferenciaCredencialAlterado(mCredencial);
		}
		preencherFormulario();
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPressionarVoltar) {
				if (mOnCredencialAdicionadoListener != null) {
					mOnCredencialAdicionadoListener
							.onCredencialAdicionado(mButtonAdicionar, mCredencial);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mCredencialAuxiliar.getId() == null
				? "" : mCredencialAuxiliar.getId().toString());
		preencherViewFuncionario();
		alternarInputFuncionario();
		mInputUsuario.setText(mCredencialAuxiliar.getUsuario() == null
				? "" : mCredencialAuxiliar.getUsuario());
		mInputSenha.setText(mCredencialAuxiliar.getSenha() == null
				? "" : mCredencialAuxiliar.getSenha());
		mInputConfirmacaoSenha.setText(mCredencialAuxiliar.getSenha() == null
				? "" : mCredencialAuxiliar.getSenha());
		alternarSwitchPerfilAdministrador();
		alternarSituacao();
	}

	private void preencherViewFuncionario() {
		mLabelFuncionario.setHint(mCredencial.getFuncionario() == null
				? getString(R.string.credencial_cadastro_label_nenhum_funcionario_hint)
				: getString(R.string.credencial_cadastro_label_funcionario_hint));
		mInputFuncionario.setText(mCredencial.getFuncionario() == null
				? "" : mCredencial.getFuncionario().toString());
	}

	public void setOnCredencialAdicionadoListener(@Nullable OnCredencialAdicionadoListener ouvinte) {
		mOnCredencialAdicionadoListener = ouvinte;
	}

	public void setOnFuncionariosPesquisaListener(@Nullable OnFuncionariosPesquisaListener ouvinte) {
		mOnFuncionariosPesquisaListener = ouvinte;
	}

	public void setOnCredenciaisPesquisaListener(@Nullable OnCredenciaisPesquisaListener ouvinte) {
		mOnCredenciaisPesquisaListener = ouvinte;
	}

	public void setOnReferenciaCredencialAlteradoListener(@Nullable OnReferenciaCredencialAlteradoListener ouvinte) {
		mOnReferenciaCredencialAlteradoListener = ouvinte;
	}

	public void setCredencial(@NonNull Credencial credencial) {
		mCredencial = credencial;
		if (getArguments() != null) {
			getArguments().putSerializable(CREDENCIAL, mCredencial);
		}
		if (mCredencialAuxiliar != null) {
			transcreverCredencial();
		}
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mCredencial.getId() != null) {
			consumirCredencialPUT();
		} else {
			consumirCredencialPOST();
		}
	}

	private void transcreverCredencial() {
		transcreverFuncionarioCredencial();
		mCredencialAuxiliar.setId(mCredencial.getId());
		mCredencialAuxiliar
				.setPerfilAdministrador(mCredencial.isPerfilAdministrador());
		mCredencialAuxiliar.setSenha(mCredencial.getSenha());
		mCredencialAuxiliar.setSituacao(mCredencial.getSituacao());
		mCredencialAuxiliar.setUsuario(mCredencial.getUsuario());
	}

	private void transcreverCredencialAuxiliar() {
		mCredencial
				.setPerfilAdministrador(mCredencialAuxiliar.isPerfilAdministrador());
		mCredencial.setSenha(mCredencialAuxiliar.getSenha());
		mCredencial.setSituacao(mCredencialAuxiliar.getSituacao());
		mCredencial.setUsuario(mCredencialAuxiliar.getUsuario());
	}

	private void transcreverFuncionarioCredencial() {
		mCredencialAuxiliar.setFuncionario(mCredencial.getFuncionario());
	}

	private void transcreverFuncionarioCredencialAuxiliar() {
		mCredencial.setFuncionario(mCredencialAuxiliar.getFuncionario());
	}

	public interface OnCredenciaisPesquisaListener {

		void onCredenciaisPesquisa(@NonNull View view);
	}

	public interface OnCredencialAdicionadoListener {

		void onCredencialAdicionado(@NonNull View view,
		                            @NonNull Credencial credencial);
	}

	public interface OnFuncionariosPesquisaListener {

		void onFuncionariosPesquisa(@NonNull View view);
	}

	public interface OnReferenciaCredencialAlteradoListener {

		void onReferenciaCredencialAlterado(@NonNull Credencial novaReferencia);
	}
}
