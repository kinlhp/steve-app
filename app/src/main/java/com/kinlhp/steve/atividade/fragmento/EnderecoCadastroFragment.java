package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Uf;
import com.kinlhp.steve.dto.EnderecamentoDTO;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.dto.UfDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.EnderecoMapeamento;
import com.kinlhp.steve.mapeamento.UfMapeamento;
import com.kinlhp.steve.requisicao.EnderecamentoRequisicao;
import com.kinlhp.steve.requisicao.EnderecoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.UfRequisicao;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class EnderecoCadastroFragment extends Fragment
		implements View.OnClickListener, TextView.OnEditorActionListener,
		View.OnFocusChangeListener, AdapterView.OnItemSelectedListener,
		Serializable {
	private static final long serialVersionUID = -5512378900845349680L;
	private static final String ENDERECO = "endereco";
	private static final String ENDERECO_AUXILIAR = "enderecoAuxiliar";
	private AdaptadorSpinner<Endereco.Tipo> mAdaptadorTipos;
	private AdaptadorSpinner<Uf.Sigla> mAdaptadorUfs;
	private Endereco mEndereco;
	private OnEnderecoAdicionadoListener mOnEnderecoAdicionadoListener;
	private Endereco mEnderecoAuxiliar;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;
	private ArrayList<Endereco.Tipo> mTipos;
	private ArrayList<Uf.Sigla> mUfs;

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirPorCep;
	private TextInputEditText mInputBairro;
	private TextInputEditText mInputCep;
	private TextInputEditText mInputCidade;
	private TextInputEditText mInputComplemento;
	private TextInputEditText mInputComplemento2;
	private TextInputEditText mInputLogradouro;
	private TextInputEditText mInputNomeContato;
	private TextInputEditText mInputNumero;
	private TextInputLayout mLabelBairro;
	private TextInputLayout mLabelCep;
	private TextInputLayout mLabelCidade;
	private TextInputLayout mLabelLogradouro;
	private TextInputLayout mLabelNumero;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirPorCep;
	private ProgressBar mProgressBarConsumirUf;
	private ScrollView mScrollEnderecoCadastro;
	private AppCompatSpinner mSpinnerTipo;
	private AppCompatSpinner mSpinnerUf;

	/**
	 * Construtor padrão é obrigatório
	 */
	public EnderecoCadastroFragment() {
	}

	public static EnderecoCadastroFragment newInstance(@NonNull Endereco endereco) {
		EnderecoCadastroFragment fragmento = new EnderecoCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(ENDERECO, endereco);
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
					mScrollEnderecoCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.button_consumir_por_cep:
				consumirEnderecamentoGETPorCep(view);
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mEnderecoAuxiliar = (Endereco) savedInstanceState
					.getSerializable(ENDERECO_AUXILIAR);
		}
		if (getArguments() != null) {
			mEndereco = (Endereco) getArguments().getSerializable(ENDERECO);
		}
		if (mEnderecoAuxiliar == null) {
			mEnderecoAuxiliar = Endereco.builder().tipo(null).build();
			transcreverEndereco();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_endereco_cadastro, container, false);
		mScrollEnderecoCadastro = (ScrollView) view;
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirPorCep = view.findViewById(R.id.button_consumir_por_cep);
		mInputBairro = view.findViewById(R.id.input_bairro);
		mInputCep = view.findViewById(R.id.input_cep);
		mInputCidade = view.findViewById(R.id.input_cidade);
		mInputComplemento = view.findViewById(R.id.input_complemento);
		mInputComplemento2 = view.findViewById(R.id.input_complemento2);
		mInputLogradouro = view.findViewById(R.id.input_logradouro);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mInputNumero = view.findViewById(R.id.input_numero);
		mLabelBairro = view.findViewById(R.id.label_bairro);
		mLabelCep = view.findViewById(R.id.label_cep);
		mLabelCidade = view.findViewById(R.id.label_cidade);
		mLabelLogradouro = view.findViewById(R.id.label_logradouro);
		mLabelNumero = view.findViewById(R.id.label_numero);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirPorCep = view
				.findViewById(R.id.progress_bar_consumir_por_cep);
		mProgressBarConsumirUf = view
				.findViewById(R.id.progress_bar_consumir_uf);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);
		mSpinnerUf = view.findViewById(R.id.spinner_uf);

		mTipos = new ArrayList<>(Arrays.asList(Endereco.Tipo.values()));
		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mUfs = new ArrayList<>(Arrays.asList(Uf.Sigla.values()));
		mAdaptadorUfs = new AdaptadorSpinner<>(getActivity(), mUfs);
		mSpinnerUf.setAdapter(mAdaptadorUfs);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirPorCep.setOnClickListener(this);
		mInputBairro.setOnFocusChangeListener(this);
		mInputCep.setOnEditorActionListener(this);
		mInputCep.setOnFocusChangeListener(this);
		mInputCidade.setOnFocusChangeListener(this);
		mInputLogradouro.setOnFocusChangeListener(this);
		mInputNumero.setOnFocusChangeListener(this);
		mSpinnerUf.setOnItemSelectedListener(this);

		mInputCep.requestFocus();
		mScrollEnderecoCadastro.fullScroll(View.FOCUS_UP);

		return view;
	}

	@Override
	public boolean onEditorAction(TextView view, int id, KeyEvent event) {
		switch (id) {
			case EditorInfo.IME_ACTION_SEARCH:
				mButtonConsumirPorCep.performClick();
				break;
		}
		return false;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_bairro:
				if (!focused) {
					isBairroValido();
				}
				break;
			case R.id.input_cep:
				if (!focused) {
					isCepValido();
				}
				break;
			case R.id.input_cidade:
				if (!focused) {
					isCidadeValido();
				}
				break;
			case R.id.input_logradouro:
				if (!focused) {
					isLogradouroValido();
				}
				break;
			case R.id.input_numero:
				if (!focused) {
					isNumeroValido();
				}
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	                           long id) {
		switch (parent.getId()) {
			case R.id.spinner_uf:
				consumirUfGETPorSigla((Uf.Sigla) parent.getSelectedItem());
				break;
		}
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
		getActivity().setTitle(R.string.endereco_cadastro_titulo);
		limitarTiposDisponiveis();
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ENDERECO, mEndereco);
		outState.putSerializable(ENDERECO_AUXILIAR, mEnderecoAuxiliar);
	}

	private void alternarButtonAdicionar() {
		// TODO: 9/15/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Endereco> enderecos =
				new ArrayList<>(mEndereco.getPessoa().getEnderecos());
		if (enderecos.contains(mEndereco)) {
			mButtonAdicionar.setHint(mEndereco.getId() == null
					? R.string.endereco_cadastro_button_alterar_hint
					: R.string.endereco_cadastro_button_salvar_hint);
		}
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(!mEndereco.getPessoa().isPerfilUsuario()
				|| credencialLogado.isPerfilAdministrador()
				|| credencialLogado.getFuncionario().getId().equals(mEndereco.getPessoa().getId())
				? View.VISIBLE : View.INVISIBLE);
	}

	private ItemCallback<EnderecamentoDTO> callbackEnderecamentoGETPorCep() {
		return new ItemCallback<EnderecamentoDTO>() {

			@Override
			public void onFailure(@NonNull Call<EnderecamentoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorCep, mButtonConsumirPorCep);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<EnderecamentoDTO> chamada,
			                       @NonNull Response<EnderecamentoDTO> resposta) {
				--mTarefasPendentes;
				if (resposta.isSuccessful()) {
					EnderecamentoDTO dto = resposta.body();
					if (!dto.isErro()) {
						preencherEnderecamento(dto);
					}
				}
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorCep, mButtonConsumirPorCep);
			}
		};
	}

	private VazioCallback callbackEnderecoPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					transcreverEnderecoAuxiliar();
					consumirEnderecoPUTUf();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackEnderecoPUTUf() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					transcreverUfEnderecoAuxiliar();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private ItemCallback<UfDTO> callbackUfGETPorSigla() {
		return new ItemCallback<UfDTO>() {

			@Override
			public void onFailure(@NonNull Call<UfDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirUf, mSpinnerUf);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<UfDTO> chamada,
			                       @NonNull Response<UfDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					UfDTO dto = resposta.body();
					Uf uf = UfMapeamento.paraDominio(dto);
					mEnderecoAuxiliar.setUf(uf);
				}
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirUf, mSpinnerUf);
			}
		};
	}

	private void consumirEnderecamentoGETPorCep(@NonNull View view) {
		mTarefasPendentes = 0;
		mPressionarVoltar = false;
		exibirProgresso(mProgressBarConsumirPorCep, null);
		view.setVisibility(View.INVISIBLE);
		if (isCepValido()) {
			Teclado.ocultar(getActivity(), view);
			String href = String
					.format(getString(R.string.requisicao_url_cep), mInputCep.getText().toString());
			++mTarefasPendentes;
			EnderecamentoRequisicao
					.getPorCep(callbackEnderecamentoGETPorCep(), new HRef(href));
		}
	}

	private void consumirEnderecoPUT() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		EnderecoDTO dto = EnderecoMapeamento.paraDTO(mEnderecoAuxiliar);
		++mTarefasPendentes;
		EnderecoRequisicao.put(callbackEnderecoPUT(), mEndereco.getId(), dto);
	}

	private void consumirEnderecoPUTUf() {
		// TODO: 9/17/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("ufs/%d");
		// TODO: 9/17/17 corrigir hard-coded
		RequestBody uriList = RequestBody
				.create(MediaType.parse("text/uri-list"), String.format(url, mEnderecoAuxiliar.getUf().getId()));
		++mTarefasPendentes;
		EnderecoRequisicao
				.putUf(callbackEnderecoPUTUf(), mEndereco.getId(), uriList);
	}

	private void consumirUfGETPorSigla(@NonNull Uf.Sigla sigla) {
		mTarefasPendentes = 0;
		mPressionarVoltar = false;
		exibirProgresso(mProgressBarConsumirUf, null);
		mSpinnerUf.setVisibility(View.INVISIBLE);
		Teclado.ocultar(getActivity(), mSpinnerUf);
		UfDTO.SiglaDTO siglaDTO = UfMapeamento.SiglaMapeamento.paraDTO(sigla);
		++mTarefasPendentes;
		UfRequisicao.getPorSigla(callbackUfGETPorSigla(), siglaDTO);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isBairroValido() {
		if (TextUtils.isEmpty(mInputBairro.getText())) {
			mLabelBairro.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelBairro.setError(null);
		mLabelBairro.setErrorEnabled(false);
		return true;
	}

	private boolean isCepValido() {
		if (TextUtils.isEmpty(mInputCep.getText())) {
			mLabelCep.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!TextUtils.isDigitsOnly(mInputCep.getText())
				|| TextUtils.getTrimmedLength(mInputCep.getText()) != 8) {
			mLabelCep.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelCep.setError(null);
		mLabelCep.setErrorEnabled(false);
		return true;
	}

	private boolean isCidadeValido() {
		if (TextUtils.isEmpty(mInputCidade.getText())) {
			mLabelCidade.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelCidade.setError(null);
		mLabelCidade.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isCepValido()
				&& isLogradouroValido()
				&& isNumeroValido()
				&& isBairroValido()
				&& isCidadeValido();
	}

	private boolean isLogradouroValido() {
		if (TextUtils.isEmpty(mInputLogradouro.getText())) {
			mLabelLogradouro.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelLogradouro.setError(null);
		mLabelLogradouro.setErrorEnabled(false);
		return true;
	}

	private boolean isNumeroValido() {
		if (TextUtils.isEmpty(mInputNumero.getText())) {
			mLabelNumero.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelNumero.setError(null);
		mLabelNumero.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mEnderecoAuxiliar
				.setTipo((Endereco.Tipo) mSpinnerTipo.getSelectedItem());
		mEnderecoAuxiliar.setCep(mInputCep.getText().toString());
		mEnderecoAuxiliar.setLogradouro(mInputLogradouro.getText().toString());
		mEnderecoAuxiliar.setNumero(mInputNumero.getText().toString());
		mEnderecoAuxiliar
				.setComplemento(mInputComplemento.getText().toString());
		mEnderecoAuxiliar
				.setComplemento2(mInputComplemento2.getText().toString());
		mEnderecoAuxiliar.setBairro(mInputBairro.getText().toString());
		mEnderecoAuxiliar.setCidade(mInputCidade.getText().toString());
		mEnderecoAuxiliar
				.setNomeContato(mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Endereco endereco : mEndereco.getPessoa().getEnderecos()) {
			if (!endereco.equals(mEndereco)
					|| TextUtils.isEmpty(mEndereco.getNumero())) {
				mAdaptadorTipos.remove(endereco.getTipo());
			}
		}
		mAdaptadorTipos.notifyDataSetChanged();
	}

	private void limparErros() {
		mLabelCep.setError(null);
		mLabelCep.setErrorEnabled(false);
		mLabelLogradouro.setError(null);
		mLabelLogradouro.setErrorEnabled(false);
		mLabelNumero.setError(null);
		mLabelNumero.setErrorEnabled(false);
		mLabelBairro.setError(null);
		mLabelBairro.setErrorEnabled(false);
		mLabelCidade.setError(null);
		mLabelCidade.setErrorEnabled(false);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPressionarVoltar) {
				if (mOnEnderecoAdicionadoListener != null) {
					mOnEnderecoAdicionadoListener
							.onEnderecoAdicionado(mButtonAdicionar, mEndereco);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherEnderecamento(@NonNull EnderecamentoDTO enderecamento) {
		mEnderecoAuxiliar.setIbge(enderecamento.getIbge());
		mInputCep.requestFocus();
		mInputCep.setText(enderecamento.getCep() == null
				? "" : enderecamento.getCep().replace("-", ""));
		mInputLogradouro.requestFocus();
		mInputLogradouro.setText(enderecamento.getLogradouro() == null
				? "" : enderecamento.getLogradouro());
		mInputComplemento2.setText(enderecamento.getComplemento() == null
				? "" : enderecamento.getComplemento());
		mInputBairro.requestFocus();
		mInputBairro.setText(enderecamento.getBairro() == null
				? "" : enderecamento.getBairro());
		mInputCidade.requestFocus();
		mInputCidade.setText(enderecamento.getLocalidade() == null
				? "" : enderecamento.getLocalidade());
		Uf.Sigla sigla = Uf.Sigla.valueOf(enderecamento.getUf().name());
		mSpinnerUf.setSelection(enderecamento.getUf() == null
				? 0 : mAdaptadorUfs.getPosition(sigla));
		mInputNumero.requestFocus();
		mScrollEnderecoCadastro.fullScroll(View.FOCUS_UP);
	}

	private void preencherFormulario() {
		limparErros();
		mSpinnerTipo.setSelection(mEnderecoAuxiliar.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mEnderecoAuxiliar.getTipo()));
		mInputCep.setText(mEnderecoAuxiliar.getCep() == null
				? "" : mEnderecoAuxiliar.getCep());
		mInputLogradouro.setText(mEnderecoAuxiliar.getLogradouro() == null
				? "" : mEnderecoAuxiliar.getLogradouro());
		mInputNumero.setText(mEnderecoAuxiliar.getNumero() == null
				? "" : mEnderecoAuxiliar.getNumero());
		mInputComplemento.setText(mEnderecoAuxiliar.getComplemento() == null
				? "" : mEnderecoAuxiliar.getComplemento());
		mInputComplemento2.setText(mEnderecoAuxiliar.getComplemento2() == null
				? "" : mEnderecoAuxiliar.getComplemento2());
		mInputBairro.setText(mEnderecoAuxiliar.getBairro() == null
				? "" : mEnderecoAuxiliar.getBairro());
		mInputCidade.setText(mEnderecoAuxiliar.getCidade() == null
				? "" : mEnderecoAuxiliar.getCidade());
		mSpinnerUf.setSelection(mEnderecoAuxiliar.getUf() == null
				? 0
				: mAdaptadorUfs.getPosition(mEnderecoAuxiliar.getUf().getSigla()));
		mInputNomeContato.setText(mEnderecoAuxiliar.getNomeContato() == null
				? "" : mEnderecoAuxiliar.getNomeContato());
		alternarButtonAdicionar();
	}

	public void setEndereco(@NonNull Endereco endereco) {
		mEndereco = endereco;
		if (getArguments() != null) {
			getArguments().putSerializable(ENDERECO, mEndereco);
		}
		if (mEnderecoAuxiliar != null) {
			transcreverEndereco();
		}
	}

	public void setOnEnderecoAdicionadoListener(@Nullable OnEnderecoAdicionadoListener ouvinte) {
		mOnEnderecoAdicionadoListener = ouvinte;
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mEndereco.getId() != null) {
			consumirEnderecoPUT();
		} else {
			transcreverEnderecoAuxiliar();
			transcreverUfEnderecoAuxiliar();
			if (mOnEnderecoAdicionadoListener != null) {
				mOnEnderecoAdicionadoListener
						.onEnderecoAdicionado(mButtonAdicionar, mEndereco);
			}
			getActivity().onBackPressed();
		}
	}

	private void transcreverEndereco() {
		mEnderecoAuxiliar.setBairro(mEndereco.getBairro());
		mEnderecoAuxiliar.setCep(mEndereco.getCep());
		mEnderecoAuxiliar.setCidade(mEndereco.getCidade());
		mEnderecoAuxiliar.setComplemento(mEndereco.getComplemento());
		mEnderecoAuxiliar.setComplemento2(mEndereco.getComplemento2());
		mEnderecoAuxiliar.setIbge(mEndereco.getIbge());
		mEnderecoAuxiliar.setId(mEndereco.getId());
		mEnderecoAuxiliar.setLogradouro(mEndereco.getLogradouro());
		mEnderecoAuxiliar.setNomeContato(mEndereco.getNomeContato());
		mEnderecoAuxiliar.setNumero(mEndereco.getNumero());
		mEnderecoAuxiliar.setPessoa(mEndereco.getPessoa());
		mEnderecoAuxiliar.setTipo(mEndereco.getTipo());
		mEnderecoAuxiliar.setUf(mEndereco.getUf());
	}

	private void transcreverEnderecoAuxiliar() {
		mEndereco.setBairro(mEnderecoAuxiliar.getBairro());
		mEndereco.setCep(mEnderecoAuxiliar.getCep());
		mEndereco.setCidade(mEnderecoAuxiliar.getCidade());
		mEndereco.setComplemento(mEnderecoAuxiliar.getComplemento());
		mEndereco.setComplemento2(mEnderecoAuxiliar.getComplemento2());
		mEndereco.setIbge(mEnderecoAuxiliar.getIbge());
		mEndereco.setLogradouro(mEnderecoAuxiliar.getLogradouro());
		mEndereco.setNomeContato(mEnderecoAuxiliar.getNomeContato());
		mEndereco.setNumero(mEnderecoAuxiliar.getNumero());
		mEndereco.setTipo(mEnderecoAuxiliar.getTipo());
	}

	private void transcreverUfEnderecoAuxiliar() {
		mEndereco.setUf(mEnderecoAuxiliar.getUf());
	}

	public interface OnEnderecoAdicionadoListener {

		void onEnderecoAdicionado(@NonNull View view,
		                          @NonNull Endereco endereco);
	}
}
