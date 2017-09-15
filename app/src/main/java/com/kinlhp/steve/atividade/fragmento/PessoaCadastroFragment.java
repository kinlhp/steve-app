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
import com.kinlhp.steve.requisicao.EnderecamentoRequisicao;
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
		implements CompoundButton.OnCheckedChangeListener,
		View.OnClickListener, TextView.OnEditorActionListener,
		View.OnFocusChangeListener,
		AdapterView.OnItemSelectedListener, View.OnLongClickListener,
		Serializable {
	private static final long serialVersionUID = 5368732714590287723L;
	private static final String PESSOA = "pessoa";
	private AdaptadorSpinner<Pessoa.Tipo> mAdaptadorTipos;
	private OnEmailsSelecionadosListener mOnEmailsSelecionadosListener;
	private OnEnderecosSelecionadosListener mOnEnderecosSelecionadosListener;
	private OnPessoasPesquisaListener mOnPessoasPesquisaListener;
	private OnReferenciaPessoaAlteradaListener mOnReferenciaPessoaAlteradaListener;
	private OnTelefonesSelecionadosListener mOnTelefonesSelecionadosListener;
	private Pessoa mPessoa;
	private PessoaDTO mPessoaDTO;
	private int mTarefasPendentes;
	private List<Pessoa.Tipo> mTipos = Arrays
			.asList(Pessoa.Tipo.FISICA, Pessoa.Tipo.JURIDICA);

	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonPessoasPesquisa;
	private AppCompatButton mButtonSalvar;
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
	private ProgressBar mProgressBarConsumirPessoas;
	private ProgressBar mProgressBarConsumirPorId;
	private ProgressBar mProgressBarSalvar;
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
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_consumir_por_id:
				if (mPessoa.getId() == null) {
					consumirPessoaGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_pessoas_pesquisa:
				if (mOnPessoasPesquisaListener != null) {
					mOnPessoasPesquisaListener.onPessoasPesquisa();
				}
				break;
			case R.id.button_salvar:
				if (isFormularioValido()) {
					submeterFormulario();
				}
				break;
			case R.id.input_abertura_nascimento:
				exibirCalendario();
				break;
			case R.id.input_emails:
				if (mOnEmailsSelecionadosListener != null) {
					mOnEmailsSelecionadosListener
							.onEmailsSelecionados(view, mPessoa.getEmails());
				}
				break;
			case R.id.input_enderecos:
				if (mOnEnderecosSelecionadosListener != null) {
					mOnEnderecosSelecionadosListener
							.onEnderecosSelecionados(view, mPessoa.getEnderecos());
				}
				break;
			case R.id.input_telefones:
				if (mOnTelefonesSelecionadosListener != null) {
					mOnTelefonesSelecionadosListener
							.onTelefonesSelecionados(view, mPessoa.getTelefones());
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mPessoa = (Pessoa) savedInstanceState.getSerializable(PESSOA);
		} else if (getArguments() != null) {
			mPessoa = (Pessoa) getArguments().getSerializable(PESSOA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoa_cadastro, container, false);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonPessoasPesquisa = view.findViewById(R.id.button_pessoas_pesquisa);
		mButtonSalvar = view.findViewById(R.id.button_salvar);
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
		mProgressBarConsumirPessoas = view
				.findViewById(R.id.progress_bar_consumir_pessoas);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mProgressBarSalvar = view.findViewById(R.id.progress_bar_salvar);
		mScrollPessoaCadastro = view.findViewById(R.id.scroll_pessoa_cadastro);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);
		mSwitchPerfilCliente = view.findViewById(R.id.switch_perfil_cliente);
		mSwitchPerfilFornecedor = view
				.findViewById(R.id.switch_perfil_fornecedor);
		mSwitchPerfilTransportador = view
				.findViewById(R.id.switch_perfil_transportador);
		mSwitchPerfilUsuario = view.findViewById(R.id.switch_perfil_usuario);
		mSwitchSituacao = view.findViewById(R.id.switch_situacao);

		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mButtonConsumirPorId.setOnClickListener(this);
		mButtonPessoasPesquisa.setOnClickListener(this);
		mButtonSalvar.setOnClickListener(this);
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
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	                           long id) {
		mPessoa.setTipo((Pessoa.Tipo) parent.getItemAtPosition(position));
		alternarFormulario(mPessoa.getTipo());
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
		mButtonSalvar.setHint(mPessoa.getId() == null
				? R.string.pessoa_cadastro_button_salvar_hint
				: R.string.pessoa_cadastro_button_alterar_hint);
	}

	private void alternarSituacao() {
		mSwitchSituacao
				.setChecked(mPessoa.getSituacao().equals(Pessoa.Situacao.ATIVO));
	}

	private void atualizarReferenciaPessoa() {
		if (getArguments() != null) {
			getArguments().putSerializable(PESSOA, mPessoa);
		}
		if (mOnReferenciaPessoaAlteradaListener != null) {
			mOnReferenciaPessoaAlteradaListener
					.onReferenciaPessoaAlterada(mPessoa);
		}
	}

	private ItemCallback<UfDTO> callbackEnderecoGETUf(@NonNull final Endereco endereco) {
		return new ItemCallback<UfDTO>() {

			@Override
			public void onFailure(@NonNull Call<UfDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<UfDTO> chamada,
			                       @NonNull Response<UfDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
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
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<EmailDTO>> chamada,
			                       @NonNull Response<Colecao<EmailDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					Set<EmailDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Email> emails = EmailMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getEmails().addAll(emails);
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
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<EnderecoDTO>> chamada,
			                       @NonNull Response<Colecao<EnderecoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					Set<EnderecoDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Endereco> enderecos = EnderecoMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getEnderecos().addAll(enderecos);
					// TODO: 9/11/17 corrigir hard-coded
					String url = Parametro.get(Parametro.Chave.URL_BASE)
							.toString().concat("enderecos/%d/uf");
					for (Endereco endereco : mPessoa.getEnderecos()) {
						++mTarefasPendentes;
						HRef href =
								new HRef(String.format(url, endereco.getId()));
						EnderecamentoRequisicao
								.getUf(callbackEnderecoGETUf(endereco), href);
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
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
					mInputId.getText().clear();
				} else {
					mPessoaDTO = resposta.body();
					mPessoa = PessoaMapeamento.paraDominio(mPessoaDTO);
					atualizarReferenciaPessoa();

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
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<TelefoneDTO>> chamada,
			                       @NonNull Response<Colecao<TelefoneDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					Set<TelefoneDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Telefone> telefones = TelefoneMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getTelefones().addAll(telefones);
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
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mPessoa.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					consumirPessoaPOSTEnderecos();
					consumirPessoaPOSTTelefones();
					consumirPessoaPOSTEmails();
				}
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
			}
		};
	}

	private VazioCallback callbackPessoaPOSTEmail(final Email email) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					email.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
			}
		};
	}

	private VazioCallback callbackPessoaPOSTEndereco(final Endereco endereco) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					endereco.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
			}
		};
	}

	private VazioCallback callbackPessoaPOSTTelefone(final Telefone telefone) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					telefone.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
			}
		};
	}

	private VazioCallback callbackPessoaPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
				Falha.tratar(mButtonPessoasPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoasPesquisa, resposta);
				} else {
					consumirPessoaPOSTEnderecos();
					consumirPessoaPOSTTelefones();
					consumirPessoaPOSTEmails();
				}
				ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
			}
		};
	}

	private void consumirPessoaGETEmails() {
		++mTarefasPendentes;
		PessoaRequisicao
				.getEmails(callbackPessoaGETEmails(), mPessoaDTO.getLinks().getEmails());
	}

	private void consumirPessoaGETEnderecos() {
		++mTarefasPendentes;
		PessoaRequisicao
				.getEnderecos(callbackPessoaGETEnderecos(), mPessoaDTO.getLinks().getEnderecos());
	}

	private void consumirPessoaGETPorId() {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarConsumirPorId, null);
		mButtonConsumirPorId.setVisibility(View.INVISIBLE);
		if (!TextUtils.isEmpty(mInputId.getText())) {
			BigInteger id = new BigInteger(mInputId.getText().toString());
			if (id.compareTo(BigInteger.ONE) < 0) {
				--mTarefasPendentes;
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
		++mTarefasPendentes;
		PessoaRequisicao
				.getTelefones(callbackPessoaGETTelefones(), mPessoaDTO.getLinks().getTelefones());
	}

	private void consumirPessoaPOST() {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarSalvar, mButtonSalvar);
		Teclado.ocultar(getActivity(), mButtonSalvar);
		mPessoaDTO = PessoaMapeamento.paraDTO(mPessoa);
		++mTarefasPendentes;
		PessoaRequisicao.post(callbackPessoaPOST(), mPessoaDTO);
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
				EnderecoRequisicao.post(callbackPessoaPOSTEndereco(endereco), dto);
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
		exibirProgresso(mProgressBarSalvar, mButtonSalvar);
		Teclado.ocultar(getActivity(), mButtonSalvar);
		mPessoaDTO = PessoaMapeamento.paraDTO(mPessoa);
		++mTarefasPendentes;
		PessoaRequisicao.put(callbackPessoaPUT(), mPessoa.getId(), mPessoaDTO);
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
				&& isIeRgValido()
				&& isNomeRazaoValido();
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

	private void iterarFormulario() {
		mPessoa.setTipo((Pessoa.Tipo) mSpinnerTipo.getSelectedItem());
		mPessoa.setCnpjCpf(mInputCnpjCpf.getText().toString());
		mPessoa.setNomeRazao(mInputNomeRazao.getText().toString());
		mPessoa.setFantasiaSobrenome(mInputFantasiaSobrenome.getText().toString());
		mPessoa.setIeRg(mInputIeRg.getText().toString());

		try {
			mPessoa.setAberturaNascimento(TextUtils.isEmpty(mInputAberturaNascimento.getText())
					? null
					: Data.deStringData(mInputAberturaNascimento.getText().toString()));
		} catch (ParseException e) {
			Snackbar.make(mButtonPessoasPesquisa, getString(R.string.suporte_mensagem_conversao_data), Snackbar.LENGTH_LONG)
					.show();
		}
		mPessoa.setPerfilCliente(mSwitchPerfilCliente.isChecked());
		mPessoa.setPerfilFornecedor(mSwitchPerfilFornecedor.isChecked());
		mPessoa.setPerfilTransportador(mSwitchPerfilTransportador.isChecked());
		mPessoa.setPerfilUsuario(mPessoa.getTipo().equals(Pessoa.Tipo.FISICA)
				&& mSwitchPerfilUsuario.isChecked());
		mPessoa.setSituacao(mSwitchSituacao.isChecked()
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
		mPessoa = Pessoa.builder().build();
		atualizarReferenciaPessoa();
		preencherFormulario();
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			preencherFormulario();
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mPessoa.getId() == null
				? "" : mPessoa.getId().toString());
		mSpinnerTipo.setSelection(mPessoa.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mPessoa.getTipo()));
		mInputCnpjCpf.setText(mPessoa.getCnpjCpf() == null
				? "" : mPessoa.getCnpjCpf());
		mInputNomeRazao.setText(mPessoa.getNomeRazao() == null
				? "" : mPessoa.getNomeRazao());
		mInputFantasiaSobrenome.setText(mPessoa.getFantasiaSobrenome() == null
				? "" : mPessoa.getFantasiaSobrenome());
		mInputIeRg.setText(mPessoa.getIeRg() == null
				? "" : mPessoa.getIeRg());
		mInputAberturaNascimento.setText(mPessoa.getAberturaNascimento() == null
				? "" : Data.paraStringData(mPessoa.getAberturaNascimento()));
		preencherViewsDeLista();
		mSwitchPerfilCliente.setChecked(mPessoa.isPerfilCliente());
		mSwitchPerfilFornecedor.setChecked(mPessoa.isPerfilFornecedor());
		mSwitchPerfilTransportador.setChecked(mPessoa.isPerfilTransportador());
		mSwitchPerfilUsuario.setChecked(mPessoa.isPerfilUsuario());
		alternarSituacao();
	}

	private void preencherViewsDeLista() {
		mLabelEnderecos.setHint(mPessoa.getEnderecos().isEmpty()
				? getString(R.string.pessoa_cadastro_label_nenhum_endereco_hint)
				: getString(R.string.pessoa_cadastro_label_enderecos_hint));
		mInputEnderecos.setText(mPessoa.getEnderecos().isEmpty()
				? "" : getString(R.string.pessoa_cadastro_input_enderecos_text));
		mLabelTelefones.setHint(mPessoa.getTelefones().isEmpty()
				? getString(R.string.pessoa_cadastro_label_nenhum_telefone_hint)
				: getString(R.string.pessoa_cadastro_label_telefones_hint));
		mInputTelefones.setText(mPessoa.getTelefones().isEmpty()
				? "" : getString(R.string.pessoa_cadastro_input_telefones_text));
		mLabelEmails.setHint(mPessoa.getEmails().isEmpty()
				? getString(R.string.pessoa_cadastro_label_nenhum_email_hint)
				: getString(R.string.pessoa_cadastro_label_emails_hint));
		mInputEmails.setText(mPessoa.getEmails().isEmpty()
				? "" : getString(R.string.pessoa_cadastro_input_emails_text));
	}

	public void setOnEmailsSelecionadosListener(@Nullable OnEmailsSelecionadosListener ouvinte) {
		mOnEmailsSelecionadosListener = ouvinte;
	}

	public void setOnEnderecosSelecionadosListener(@Nullable OnEnderecosSelecionadosListener ouvinte) {
		mOnEnderecosSelecionadosListener = ouvinte;
	}

	public void setOnPessoasPesquisaListener(@Nullable OnPessoasPesquisaListener ouvinte) {
		mOnPessoasPesquisaListener = ouvinte;
	}

	public void setOnReferenciaPessoaAlteradaListener(@Nullable OnReferenciaPessoaAlteradaListener ouvinte) {
		mOnReferenciaPessoaAlteradaListener = ouvinte;
	}

	public void setOnTelefonesSelecionadosListener(@Nullable OnTelefonesSelecionadosListener ouvinte) {
		mOnTelefonesSelecionadosListener = ouvinte;
	}

	public void setPessoa(@NonNull Pessoa pessoa) {
		mPessoa = pessoa;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked) {
		view.setText(checked
				? Pessoa.Situacao.ATIVO.toString()
				: Pessoa.Situacao.INATIVO.toString());
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mPessoa.getId() == null) {
			consumirPessoaPOST();
		} else {
			consumirPessoaPUT();
		}
		ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
	}

	public interface OnEmailsSelecionadosListener {

		void onEmailsSelecionados(@NonNull View view,
		                          @NonNull Set<Email> emails);
	}

	public interface OnEnderecosSelecionadosListener {

		void onEnderecosSelecionados(@NonNull View view,
		                             @NonNull Set<Endereco> enderecos);
	}

	public interface OnPessoasPesquisaListener {

		void onPessoasPesquisa();
	}

	public interface OnReferenciaPessoaAlteradaListener {

		void onReferenciaPessoaAlterada(@NonNull Pessoa novaReferencia);
	}

	public interface OnTelefonesSelecionadosListener {

		void onTelefonesSelecionados(@NonNull View view,
		                             @NonNull Set<Telefone> telefones);
	}
}
