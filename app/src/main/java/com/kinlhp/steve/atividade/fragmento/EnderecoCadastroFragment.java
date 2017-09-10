package com.kinlhp.steve.atividade.fragmento;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnderecoCadastroFragment extends Fragment
		implements View.OnClickListener, TextView.OnEditorActionListener,
		Serializable {
	private static final long serialVersionUID = 4826434755909163950L;
	private static final String ENDERECO = "endereco";
	private AdaptadorSpinner<Endereco.Tipo> mAdaptadorTipo;
	private AdaptadorSpinner<Uf.Sigla> mAdaptadorUf;
	private Endereco mEndereco;
	private OnEnderecoAddedListener mEnderecoAddedListener;
	private int mTarefasPendentes;
	private ArrayList<Endereco.Tipo> mTipos =
			new ArrayList<>(Arrays.asList(Endereco.Tipo.values()));
	private ArrayList<Uf.Sigla> mUfs =
			new ArrayList<>(Arrays.asList(Uf.Sigla.values()));

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirCep;
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
	private ProgressBar mProgressBarConsumirCep;
	private ProgressBar mProgressBarConsumirUfPorSigla;
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
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnEnderecoAddedListener) {
			mEnderecoAddedListener = (OnEnderecoAddedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnEnderecoAddedListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					iterarFormulario();
					mEnderecoAddedListener.onEnderecoAdded(mEndereco);
				} else {
					((ScrollView) getActivity().findViewById(R.id.fragment_endereco_cadastro))
							.fullScroll(View.FOCUS_UP);
					Snackbar.make(mButtonAdicionar, getString(R.string.form_mensagem_invalido), Snackbar.LENGTH_LONG)
							.show();
				}
				break;
			case R.id.button_consumir_cep:
				mTarefasPendentes = 0;
				consumirEnderecamentoGETPorCep(view);
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEndereco = getArguments() != null
				? (Endereco) getArguments().getSerializable(ENDERECO)
				: Endereco.builder().build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_endereco_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirCep = view.findViewById(R.id.button_consumir_cep);
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
		mProgressBarConsumirCep = view
				.findViewById(R.id.progress_bar_consumir_cep);
		mProgressBarConsumirUfPorSigla = view
				.findViewById(R.id.progress_bar_consumir_uf);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);
		mSpinnerUf = view.findViewById(R.id.spinner_uf);

		mAdaptadorTipo = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipo);

		mAdaptadorUf = new AdaptadorSpinner<>(getActivity(), mUfs);
		mSpinnerUf.setAdapter(mAdaptadorUf);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirCep.setOnClickListener(this);
		mInputBairro.setOnFocusChangeListener(bairroFocusChangeListener());
		mInputCep.setOnFocusChangeListener(cepFocusChangeListener());
		mInputCidade.setOnFocusChangeListener(cidadeFocusChangeListener());
		mInputLogradouro
				.setOnFocusChangeListener(logradouroFocusChangeListener());
		mInputNumero.setOnFocusChangeListener(numeroFocusChangeListener());
		mSpinnerUf.setOnItemSelectedListener(ufSelectedListener());

		limitarTiposDisponiveis();
		preencherFormulario();
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mEnderecoAddedListener = null;
	}

	@Override
	public boolean onEditorAction(TextView view, int id, KeyEvent event) {
		switch (id) {
			case EditorInfo.IME_ACTION_SEARCH:
				mButtonConsumirCep.performClick();
				break;
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.endereco_cadastro_titulo);
	}

	private View.OnFocusChangeListener bairroFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isBairroValido()) {
						mLabelBairro.setError(null);
						mLabelBairro.setErrorEnabled(false);
					}
				}
			}
		};
	}

	private Callback<EnderecamentoDTO> callbackEnderecamentoGETPorCep() {
		return new Callback<EnderecamentoDTO>() {

			@Override
			public void onFailure(@NonNull Call<EnderecamentoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirCep, mButtonConsumirCep);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<EnderecamentoDTO> chamada,
			                       @NonNull Response<EnderecamentoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					EnderecamentoDTO dto = resposta.body();
					mInputCep.requestFocus();
					mInputCep.setText(dto.getCep() == null
							? "" : dto.getCep().replace("-", ""));
					mInputLogradouro.requestFocus();
					mInputLogradouro.setText(dto.getLogradouro() == null
							? "" : dto.getLogradouro());
					mInputComplemento.requestFocus();
					mInputComplemento.setText(dto.getComplemento() == null
							? "" : dto.getComplemento());
					mInputBairro.requestFocus();
					mInputBairro.setText(dto.getBairro() == null
							? "" : dto.getBairro());
					mInputCidade.requestFocus();
					mInputCidade.setText(dto.getLocalidade() == null
							? "" : dto.getLocalidade());
					Uf.Sigla sigla = Uf.Sigla.valueOf(dto.getUf().name());
					mSpinnerUf.setSelection(dto.getUf() == null
							? 0 : mAdaptadorUf.getPosition(sigla));
					mInputNumero.getText().clear();
					mInputNumero.requestFocus();
				}
				ocultarProgresso(mProgressBarConsumirCep, mButtonConsumirCep);
			}
		};
	}

	private ItemCallback<UfDTO> callbackUfGETPorSigla() {
		return new ItemCallback<UfDTO>() {

			@Override
			public void onFailure(@NonNull Call<UfDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirUfPorSigla, mSpinnerUf);
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
							.setSelection(mAdaptadorUf.getPosition(mEndereco.getUf().getSigla()));
				}
				ocultarProgresso(mProgressBarConsumirUfPorSigla, mSpinnerUf);
			}
		};
	}

	private View.OnFocusChangeListener cepFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isCepValido()) {
						mLabelCep.setError(null);
						mLabelCep.setErrorEnabled(false);
					}
				}
			}
		};
	}

	private View.OnFocusChangeListener cidadeFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isCidadeValido()) {
						mLabelCidade.setError(null);
						mLabelCidade.setErrorEnabled(false);
					}
				}
			}
		};
	}

	private void consumirEnderecamentoGETPorCep(@NonNull View view) {
		view.setVisibility(View.INVISIBLE);
		exibirProgresso(mProgressBarConsumirCep, null);
		if (isCepValido()) {
			Teclado.ocultar(getActivity(), view);
			++mTarefasPendentes;
			String href = String
					.format(getString(R.string.requisicao_url_cep), mInputCep.getText().toString());
			EnderecamentoRequisicao
					.getPorCep(callbackEnderecamentoGETPorCep(), new HRef(href));
		}
		ocultarProgresso(mProgressBarConsumirCep, view);
	}

	private void consumirUfGETPorSigla(@NonNull Uf.Sigla sigla) {
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
		return true;
	}

	private boolean isCidadeValido() {
		if (TextUtils.isEmpty(mInputCidade.getText())) {
			mLabelCidade.setError(getString(R.string.input_obrigatorio));
			return false;
		}
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
		return true;
	}

	private boolean isNumeroValido() {
		if (TextUtils.isEmpty(mInputNumero.getText())) {
			mLabelNumero.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		return true;
	}

	private void iterarFormulario() {
		mEndereco.setTipo((Endereco.Tipo) mSpinnerTipo.getSelectedItem());
		mEndereco.setCep(mInputCep.getText().toString());
		mEndereco.setLogradouro(mInputLogradouro.getText().toString());
		mEndereco.setNumero(mInputNumero.getText().toString());
		mEndereco.setComplemento(TextUtils.isEmpty(mInputComplemento.getText())
				? null : mInputComplemento.getText().toString());
		mEndereco.setComplemento2(TextUtils.isEmpty(mInputComplemento2.getText())
				? null : mInputComplemento2.getText().toString());
		mEndereco.setBairro(mInputBairro.getText().toString());
		mEndereco.setCidade(mInputCidade.getText().toString());
		mEndereco.setNomeContato(TextUtils.isEmpty(mInputNomeContato.getText())
				? null : mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Endereco endereco : mEndereco.getPessoa().getEnderecos()) {
			if (!endereco.equals(mEndereco)
					|| TextUtils.isEmpty(mEndereco.getNumero())) {
				mAdaptadorTipo.remove(endereco.getTipo());
			}
		}
		mAdaptadorTipo.notifyDataSetChanged();
	}

	private View.OnFocusChangeListener logradouroFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isLogradouroValido()) {
						mLabelLogradouro.setError(null);
						mLabelLogradouro.setErrorEnabled(false);
					}
				}
			}
		};
	}

	private View.OnFocusChangeListener numeroFocusChangeListener() {
		return new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (!focused) {
					if (isNumeroValido()) {
						mLabelNumero.setError(null);
						mLabelNumero.setErrorEnabled(false);
					}
				}
			}
		};
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
		mSpinnerTipo.setSelection(mEndereco.getTipo() == null
				? 0 : mAdaptadorTipo.getPosition(mEndereco.getTipo()));
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
				? 0 : mAdaptadorUf.getPosition(mEndereco.getUf().getSigla()));
		mInputNomeContato.setText(mEndereco.getNomeContato() == null
				? "" : mEndereco.getNomeContato());
		if (mEndereco.getPessoa().getEnderecos().contains(mEndereco)) {
			mButtonAdicionar
					.setHint(R.string.endereco_cadastro_button_alterar_hint);
		}

		// TODO: 9/9/17 corrigir essa gambiarra [problema com equals e hashCode]
			/*
			essa gambiarra foi necessária pois a validação acima não funciona
			quando o Tipo foi alterado, gerando assim inconsistência no hint do
			mButtonAdicionar.
			 */
		List<Endereco> enderecos =
				new ArrayList<>(mEndereco.getPessoa().getEnderecos());
		int indice = enderecos.indexOf(mEndereco);
		if (indice > -1) {
			mButtonAdicionar
					.setHint(R.string.endereco_cadastro_button_alterar_hint);
		}
	}

	private AdapterView.OnItemSelectedListener ufSelectedListener() {
		return new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
			                           int position, long id) {
				Teclado.ocultar(getActivity(), parent);
				mTarefasPendentes = 0;
				parent.setVisibility(View.INVISIBLE);
				exibirProgresso(mProgressBarConsumirUfPorSigla, null);
				consumirUfGETPorSigla((Uf.Sigla) parent.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				/*
				 */
			}
		};
	}

	public interface OnEnderecoAddedListener {
		void onEnderecoAdded(Endereco endereco);
	}
}
