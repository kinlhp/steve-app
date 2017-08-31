package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.SpinnerAdaptador;
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

public class PessoaCadastroActivity extends AppCompatActivity
		implements View.OnClickListener, TextView.OnEditorActionListener,
		View.OnLongClickListener, Serializable {
	private static final long serialVersionUID = 8217023115403063255L;
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
	private Pessoa mPessoa = Pessoa.builder().build();
	private PessoaDTO mPessoaDTO;
	private ProgressBar mProgressBarConsumirPorId;
	private ProgressBar mProgressBarSalvar;
	private AppCompatSpinner mSpinnerTipo;
	private SwitchCompat mSwitchPerfilCliente;
	private SwitchCompat mSwitchPerfilFornecedor;
	private SwitchCompat mSwitchPerfilTransportador;
	private SwitchCompat mSwitchPerfilUsuario;
	private SwitchCompat mSwitchSituacao;
	private int mTarefasPendentes;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_consumir_por_id:
				mTarefasPendentes = 0;
				clickConsumirPorId(view);
				break;
			case R.id.button_pessoa_pesquisa:
				clickPessoaPesquisa(view);
				break;
			case R.id.button_salvar:
				mTarefasPendentes = 0;
				clickSalvar(view);
				break;
			case R.id.input_abertura_nascimento:
				clickCalendario();
				break;
			case R.id.input_emails:
				Toast.makeText(this, "Implementar", Toast.LENGTH_SHORT).show();
				break;
			case R.id.input_enderecos:
				Toast.makeText(this, "Implementar", Toast.LENGTH_SHORT).show();
				break;
			case R.id.input_telefones:
				Toast.makeText(this, "Implementar", Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pessoa_cadastro);

		Toolbar toolbar = findViewById(R.id.toolbar_pessoa_cadastro);
		mButtonConsumirPorId = findViewById(R.id.button_consumir_por_id);
		mButtonPessoaPesquisa = findViewById(R.id.button_pessoa_pesquisa);
		mButtonSalvar = findViewById(R.id.button_salvar);
		mInputAberturaNascimento = findViewById(R.id.input_abertura_nascimento);
		mInputCnpjCpf = findViewById(R.id.input_cnpj_cpf);
		mInputEmails = findViewById(R.id.input_emails);
		mInputEnderecos = findViewById(R.id.input_enderecos);
		mInputFantasiaSobrenome = findViewById(R.id.input_fantasia_sobrenome);
		mInputId = findViewById(R.id.input_id);
		mInputIeRg = findViewById(R.id.input_ie_rg);
		mInputNomeRazao = findViewById(R.id.input_nome_razao);
		mInputTelefones = findViewById(R.id.input_telefones);
		mLabelAberturaNascimento = findViewById(R.id.label_abertura_nascimento);
		mLabelCnpjCpf = findViewById(R.id.label_cnpj_cpf);
		mLabelEmails = findViewById(R.id.label_emails);
		mLabelEnderecos = findViewById(R.id.label_enderecos);
		mLabelFantasiaSobrenome = findViewById(R.id.label_fantasia_sobrenome);
		mLabelId = findViewById(R.id.label_id);
		mLabelIeRg = findViewById(R.id.label_ie_rg);
		mLabelNomeRazao = findViewById(R.id.label_nome_razao);
		mLabelTelefones = findViewById(R.id.label_telefones);
		mProgressBarConsumirPorId =
				findViewById(R.id.progress_bar_consumir_por_id);
		mProgressBarSalvar = findViewById(R.id.progress_bar_salvar);
		mSpinnerTipo = findViewById(R.id.spinner_tipo);
		mSwitchPerfilCliente = findViewById(R.id.switch_perfil_cliente);
		mSwitchPerfilFornecedor = findViewById(R.id.switch_perfil_fornecedor);
		mSwitchPerfilTransportador =
				findViewById(R.id.switch_perfil_transportador);
		mSwitchPerfilUsuario = findViewById(R.id.switch_perfil_usuario);
		mSwitchSituacao = findViewById(R.id.switch_situacao);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		List<Pessoa.Tipo> tipos = Arrays
				.asList(Pessoa.Tipo.FISICA, Pessoa.Tipo.JURIDICA);
		mSpinnerTipo.setAdapter(new SpinnerAdaptador<>(this, tipos));

		mButtonConsumirPorId.setOnClickListener(this);
		mButtonPessoaPesquisa.setOnClickListener(this);
		mButtonSalvar.setOnClickListener(this);
		mInputAberturaNascimento.setOnClickListener(this);
		mInputAberturaNascimento.setOnLongClickListener(this);
		mInputEmails.setOnClickListener(this);
		mInputEnderecos.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputTelefones.setOnClickListener(this);
		mSpinnerTipo.setOnItemSelectedListener(tipoSelectedListener());
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
					// TODO: 8/27/17 consumir e-mails, endereços e telefones aqui pois estes precisam do id da pessoa
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

	private void clickCalendario() {
		Date data = new Date(System.currentTimeMillis());
		if (!TextUtils.isEmpty(mInputAberturaNascimento.getText())) {
			try {
				data = Data
						.deStringData(mInputAberturaNascimento.getText().toString());
			} catch (ParseException e) {
				Toast.makeText(this, getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
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
				.show(getSupportFragmentManager(), "Calendário");
	}

	private void clickConsumirPorId(@NonNull View view) {
		view.setVisibility(View.INVISIBLE);
		exibirProgresso(mProgressBarConsumirPorId, null);
		// TODO: 8/29/17 tratar em PUT, pois o id será != null
		if (mPessoa.getId() == null) {
			consumirPessoaGETPorId(view);
		} else {
			mTarefasPendentes = 0;
			limparFormulario();
		}
	}

	private void clickPessoaPesquisa(@NonNull View view) {
		Snackbar.make(view, "Pesquisar pessoas", Snackbar.LENGTH_LONG)
				.show();
	}

	private void clickSalvar(@NonNull View view) {
		Teclado.ocultar(this, view);
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
				mInputId.requestFocus(View.FOCUS_UP);
			} else {
				mLabelId.setError(null);
				mLabelId.setErrorEnabled(false);
				Teclado.ocultar(this, view);
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
		Toast.makeText(this, "Implementar PUT", Toast.LENGTH_SHORT).show();
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

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
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
		mButtonConsumirPorId
				.setImageResource(R.drawable.ic_consumir_por_id_accent_24dp);
		mInputId.setEnabled(true);
		mInputId.getText().clear();
		mSpinnerTipo.setSelection(0);
		mInputCnpjCpf.getText().clear();
		mInputNomeRazao.getText().clear();
		mInputFantasiaSobrenome.getText().clear();
		mInputIeRg.getText().clear();
		mInputAberturaNascimento.getText().clear();
		mInputEnderecos.getText().clear();
		mInputTelefones.getText().clear();
		mInputEmails.getText().clear();
		mLabelEnderecos
				.setHint(getString(R.string.pessoa_cadastro_label_nenhum_endereco_hint));
		mLabelTelefones
				.setHint(getString(R.string.pessoa_cadastro_label_nenhum_telefone_hint));
		mLabelEmails
				.setHint(getString(R.string.pessoa_cadastro_label_nenhum_email_hint));
		mSwitchPerfilCliente.setChecked(mPessoa.isPerfilCliente());
		mSwitchPerfilFornecedor.setChecked(mPessoa.isPerfilFornecedor());
		mSwitchPerfilTransportador.setChecked(mPessoa.isPerfilTransportador());
		mSwitchPerfilUsuario.setChecked(mPessoa.isPerfilUsuario());
		mSwitchSituacao
				.setChecked(mPessoa.getSituacao().equals(Pessoa.Situacao.ATIVO));
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
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
		mButtonConsumirPorId
				.setImageResource(R.drawable.ic_borracha_accent_24dp);
		mInputId.setEnabled(false);
		mInputId.setText(String.format(Locale.getDefault(), "%d", mPessoa.getId()));
		mSpinnerTipo.setSelection(mPessoa.getTipo().ordinal());
		mInputCnpjCpf.setText(mPessoa.getCnpjCpf());
		mInputNomeRazao.setText(mPessoa.getNomeRazao());
		mInputFantasiaSobrenome.setText(mPessoa.getFantasiaSobrenome() == null
				? "" : mPessoa.getFantasiaSobrenome());
		mInputIeRg.setText(mPessoa.getIeRg());
		mInputAberturaNascimento.setText(mPessoa.getAberturaNascimento() == null
				? "" : Data.paraStringData(mPessoa.getAberturaNascimento()));
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
		mSwitchPerfilCliente.setChecked(mPessoa.isPerfilCliente());
		mSwitchPerfilFornecedor.setChecked(mPessoa.isPerfilFornecedor());
		mSwitchPerfilTransportador.setChecked(mPessoa.isPerfilTransportador());
		mSwitchPerfilUsuario.setChecked(mPessoa.isPerfilUsuario());
		mSwitchSituacao
				.setChecked(mPessoa.getSituacao().equals(Pessoa.Situacao.ATIVO));
		mInputCnpjCpf.requestFocus();
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
}
