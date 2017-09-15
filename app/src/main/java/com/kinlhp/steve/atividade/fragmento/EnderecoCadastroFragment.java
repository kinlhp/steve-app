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
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Uf;
import com.kinlhp.steve.dto.EnderecamentoDTO;
import com.kinlhp.steve.dto.UfDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.UfMapeamento;
import com.kinlhp.steve.requisicao.EnderecamentoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.UfRequisicao;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

public class EnderecoCadastroFragment extends Fragment
		implements View.OnClickListener, TextView.OnEditorActionListener,
		View.OnFocusChangeListener, AdapterView.OnItemSelectedListener,
		Serializable {
	private static final long serialVersionUID = -8925798097971534682L;
	private static final String ENDERECO = "endereco";
	private AdaptadorSpinner<Endereco.Tipo> mAdaptadorTipos;
	private AdaptadorSpinner<Uf.Sigla> mAdaptadorUfs;
	private Endereco mEndereco;
	private OnEnderecoAdicionadoListener mOnEnderecoAdicionadoListener;
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
	private ProgressBar mProgressBarConsumirPorCep;
	private ProgressBar mProgressBarConsumirPorUf;
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
					iterarFormulario();
					if (mOnEnderecoAdicionadoListener != null) {
						mOnEnderecoAdicionadoListener
								.onEnderecoAdicionado(view, mEndereco);
					}
					getActivity().onBackPressed();
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
			mEndereco = (Endereco) savedInstanceState.getSerializable(ENDERECO);
		} else if (getArguments() != null) {
			mEndereco = (Endereco) getArguments().getSerializable(ENDERECO);
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
		mProgressBarConsumirPorCep = view
				.findViewById(R.id.progress_bar_consumir_por_cep);
		mProgressBarConsumirPorUf = view
				.findViewById(R.id.progress_bar_consumir_por_uf);
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
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.endereco_cadastro_titulo);
		limitarTiposDisponiveis();
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		iterarFormulario();
		outState.putSerializable(ENDERECO, mEndereco);
	}

	private ItemCallback<EnderecamentoDTO> callbackEnderecamentoGETPorCep() {
		return new ItemCallback<EnderecamentoDTO>() {

			@Override
			public void onFailure(@NonNull Call<EnderecamentoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
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
				ocultarProgresso(mProgressBarConsumirPorCep, mButtonConsumirPorCep);
			}
		};
	}

	private ItemCallback<UfDTO> callbackUfGETPorSigla() {
		return new ItemCallback<UfDTO>() {

			@Override
			public void onFailure(@NonNull Call<UfDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorUf, mSpinnerUf);
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
					mEndereco.setUf(uf);
					mSpinnerUf
							.setSelection(mAdaptadorUfs.getPosition(mEndereco.getUf().getSigla()));
				}
				ocultarProgresso(mProgressBarConsumirPorUf, mSpinnerUf);
			}
		};
	}

	private void consumirEnderecamentoGETPorCep(@NonNull View view) {
		mTarefasPendentes = 0;
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
		ocultarProgresso(mProgressBarConsumirPorCep, view);
	}

	private void consumirUfGETPorSigla(@NonNull Uf.Sigla sigla) {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarConsumirPorUf, null);
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
		mEndereco.setTipo((Endereco.Tipo) mSpinnerTipo.getSelectedItem());
		mEndereco.setCep(mInputCep.getText().toString());
		mEndereco.setLogradouro(mInputLogradouro.getText().toString());
		mEndereco.setNumero(mInputNumero.getText().toString());
		mEndereco.setComplemento(mInputComplemento.getText().toString());
		mEndereco.setComplemento2(mInputComplemento2.getText().toString());
		mEndereco.setBairro(mInputBairro.getText().toString());
		mEndereco.setCidade(mInputCidade.getText().toString());
		mEndereco.setNomeContato(mInputNomeContato.getText().toString());
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

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
		}
	}

	private void preencherEnderecamento(@NonNull EnderecamentoDTO enderecamento) {
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
	}

	private void preencherFormulario() {
		mSpinnerTipo.setSelection(mEndereco.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mEndereco.getTipo()));
		mInputCep.setText(mEndereco.getCep() == null ? "" : mEndereco.getCep());
		mInputLogradouro.setText(mEndereco.getLogradouro() == null
				? "" : mEndereco.getLogradouro());
		mInputNumero.setText(mEndereco.getNumero() == null
				? "" : mEndereco.getNumero());
		mInputComplemento.setText(mEndereco.getComplemento() == null
				? "" : mEndereco.getComplemento());
		mInputComplemento2.setText(mEndereco.getComplemento2() == null
				? "" : mEndereco.getComplemento2());
		mInputBairro.setText(mEndereco.getBairro() == null
				? "" : mEndereco.getBairro());
		mInputCidade.setText(mEndereco.getCidade() == null
				? "" : mEndereco.getCidade());
		mSpinnerUf.setSelection(mEndereco.getUf() == null
				? 0 : mAdaptadorUfs.getPosition(mEndereco.getUf().getSigla()));
		mInputNomeContato.setText(mEndereco.getNomeContato() == null
				? "" : mEndereco.getNomeContato());
		if (mEndereco.getPessoa().getEnderecos().contains(mEndereco)) {
			mButtonAdicionar
					.setHint(R.string.endereco_cadastro_button_alterar_hint);
		}
		mInputCep.requestFocus();
	}

	public void setEndereco(@NonNull Endereco endereco) {
		mEndereco = endereco;
		if (getArguments() != null) {
			getArguments().putSerializable(ENDERECO, mEndereco);
		}
	}

	public void setOnEnderecoAdicionadoListener(@Nullable OnEnderecoAdicionadoListener ouvinte) {
		mOnEnderecoAdicionadoListener = ouvinte;
	}

	public interface OnEnderecoAdicionadoListener {

		void onEnderecoAdicionado(@NonNull View view,
		                          @NonNull Endereco endereco);
	}
}
