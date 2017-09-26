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
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.componente.DialogoCalendario;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Telefone;
import com.kinlhp.steve.dominio.Uf;
import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.dto.UfDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.EmailMapeamento;
import com.kinlhp.steve.mapeamento.EnderecoMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.mapeamento.TelefoneMapeamento;
import com.kinlhp.steve.mapeamento.UfMapeamento;
import com.kinlhp.steve.requisicao.EmailRequisicao;
import com.kinlhp.steve.requisicao.EnderecoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.PessoaRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.requisicao.TelefoneRequisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Data;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import retrofit2.Call;
import retrofit2.Response;

public class PessoaCadastroFragment extends Fragment
		implements CompoundButton.OnCheckedChangeListener, View.OnClickListener,
		TextView.OnEditorActionListener, View.OnFocusChangeListener,
		AdapterView.OnItemSelectedListener, View.OnLongClickListener,
		Serializable {
	private static final long serialVersionUID = -1206106100539236949L;
	private static final String PESSOA = "pessoa";
	private static final String PESSOA_AUXILIAR = "pessoaAuxiliar";
	private AdaptadorSpinner<Pessoa.Tipo> mAdaptadorTipos;
	private OnPessoaAdicionadoListener mOnPessoaAdicionadoListener;
	private OnEmailsPesquisaListener mOnEmailsPesquisaListener;
	private OnEnderecosPesquisaListener mOnEnderecosPesquisaListener;
	private OnPessoasPesquisaListener mOnPessoasPesquisaListener;
	private OnReferenciaPessoaAlteradoListener mOnReferenciaPessoaAlteradoListener;
	private OnTelefonesPesquisaListener mOnTelefonesPesquisaListener;
	private Pessoa mPessoa;
	private Pessoa mPessoaAuxiliar;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;
	private List<Pessoa.Tipo> mTipos;

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonPessoasPesquisa;
	private TextInputEditText mInputAberturaNascimento;
	private TextInputEditText mInputCnpjCpf;
	private TextInputEditText mInputEmails;
	private TextInputEditText mInputEnderecos;
	private TextInputEditText mInputFantasiaSobrenome;
	private TextInputEditText mInputId;
	private TextInputEditText mInputIeRg;
	private TextInputEditText mInputNomeRazao;
	private TextInputEditText mInputTelefones;
	private TextInputLayout mLabelAberturaNascimento;
	private TextInputLayout mLabelCnpjCpf;
	private TextInputLayout mLabelEmails;
	private TextInputLayout mLabelEnderecos;
	private TextInputLayout mLabelFantasiaSobrenome;
	private TextInputLayout mLabelId;
	private TextInputLayout mLabelIeRg;
	private TextInputLayout mLabelNomeRazao;
	private TextInputLayout mLabelTelefones;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirPessoas;
	private ProgressBar mProgressBarConsumirPorId;
	private ScrollView mScrollPessoaCadastro;
	private AppCompatSpinner mSpinnerTipo;
	private SwitchCompat mSwitchPerfilCliente;
	private SwitchCompat mSwitchPerfilFornecedor;
	private SwitchCompat mSwitchPerfilTransportador;
	private SwitchCompat mSwitchPerfilUsuario;
	private SwitchCompat mSwitchSituacao;

	/**
	 * Construtor padrão é obrigatório
	 */
	public PessoaCadastroFragment() {
	}

	public static PessoaCadastroFragment newInstance(@NonNull Pessoa pessoa) {
		PessoaCadastroFragment fragmento = new PessoaCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(PESSOA, pessoa);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked) {
		switch (view.getId()) {
			case R.id.switch_situacao:
				mSwitchSituacao.setText(checked
						? Pessoa.Situacao.ATIVO.toString()
						: Pessoa.Situacao.INATIVO.toString());
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
					mScrollPessoaCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.button_consumir_por_id:
				if (mPessoa.getId() == null) {
					consumirPessoaGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_pessoas_pesquisa:
				if (mOnPessoasPesquisaListener != null) {
					mOnPessoasPesquisaListener.onPessoasPesquisa(view);
				}
				break;
			case R.id.input_abertura_nascimento:
				exibirCalendario();
				break;
			case R.id.input_emails:
				if (mOnEmailsPesquisaListener != null) {
					mOnEmailsPesquisaListener.onEmailsPesquisa(view);
				}
				break;
			case R.id.input_enderecos:
				if (mOnEnderecosPesquisaListener != null) {
					mOnEnderecosPesquisaListener.onEnderecosPesquisa(view);
				}
				break;
			case R.id.input_telefones:
				if (mOnTelefonesPesquisaListener != null) {
					mOnTelefonesPesquisaListener.onTelefonesPesquisa(view);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mPessoaAuxiliar = (Pessoa) savedInstanceState
					.getSerializable(PESSOA_AUXILIAR);
		}
		if (getArguments() != null) {
			mPessoa = (Pessoa) getArguments().getSerializable(PESSOA);
		}
		if (mPessoaAuxiliar == null) {
			mPessoaAuxiliar = Pessoa.builder().build();
			transcreverPessoa();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoa_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonPessoasPesquisa = view
				.findViewById(R.id.button_pessoas_pesquisa);
		mInputAberturaNascimento = view
				.findViewById(R.id.input_abertura_nascimento);
		mInputCnpjCpf = view.findViewById(R.id.input_cnpj_cpf);
		mInputEmails = view.findViewById(R.id.input_emails);
		mInputEnderecos = view.findViewById(R.id.input_enderecos);
		mInputFantasiaSobrenome = view
				.findViewById(R.id.input_fantasia_sobrenome);
		mInputId = view.findViewById(R.id.input_id);
		mInputIeRg = view.findViewById(R.id.input_ie_rg);
		mInputNomeRazao = view.findViewById(R.id.input_nome_razao);
		mInputTelefones = view.findViewById(R.id.input_telefones);
		mLabelAberturaNascimento = view
				.findViewById(R.id.label_abertura_nascimento);
		mLabelCnpjCpf = view.findViewById(R.id.label_cnpj_cpf);
		mLabelEmails = view.findViewById(R.id.label_emails);
		mLabelEnderecos = view.findViewById(R.id.label_enderecos);
		mLabelFantasiaSobrenome = view
				.findViewById(R.id.label_fantasia_sobrenome);
		mLabelId = view.findViewById(R.id.label_id);
		mLabelIeRg = view.findViewById(R.id.label_ie_rg);
		mLabelNomeRazao = view.findViewById(R.id.label_nome_razao);
		mLabelTelefones = view.findViewById(R.id.label_telefones);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirPessoas = view
				.findViewById(R.id.progress_bar_consumir_pessoas);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mScrollPessoaCadastro = view.findViewById(R.id.scroll_pessoa_cadastro);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);
		mSwitchPerfilCliente = view.findViewById(R.id.switch_perfil_cliente);
		mSwitchPerfilFornecedor = view
				.findViewById(R.id.switch_perfil_fornecedor);
		mSwitchPerfilTransportador = view
				.findViewById(R.id.switch_perfil_transportador);
		mSwitchPerfilUsuario = view.findViewById(R.id.switch_perfil_usuario);
		mSwitchSituacao = view.findViewById(R.id.switch_situacao);

		mTipos = Arrays.asList(Pessoa.Tipo.FISICA, Pessoa.Tipo.JURIDICA);
		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirPorId.setOnClickListener(this);
		mButtonPessoasPesquisa.setOnClickListener(this);
		mInputAberturaNascimento.setOnClickListener(this);
		mInputAberturaNascimento.setOnLongClickListener(this);
		mInputCnpjCpf.setOnFocusChangeListener(this);
		mInputEmails.setOnClickListener(this);
		mInputEnderecos.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputIeRg.setOnFocusChangeListener(this);
		mInputNomeRazao.setOnFocusChangeListener(this);
		mInputTelefones.setOnClickListener(this);
		mSpinnerTipo.setOnItemSelectedListener(this);
		mSwitchSituacao.setOnCheckedChangeListener(this);

		mInputCnpjCpf.requestFocus();
		mScrollPessoaCadastro.fullScroll(View.FOCUS_UP);

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
			case R.id.input_cnpj_cpf:
				if (!focused) {
					isCnpjCpfValido();
				}
				break;
			case R.id.input_nome_razao:
				if (!focused) {
					isNomeRazaoValido();
				}
				break;
			case R.id.input_ie_rg:
				if (!focused) {
					isIeRgValido();
				}
				break;
			case R.id.input_abertura_nascimento:
				if (!focused) {
					isAberturaNascimentoValido();
				}
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	                           long id) {
		switch (parent.getId()) {
			case R.id.spinner_tipo:
				mPessoaAuxiliar
						.setTipo((Pessoa.Tipo) parent.getItemAtPosition(position));
				alternarFormulario(mPessoaAuxiliar.getTipo());
				break;
		}
	}

	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
			case R.id.input_abertura_nascimento:
				mInputAberturaNascimento.getText().clear();
				break;
		}
		return true;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		/*
		 */
	}

	@Override
	public void onPause() {
		super.onPause();
		iterarFormulario();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_titulo);
		mButtonPessoasPesquisa.setVisibility(View.VISIBLE);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(PESSOA, mPessoa);
		outState.putSerializable(PESSOA_AUXILIAR, mPessoaAuxiliar);
	}

	private void alternarButtonAdicionar() {
		mButtonAdicionar.setHint(mPessoa.getId() == null
				? R.string.pessoa_cadastro_button_adicionar_hint
				: R.string.pessoa_cadastro_button_salvar_hint);
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(!mPessoa.isPerfilUsuario()
				|| credencialLogado.isPerfilAdministrador()
				|| credencialLogado.getFuncionario().getId().equals(mPessoa.getId())
				? View.VISIBLE : View.INVISIBLE);
	}

	private void alternarFormulario(@NonNull Pessoa.Tipo tipo) {
		if (tipo.equals(Pessoa.Tipo.FISICA)) {
			definirFormularioPessoaFisica();
		} else if (tipo.equals(Pessoa.Tipo.JURIDICA)) {
			definirFormularioPessoaJuridica();
		}
	}

	private void alternarInputId() {
		mInputId.setEnabled(mPessoa.getId() == null);
		mButtonConsumirPorId.setImageResource(mPessoa.getId() == null
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
	}

	private void alternarPerfilUsuario() {
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mSwitchPerfilUsuario
				.setEnabled(credencialLogado.isPerfilAdministrador());
	}

	private void alternarSituacao() {
		mSwitchSituacao
				.setChecked(mPessoaAuxiliar.getSituacao().equals(Pessoa.Situacao.ATIVO));
	}

	private ItemCallback<UfDTO> callbackEnderecoGETUf(@NonNull Endereco endereco) {
		return new ItemCallback<UfDTO>() {

			@Override
			public void onFailure(@NonNull Call<UfDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<UfDTO> chamada,
			                       @NonNull Response<UfDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					UfDTO dto = resposta.body();
					Uf uf = UfMapeamento.paraDominio(dto);
					endereco.setUf(uf);
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ColecaoCallback<Colecao<EmailDTO>> callbackPessoaGETEmails() {
		return new ColecaoCallback<Colecao<EmailDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<EmailDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<EmailDTO>> chamada,
			                       @NonNull Response<Colecao<EmailDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					Set<EmailDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Email> emails = EmailMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getEmails().addAll(emails);
					transcreverEmailsPessoa();
					preencherViewsDeLista();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ColecaoCallback<Colecao<EnderecoDTO>> callbackPessoaGETEnderecos() {
		return new ColecaoCallback<Colecao<EnderecoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<EnderecoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<EnderecoDTO>> chamada,
			                       @NonNull Response<Colecao<EnderecoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					Set<EnderecoDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Endereco> enderecos = EnderecoMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getEnderecos().addAll(enderecos);
					transcreverEnderecosPessoa();
					preencherViewsDeLista();
					for (Endereco endereco : enderecos) {
						consumirEnderecoGETUf(endereco);
					}
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ItemCallback<PessoaDTO> callbackPessoaGETPorId() {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
					mInputId.getText().clear();
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa pessoa = PessoaMapeamento.paraDominio(dto);
					setPessoa(pessoa);
					if (mOnReferenciaPessoaAlteradoListener != null) {
						mOnReferenciaPessoaAlteradoListener
								.onReferenciaPessoaAlterado(mPessoa);
					}
					preencherFormulario();
					mInputCnpjCpf.requestFocus();
					mScrollPessoaCadastro.fullScroll(View.FOCUS_UP);

					consumirPessoaGETEmails();
					consumirPessoaGETEnderecos();
					consumirPessoaGETTelefones();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ColecaoCallback<Colecao<TelefoneDTO>> callbackPessoaGETTelefones() {
		return new ColecaoCallback<Colecao<TelefoneDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<TelefoneDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<TelefoneDTO>> chamada,
			                       @NonNull Response<Colecao<TelefoneDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					Set<TelefoneDTO> dtos = resposta.body().getEmbedded().getDtos();
					Set<Telefone> telefones = TelefoneMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getTelefones().addAll(telefones);
					transcreverTelefonesPessoa();
					preencherViewsDeLista();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private VazioCallback callbackPessoaPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mPessoa.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					transcreverPessoaAuxiliar();
					consumirPessoaPOSTEnderecos();
					consumirPessoaPOSTTelefones();
					consumirPessoaPOSTEmails();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackPessoaPOSTEmail(final Email email) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					email.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackPessoaPOSTEndereco(final Endereco endereco) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					endereco.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackPessoaPOSTTelefone(final Telefone telefone) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					telefone.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackPessoaPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					transcreverPessoaAuxiliar();
					consumirPessoaPOSTEnderecos();
					consumirPessoaPOSTTelefones();
					consumirPessoaPOSTEmails();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirEnderecoGETUf(@NonNull Endereco endereco) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("enderecos/%d/uf");
		HRef href = new HRef(String.format(url, endereco.getId()));
		++mTarefasPendentes;
		EnderecoRequisicao.getUf(callbackEnderecoGETUf(endereco), href);
	}

	private void consumirPessoaGETEmails() {
		// TODO: 9/16/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d/emails");
		HRef href = new HRef(String.format(url, mPessoa.getId()));
		++mTarefasPendentes;
		PessoaRequisicao.getEmails(callbackPessoaGETEmails(), href);
	}

	private void consumirPessoaGETEnderecos() {
		// TODO: 9/16/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d/enderecos");
		HRef href = new HRef(String.format(url, mPessoa.getId()));
		++mTarefasPendentes;
		PessoaRequisicao.getEnderecos(callbackPessoaGETEnderecos(), href);
	}

	private void consumirPessoaGETPorId() {
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
				PessoaRequisicao.getPorId(callbackPessoaGETPorId(), id);
			}
		}
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
	}

	private void consumirPessoaGETTelefones() {
		// TODO: 9/16/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d/telefones");
		HRef href = new HRef(String.format(url, mPessoa.getId()));
		++mTarefasPendentes;
		PessoaRequisicao.getTelefones(callbackPessoaGETTelefones(), href);
	}

	private void consumirPessoaPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		PessoaDTO dto = PessoaMapeamento.paraDTO(mPessoaAuxiliar);
		++mTarefasPendentes;
		PessoaRequisicao.post(callbackPessoaPOST(), dto);
	}

	private void consumirPessoaPOSTEmails() {
		for (Email email : mPessoa.getEmails()) {
			if (email.getId() == null) {
				EmailDTO dto = EmailMapeamento.paraDTO(email);
				++mTarefasPendentes;
				EmailRequisicao.post(callbackPessoaPOSTEmail(email), dto);
			}
		}
	}

	private void consumirPessoaPOSTEnderecos() {
		for (Endereco endereco : mPessoa.getEnderecos()) {
			if (endereco.getId() == null) {
				EnderecoDTO dto = EnderecoMapeamento.paraDTO(endereco);
				++mTarefasPendentes;
				EnderecoRequisicao
						.post(callbackPessoaPOSTEndereco(endereco), dto);
			}
		}
	}

	private void consumirPessoaPOSTTelefones() {
		for (Telefone telefone : mPessoa.getTelefones()) {
			if (telefone.getId() == null) {
				TelefoneDTO dto = TelefoneMapeamento.paraDTO(telefone);
				++mTarefasPendentes;
				TelefoneRequisicao
						.post(callbackPessoaPOSTTelefone(telefone), dto);
			}
		}
	}

	private void consumirPessoaPUT() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		PessoaDTO dto = PessoaMapeamento.paraDTO(mPessoaAuxiliar);
		++mTarefasPendentes;
		PessoaRequisicao.put(callbackPessoaPUT(), mPessoa.getId(), dto);
	}

	private void definirFormularioPessoaFisica() {
		mLabelCnpjCpf
				.setHint(getString(R.string.pessoa_cadastro_label_cpf_hint));
		mLabelNomeRazao
				.setHint(getString(R.string.pessoa_cadastro_label_nome_hint));
		mLabelFantasiaSobrenome
				.setHint(getString(R.string.pessoa_cadastro_label_sobrenome_hint));
		mLabelIeRg.setHint(getString(R.string.pessoa_cadastro_label_rg_hint));
		mLabelAberturaNascimento
				.setHint(getString(R.string.pessoa_cadastro_label_nascimento_hint));
		mSwitchPerfilUsuario.setVisibility(View.VISIBLE);
	}

	private void definirFormularioPessoaJuridica() {
		mLabelCnpjCpf
				.setHint(getString(R.string.pessoa_cadastro_label_cnpj_hint));
		mLabelNomeRazao
				.setHint(getString(R.string.pessoa_cadastro_label_razao_hint));
		mLabelFantasiaSobrenome
				.setHint(getString(R.string.pessoa_cadastro_label_fantasia_hint));
		mLabelIeRg.setHint(getString(R.string.pessoa_cadastro_label_ie_hint));
		mLabelAberturaNascimento
				.setHint(getString(R.string.pessoa_cadastro_label_abertura_hint));
		mSwitchPerfilUsuario.setVisibility(View.GONE);
	}

	private void exibirCalendario() {
		Date data = new Date(System.currentTimeMillis());
		if (!TextUtils.isEmpty(mInputAberturaNascimento.getText())) {
			try {
				data = Data
						.deStringData(mInputAberturaNascimento.getText().toString());
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
						.show();
			}
		}
		Calendar calendario = Calendar.getInstance(Locale.getDefault());
		calendario.setTime(data);
		DialogoCalendario
				.newInstance(mInputAberturaNascimento.getId(),
						calendario.get(Calendar.YEAR),
						calendario.get(Calendar.MONTH),
						calendario.get(Calendar.DAY_OF_MONTH))
				.show(getFragmentManager(), getString(R.string.pessoa_cadastro_label_calendario_hint));
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isAberturaNascimentoValido() {
		if (!TextUtils.isEmpty(mInputAberturaNascimento.getText())) {
			Date data = new Date(System.currentTimeMillis());
			try {
				data = Data
						.deStringData(mInputAberturaNascimento.getText().toString());
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
						.show();
			}
			if (data.compareTo(Data.fimDoDia()) > 0) {
				mLabelAberturaNascimento.setError(getString(R.string.input_invalido));
				return false;
			}
		}
		mLabelAberturaNascimento.setError(null);
		mLabelAberturaNascimento.setErrorEnabled(false);
		return true;
	}

	private boolean isCnpjValido() {
		try {
			new CNPJValidator().assertValid(mInputCnpjCpf.getText().toString());
		} catch (InvalidStateException e) {
			mLabelCnpjCpf.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelCnpjCpf.setError(null);
		mLabelCnpjCpf.setErrorEnabled(false);
		return true;
	}

	private boolean isCnpjCpfValido() {
		if (TextUtils.isEmpty(mInputCnpjCpf.getText())) {
			mLabelCnpjCpf.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		return mSpinnerTipo.getSelectedItem().equals(Pessoa.Tipo.FISICA)
				? isCpfValido() : isCnpjValido();
	}

	private boolean isCpfValido() {
		try {
			new CPFValidator().assertValid(mInputCnpjCpf.getText().toString());
		} catch (InvalidStateException e) {
			mLabelCnpjCpf.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelCnpjCpf.setError(null);
		mLabelCnpjCpf.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isCnpjCpfValido()
				&& isNomeRazaoValido()
				&& isIeRgValido()
				&& isAberturaNascimentoValido();
	}

	private boolean isIeRgValido() {
		if (TextUtils.isEmpty(mInputIeRg.getText())) {
			mLabelIeRg.setError(getString(R.string.input_obrigatorio));
			return false;
		} else if (!TextUtils.isDigitsOnly(mInputIeRg.getText())
				// TODO: 9/15/17 corrigir hard-coded
				&& !mInputIeRg.getText().toString().equalsIgnoreCase("ISENTO")) {
			mLabelIeRg.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelIeRg.setError(null);
		mLabelIeRg.setErrorEnabled(false);
		return true;
	}

	private boolean isNomeRazaoValido() {
		if (TextUtils.isEmpty(mInputNomeRazao.getText())) {
			mLabelNomeRazao.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelNomeRazao.setError(null);
		mLabelNomeRazao.setErrorEnabled(false);
		return true;
	}

	private void iterarAberturaNascimento() throws ParseException {
		mPessoaAuxiliar
				.setAberturaNascimento(TextUtils.isEmpty(mInputAberturaNascimento.getText())
						? null
						: Data.deStringData(mInputAberturaNascimento.getText().toString()));
//		try {
//		} catch (ParseException e) {
//			Snackbar.make(mButtonPessoasPesquisa, getString(R.string.suporte_mensagem_conversao_data), Snackbar.LENGTH_LONG)
//					.show();
//		}
	}

	private void iterarFormulario() {
		mPessoaAuxiliar.setTipo((Pessoa.Tipo) mSpinnerTipo.getSelectedItem());
		mPessoaAuxiliar.setCnpjCpf(mInputCnpjCpf.getText().toString());
		mPessoaAuxiliar.setNomeRazao(mInputNomeRazao.getText().toString());
		mPessoaAuxiliar
				.setFantasiaSobrenome(mInputFantasiaSobrenome.getText().toString());
		mPessoaAuxiliar.setIeRg(mInputIeRg.getText().toString());
		try {
			iterarAberturaNascimento();
		} catch (ParseException e) {
			Snackbar.make(mButtonPessoasPesquisa, getString(R.string.suporte_mensagem_conversao_data), Snackbar.LENGTH_LONG)
					.show();
		}
		mPessoaAuxiliar.setPerfilCliente(mSwitchPerfilCliente.isChecked());
		mPessoaAuxiliar
				.setPerfilFornecedor(mSwitchPerfilFornecedor.isChecked());
		mPessoaAuxiliar
				.setPerfilTransportador(mSwitchPerfilTransportador.isChecked());
		mPessoaAuxiliar
				.setPerfilUsuario(mPessoaAuxiliar.getTipo().equals(Pessoa.Tipo.FISICA)
						&& mSwitchPerfilUsuario.isChecked());
		mPessoaAuxiliar.setSituacao(mSwitchSituacao.isChecked()
				? Pessoa.Situacao.ATIVO : Pessoa.Situacao.INATIVO);
	}

	private void limparErros() {
		mLabelId.setError(null);
		mLabelId.setErrorEnabled(false);
		mLabelCnpjCpf.setError(null);
		mLabelCnpjCpf.setErrorEnabled(false);
		mLabelNomeRazao.setError(null);
		mLabelNomeRazao.setErrorEnabled(false);
		mLabelIeRg.setError(null);
		mLabelIeRg.setErrorEnabled(false);
	}

	private void limparFormulario() {
		setPessoa(Pessoa.builder().build());
		if (mOnReferenciaPessoaAlteradoListener != null) {
			mOnReferenciaPessoaAlteradoListener
					.onReferenciaPessoaAlterado(mPessoa);
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
				if (mOnPessoaAdicionadoListener != null) {
					mOnPessoaAdicionadoListener
							.onPessoaAdicionado(mButtonAdicionar, mPessoa);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mPessoaAuxiliar.getId() == null
				? "" : mPessoaAuxiliar.getId().toString());
		mSpinnerTipo.setSelection(mPessoaAuxiliar.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mPessoaAuxiliar.getTipo()));
		mInputCnpjCpf.setText(mPessoaAuxiliar.getCnpjCpf() == null
				? "" : mPessoaAuxiliar.getCnpjCpf());
		mInputNomeRazao.setText(mPessoaAuxiliar.getNomeRazao() == null
				? "" : mPessoaAuxiliar.getNomeRazao());
		mInputFantasiaSobrenome
				.setText(mPessoaAuxiliar.getFantasiaSobrenome() == null
						? "" : mPessoaAuxiliar.getFantasiaSobrenome());
		mInputIeRg.setText(mPessoaAuxiliar.getIeRg() == null
				? "" : mPessoaAuxiliar.getIeRg());
		mInputAberturaNascimento
				.setText(mPessoaAuxiliar.getAberturaNascimento() == null
						? ""
						: Data.paraStringData(mPessoaAuxiliar.getAberturaNascimento()));
		preencherViewsDeLista();
		mSwitchPerfilCliente.setChecked(mPessoaAuxiliar.isPerfilCliente());
		mSwitchPerfilFornecedor
				.setChecked(mPessoaAuxiliar.isPerfilFornecedor());
		mSwitchPerfilTransportador
				.setChecked(mPessoaAuxiliar.isPerfilTransportador());
		mSwitchPerfilUsuario.setChecked(mPessoaAuxiliar.isPerfilUsuario());
		alternarPerfilUsuario();
		alternarSituacao();
		alternarButtonAdicionar();
	}

	private void preencherViewsDeLista() {
		mLabelEnderecos.setHint(mPessoa.getEnderecos().isEmpty()
				? getString(R.string.pessoa_cadastro_label_nenhum_endereco_hint)
				: getString(R.string.pessoa_cadastro_label_enderecos_hint));
		mInputEnderecos.setText(mPessoa.getEnderecos().isEmpty()
				? ""
				: getString(R.string.pessoa_cadastro_input_enderecos_text));
		mLabelTelefones.setHint(mPessoa.getTelefones().isEmpty()
				? getString(R.string.pessoa_cadastro_label_nenhum_telefone_hint)
				: getString(R.string.pessoa_cadastro_label_telefones_hint));
		mInputTelefones.setText(mPessoa.getTelefones().isEmpty()
				? ""
				: getString(R.string.pessoa_cadastro_input_telefones_text));
		mLabelEmails.setHint(mPessoa.getEmails().isEmpty()
				? getString(R.string.pessoa_cadastro_label_nenhum_email_hint)
				: getString(R.string.pessoa_cadastro_label_emails_hint));
		mInputEmails.setText(mPessoa.getEmails().isEmpty()
				? "" : getString(R.string.pessoa_cadastro_input_emails_text));
	}

	public void setOnEmailsPesquisaListener(@Nullable OnEmailsPesquisaListener ouvinte) {
		mOnEmailsPesquisaListener = ouvinte;
	}

	public void setOnEnderecosPesquisaListener(@Nullable OnEnderecosPesquisaListener ouvinte) {
		mOnEnderecosPesquisaListener = ouvinte;
	}

	public void setOnPessoaAdicionadoListener(@Nullable OnPessoaAdicionadoListener ouvinte) {
		mOnPessoaAdicionadoListener = ouvinte;
	}

	public void setOnPessoasPesquisaListener(@Nullable OnPessoasPesquisaListener ouvinte) {
		mOnPessoasPesquisaListener = ouvinte;
	}

	public void setOnReferenciaPessoaAlteradoListener(@Nullable OnReferenciaPessoaAlteradoListener ouvinte) {
		mOnReferenciaPessoaAlteradoListener = ouvinte;
	}

	public void setOnTelefonesPesquisaListener(@Nullable OnTelefonesPesquisaListener ouvinte) {
		mOnTelefonesPesquisaListener = ouvinte;
	}

	public void setPessoa(@NonNull Pessoa pessoa) {
		mPessoa = pessoa;
		if (getArguments() != null) {
			getArguments().putSerializable(PESSOA, mPessoa);
		}
		if (mPessoaAuxiliar != null) {
			transcreverPessoa();
		}
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mPessoa.getId() != null) {
			consumirPessoaPUT();
		} else {
			consumirPessoaPOST();
		}
	}

	private void transcreverEmailsPessoa() {
		mPessoaAuxiliar.getEmails().clear();
		mPessoaAuxiliar.getEmails().addAll(mPessoa.getEmails());
	}

	private void transcreverEnderecosPessoa() {
		mPessoaAuxiliar.getEnderecos().clear();
		mPessoaAuxiliar.getEnderecos().addAll(mPessoa.getEnderecos());
	}

	private void transcreverPessoa() {
		mPessoaAuxiliar.setAberturaNascimento(mPessoa.getAberturaNascimento());
		mPessoaAuxiliar.setCnpjCpf(mPessoa.getCnpjCpf());
		transcreverEmailsPessoa();
		transcreverEnderecosPessoa();
		mPessoaAuxiliar.setFantasiaSobrenome(mPessoa.getFantasiaSobrenome());
		mPessoaAuxiliar.setId(mPessoa.getId());
		mPessoaAuxiliar.setIeRg(mPessoa.getIeRg());
		mPessoaAuxiliar.setNomeRazao(mPessoa.getNomeRazao());
		mPessoaAuxiliar.setPerfilCliente(mPessoa.isPerfilCliente());
		mPessoaAuxiliar.setPerfilFornecedor(mPessoa.isPerfilFornecedor());
		mPessoaAuxiliar.setPerfilTransportador(mPessoa.isPerfilTransportador());
		mPessoaAuxiliar.setPerfilUsuario(mPessoa.isPerfilUsuario());
		mPessoaAuxiliar.setSituacao(mPessoa.getSituacao());
		transcreverTelefonesPessoa();
		mPessoaAuxiliar.setTipo(mPessoa.getTipo());
	}

	private void transcreverPessoaAuxiliar() {
		mPessoa.setAberturaNascimento(mPessoaAuxiliar.getAberturaNascimento());
		mPessoa.setCnpjCpf(mPessoaAuxiliar.getCnpjCpf());
		mPessoa.setFantasiaSobrenome(mPessoaAuxiliar.getFantasiaSobrenome());
		mPessoa.setIeRg(mPessoaAuxiliar.getIeRg());
		mPessoa.setNomeRazao(mPessoaAuxiliar.getNomeRazao());
		mPessoa.setPerfilCliente(mPessoaAuxiliar.isPerfilCliente());
		mPessoa.setPerfilFornecedor(mPessoaAuxiliar.isPerfilFornecedor());
		mPessoa.setPerfilTransportador(mPessoaAuxiliar.isPerfilTransportador());
		mPessoa.setPerfilUsuario(mPessoaAuxiliar.isPerfilUsuario());
		mPessoa.setSituacao(mPessoaAuxiliar.getSituacao());
		mPessoa.setTipo(mPessoaAuxiliar.getTipo());
	}

	private void transcreverTelefonesPessoa() {
		mPessoaAuxiliar.getTelefones().clear();
		mPessoaAuxiliar.getTelefones().addAll(mPessoa.getTelefones());
	}

	public interface OnEmailsPesquisaListener {

		void onEmailsPesquisa(@NonNull View view);
	}

	public interface OnEnderecosPesquisaListener {

		void onEnderecosPesquisa(@NonNull View view);
	}

	public interface OnPessoaAdicionadoListener {

		void onPessoaAdicionado(@NonNull View view, @NonNull Pessoa pessoa);
	}

	public interface OnPessoasPesquisaListener {

		void onPessoasPesquisa(@NonNull View view);
	}

	public interface OnReferenciaPessoaAlteradoListener {

		void onReferenciaPessoaAlterado(@NonNull Pessoa novaReferencia);
	}

	public interface OnTelefonesPesquisaListener {

		void onTelefonesPesquisa(@NonNull View view);
	}
}
