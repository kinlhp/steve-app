package com.kinlhp.steve.atividade.fragmento;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.componente.DialogoCalendario;
import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Telefone;
import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.mapeamento.EmailMapeamento;
import com.kinlhp.steve.mapeamento.EnderecoMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.mapeamento.TelefoneMapeamento;
import com.kinlhp.steve.requisicao.EmailRequisicao;
import com.kinlhp.steve.requisicao.EnderecoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.PessoaRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.requisicao.TelefoneRequisicao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.Resposta;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Data;
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

import retrofit2.Call;
import retrofit2.Response;

public class PessoaCadastroFragment extends Fragment
		implements View.OnClickListener, TextView.OnEditorActionListener,
		View.OnLongClickListener, Serializable {
	private static final long serialVersionUID = 4334858035488377158L;
	private static final String PESSOA = "pessoa";
	private OnListClickListener mListClickListener;
	private Pessoa mPessoa;
	private PessoaDTO mPessoaDTO;
	private int mTarefasPendentes;

	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonPessoaPesquisa;
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
	private ProgressBar mProgressBarConsumirPorId;
	private ProgressBar mProgressBarSalvar;
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
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListClickListener) {
			mListClickListener = (OnListClickListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnClickListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_consumir_por_id:
				mTarefasPendentes = 0;
				if (mPessoa.getId() == null) {
					consumirPorId(view);
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_salvar:
				mTarefasPendentes = 0;
				submeterFormulario(view);
				break;
			case R.id.input_abertura_nascimento:
				exibirCalendario();
				break;
			case R.id.input_emails:
				mListClickListener.onEmailsClick(mPessoa.getEmails());
				break;
			case R.id.input_enderecos:
				mListClickListener.onEnderecosClick(mPessoa.getEnderecos());
				break;
			case R.id.input_telefones:
				mListClickListener.onTelefonesClick(mPessoa.getTelefones());
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPessoa = getArguments() != null
				? (Pessoa) getArguments().getSerializable(PESSOA)
				: Pessoa.builder().build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoa_cadastro, container, false);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonPessoaPesquisa = getActivity()
				.findViewById(R.id.button_pessoa_pesquisa);
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
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mProgressBarSalvar = view.findViewById(R.id.progress_bar_salvar);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);
		mSwitchPerfilCliente = view.findViewById(R.id.switch_perfil_cliente);
		mSwitchPerfilFornecedor = view
				.findViewById(R.id.switch_perfil_fornecedor);
		mSwitchPerfilTransportador = view
				.findViewById(R.id.switch_perfil_transportador);
		mSwitchPerfilUsuario = view.findViewById(R.id.switch_perfil_usuario);
		mSwitchSituacao = view.findViewById(R.id.switch_situacao);

		List<Pessoa.Tipo> tipos = Arrays
				.asList(Pessoa.Tipo.FISICA, Pessoa.Tipo.JURIDICA);
		mSpinnerTipo.setAdapter(new AdaptadorSpinner<>(getActivity(), tipos));

		mButtonConsumirPorId.setOnClickListener(this);
		mButtonSalvar.setOnClickListener(this);
		mInputAberturaNascimento.setOnClickListener(this);
		mInputAberturaNascimento.setOnLongClickListener(this);
		mInputEmails.setOnClickListener(this);
		mInputEnderecos.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputTelefones.setOnClickListener(this);
		mSpinnerTipo.setOnItemSelectedListener(tipoSelectedListener());
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListClickListener = null;
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
	public boolean onLongClick(View view) {
		switch (view.getId()) {
			case R.id.input_abertura_nascimento:
				mInputAberturaNascimento.getText().clear();
				break;
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_titulo);
		mButtonPessoaPesquisa.setVisibility(View.VISIBLE);
		habilitarInputId(mPessoa.getId() == null);
		preencherViewsDeLista();
	}

	private void alternarFormulario(@NonNull Pessoa.Tipo tipo) {
		if (tipo.equals(Pessoa.Tipo.FISICA)) {
			definirFormularioPessoaFisica();
		} else if (tipo.equals(Pessoa.Tipo.JURIDICA)) {
			definirFormularioPessoaJuridica();
		}
	}

	private ColecaoCallback<Resposta<EmailDTO>> callbackPessoaGETEmails() {
		return new ColecaoCallback<Resposta<EmailDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Resposta<EmailDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Resposta<EmailDTO>> chamada,
			                       @NonNull Response<Resposta<EmailDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
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

	private ColecaoCallback<Resposta<EnderecoDTO>> callbackPessoaGETEnderecos() {
		return new ColecaoCallback<Resposta<EnderecoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Resposta<EnderecoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Resposta<EnderecoDTO>> chamada,
			                       @NonNull Response<Resposta<EnderecoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
				} else {
					Set<EnderecoDTO> dtos = resposta.body().getEmbedded().getDtos();
					Set<Endereco> enderecos = EnderecoMapeamento
							.paraDominios(dtos, mPessoa);
					mPessoa.getEnderecos().addAll(enderecos);
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
				Falha.tratar(mButtonPessoaPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
				} else {
					mPessoaDTO = resposta.body();
					mPessoa = PessoaMapeamento.paraDominio(mPessoaDTO);
					consumirPessoaGETEnderecos();
					consumirPessoaGETTelefones();
					consumirPessoaGETEmails();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				mInputId.getText().clear();
			}
		};
	}

	private ColecaoCallback<Resposta<TelefoneDTO>> callbackPessoaGETTelefones() {
		return new ColecaoCallback<Resposta<TelefoneDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Resposta<TelefoneDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Resposta<TelefoneDTO>> chamada,
			                       @NonNull Response<Resposta<TelefoneDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
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
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
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
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
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
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
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
				Falha.tratar(mButtonPessoaPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonPessoaPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					telefone.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
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

	private void consumirPessoaGETPorId(@NonNull View view) {
		if (!TextUtils.isEmpty(mInputId.getText())) {
			BigInteger id = new BigInteger(mInputId.getText().toString());
			if (id.compareTo(BigInteger.ONE) < 0) {
				--mTarefasPendentes;
				mLabelId.setError(getString(R.string.input_invalido));
				mInputId.requestFocus();
			} else {
				mLabelId.setError(null);
				mLabelId.setErrorEnabled(false);
				Teclado.ocultar(getActivity(), view);
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
		mPessoaDTO = PessoaMapeamento.paraDTO(mPessoa);
		++mTarefasPendentes;
		PessoaRequisicao.post(callbackPessoaPOST(), mPessoaDTO);
	}

	private void consumirPessoaPOSTEmails() {
		if (mPessoa.getEmails() != null) {
			for (Email email : mPessoa.getEmails()) {
				EmailDTO dto = EmailMapeamento.paraDTO(email);
				mTarefasPendentes++;
				EmailRequisicao.post(callbackPessoaPOSTEmail(email), dto);
			}
		}
	}

	private void consumirPessoaPOSTEnderecos() {
		if (mPessoa.getEnderecos() != null) {
			for (Endereco endereco : mPessoa.getEnderecos()) {
				EnderecoDTO dto = EnderecoMapeamento.paraDTO(endereco);
				mTarefasPendentes++;
				EnderecoRequisicao
						.post(callbackPessoaPOSTEndereco(endereco), dto);
			}
		}
	}

	private void consumirPessoaPOSTTelefones() {
		if (mPessoa.getTelefones() != null) {
			for (Telefone telefone : mPessoa.getTelefones()) {
				TelefoneDTO dto = TelefoneMapeamento.paraDTO(telefone);
				mTarefasPendentes++;
				TelefoneRequisicao
						.post(callbackPessoaPOSTTelefone(telefone), dto);
			}
		}
	}

	private void consumirPessoaPUT() {
		// TODO: 8/23/17 implementar PUT
		Toast.makeText(getActivity(), "Implementar PUT", Toast.LENGTH_SHORT)
				.show();
	}

	private void consumirPorId(@NonNull View view) {
		view.setVisibility(View.INVISIBLE);
		exibirProgresso(mProgressBarConsumirPorId, null);
		consumirPessoaGETPorId(view);
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
				.show(getFragmentManager(), "Calendário");
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private void habilitarInputId(boolean habilitar) {
		mInputId.setEnabled(habilitar);
		mButtonConsumirPorId.setImageResource(habilitar
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
	}

	private void iterarFormulario() throws ParseException {
		mPessoa.setTipo((Pessoa.Tipo) mSpinnerTipo.getSelectedItem());
		mPessoa.setCnpjCpf(mInputCnpjCpf.getText().toString());
		mPessoa.setNomeRazao(mInputNomeRazao.getText().toString());
		mPessoa.setFantasiaSobrenome(mInputFantasiaSobrenome.getText().toString());
		mPessoa.setIeRg(mInputIeRg.getText().toString());
		mPessoa.setAberturaNascimento(TextUtils.isEmpty(mInputAberturaNascimento.getText())
				? null
				: Data.deStringData(mInputAberturaNascimento.getText().toString()));
		mPessoa.setPerfilCliente(mSwitchPerfilCliente.isChecked());
		mPessoa.setPerfilFornecedor(mSwitchPerfilFornecedor.isChecked());
		mPessoa.setPerfilTransportador(mSwitchPerfilTransportador.isChecked());
		mPessoa.setPerfilUsuario(mPessoa.getTipo().equals(Pessoa.Tipo.FISICA)
				&& mSwitchPerfilUsuario.isChecked());
		mPessoa.setSituacao(mSwitchSituacao.isChecked()
				? Pessoa.Situacao.ATIVO : Pessoa.Situacao.INATIVO);
	}

	private void limparFormulario() {
		mPessoa = Pessoa.builder().build();
		habilitarInputId(true);
		mInputId.getText().clear();
		mSpinnerTipo.setSelection(0);
		mInputCnpjCpf.getText().clear();
		mInputNomeRazao.getText().clear();
		mInputFantasiaSobrenome.getText().clear();
		mInputIeRg.getText().clear();
		mInputAberturaNascimento.getText().clear();
		preencherViewsDeLista();
		mSwitchPerfilCliente.setChecked(mPessoa.isPerfilCliente());
		mSwitchPerfilFornecedor.setChecked(mPessoa.isPerfilFornecedor());
		mSwitchPerfilTransportador.setChecked(mPessoa.isPerfilTransportador());
		mSwitchPerfilUsuario.setChecked(mPessoa.isPerfilUsuario());
		mSwitchSituacao
				.setChecked(mPessoa.getSituacao().equals(Pessoa.Situacao.ATIVO));
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
		mInputId.requestFocus();
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPessoa != null && mPessoa.getId() != null) {
				preencherFormulario();
			}
		}
	}

	private void preencherFormulario() {
		habilitarInputId(false);
		mInputId.setText(String.format(Locale.getDefault(), "%d", mPessoa.getId()));
		mSpinnerTipo.setSelection(mPessoa.getTipo().ordinal());
		mInputCnpjCpf.setText(mPessoa.getCnpjCpf());
		mInputNomeRazao.setText(mPessoa.getNomeRazao());
		mInputFantasiaSobrenome.setText(mPessoa.getFantasiaSobrenome() == null
				? "" : mPessoa.getFantasiaSobrenome());
		mInputIeRg.setText(mPessoa.getIeRg());
		mInputAberturaNascimento.setText(mPessoa.getAberturaNascimento() == null
				? "" : Data.paraStringData(mPessoa.getAberturaNascimento()));
		preencherViewsDeLista();
		mSwitchPerfilCliente.setChecked(mPessoa.isPerfilCliente());
		mSwitchPerfilFornecedor.setChecked(mPessoa.isPerfilFornecedor());
		mSwitchPerfilTransportador.setChecked(mPessoa.isPerfilTransportador());
		mSwitchPerfilUsuario.setChecked(mPessoa.isPerfilUsuario());
		mSwitchSituacao
				.setChecked(mPessoa.getSituacao().equals(Pessoa.Situacao.ATIVO));
		mInputCnpjCpf.requestFocus();
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

	private void submeterFormulario(@NonNull View view) {
		Teclado.ocultar(getActivity(), view);
		exibirProgresso(mProgressBarSalvar, mButtonSalvar);
		try {
			iterarFormulario();
		} catch (ParseException e) {
			--mTarefasPendentes;
			ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
			Snackbar.make(mButtonPessoaPesquisa, getString(R.string.suporte_mensagem_conversao_data), Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		if (mPessoa.getId() == null) {
			consumirPessoaPOST();
		} else {
			consumirPessoaPUT();
		}
		ocultarProgresso(mProgressBarSalvar, mButtonSalvar);
	}

	private AdapterView.OnItemSelectedListener tipoSelectedListener() {
		return new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
			                           int posicao, long id) {
				mPessoa.setTipo((Pessoa.Tipo) parent.getItemAtPosition(posicao));
				alternarFormulario(mPessoa.getTipo());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				/*
				 */
			}
		};
	}

	public interface OnListClickListener {

		void onEmailsClick(Set<Email> emails);

		void onEnderecosClick(Set<Endereco> enderecos);

		void onTelefonesClick(Set<Telefone> telefones);
	}
}
