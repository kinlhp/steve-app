package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.FormaPagamentoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CondicaoPagamentoMapeamento;
import com.kinlhp.steve.mapeamento.FormaPagamentoMapeamento;
import com.kinlhp.steve.requisicao.CondicaoPagamentoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.FormaPagamentoRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class FormaPagamentoCadastroFragment extends Fragment
		implements View.OnClickListener, TextView.OnEditorActionListener,
		View.OnFocusChangeListener, Serializable {
	private static final long serialVersionUID = 7793503408292639636L;
	private static final String FORMA_PAGAMENTO = "formaPagamento";
	private static final String FORMA_PAGAMENTO_AUXILIAR = "formaPagamentoAuxiliar";
	private OnCondicoesPagamentoPesquisaListener mOnCondicoesPagamentoPesquisaListener;
	private OnFormaPagamentoAdicionadoListener mOnFormaPagamentoAdicionadoListener;
	private OnFormasPagamentoPesquisaListener mOnFormasPagamentoPesquisaListener;
	private OnReferenciaFormaPagamentoAlteradoListener mOnReferenciaFormaPagamentoAlteradoListener;
	private FormaPagamento mFormaPagamento;
	private FormaPagamento mFormaPagamentoAuxiliar;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonFormasPagamentoPesquisa;
	private TextInputEditText mInputCondicoesPagamento;
	private TextInputEditText mInputDescricao;
	private TextInputEditText mInputId;
	private TextInputLayout mLabelCondicoesPagamento;
	private TextInputLayout mLabelDescricao;
	private TextInputLayout mLabelId;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirFormasPagamento;
	private ProgressBar mProgressBarConsumirPorId;
	private ScrollView mScrollFormaPagamentoCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public FormaPagamentoCadastroFragment() {
	}

	public static FormaPagamentoCadastroFragment newInstance(@NonNull FormaPagamento formaPagamento) {
		FormaPagamentoCadastroFragment fragmento =
				new FormaPagamentoCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(FORMA_PAGAMENTO, formaPagamento);
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
					mScrollFormaPagamentoCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.button_consumir_por_id:
				if (mFormaPagamento.getId() == null) {
					consumirFormaPagamentoGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_formas_pagamento_pesquisa:
				if (mOnFormasPagamentoPesquisaListener != null) {
					mOnFormasPagamentoPesquisaListener
							.onFormasPagamentoPesquisa(view);
				}
				break;
			case R.id.input_condicoes_pagamento:
				if (mOnCondicoesPagamentoPesquisaListener != null) {
					mOnCondicoesPagamentoPesquisaListener
							.onCondicoesPagamentoPesquisa(view);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mFormaPagamentoAuxiliar = (FormaPagamento) savedInstanceState
					.getSerializable(FORMA_PAGAMENTO_AUXILIAR);
		}
		if (getArguments() != null) {
			mFormaPagamento = (FormaPagamento) getArguments()
					.getSerializable(FORMA_PAGAMENTO);
		}
		if (mFormaPagamentoAuxiliar == null) {
			mFormaPagamentoAuxiliar = FormaPagamento.builder().build();
			transcreverFormaPagamento();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_forma_pagamento_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonFormasPagamentoPesquisa = view
				.findViewById(R.id.button_formas_pagamento_pesquisa);
		mInputCondicoesPagamento = view
				.findViewById(R.id.input_condicoes_pagamento);
		mInputDescricao = view.findViewById(R.id.input_descricao);
		mInputId = view.findViewById(R.id.input_id);
		mLabelCondicoesPagamento = view
				.findViewById(R.id.label_condicoes_pagamento);
		mLabelDescricao = view.findViewById(R.id.label_descricao);
		mLabelId = view.findViewById(R.id.label_id);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirFormasPagamento = view
				.findViewById(R.id.progress_bar_consumir_formas_pagamento);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mScrollFormaPagamentoCadastro = view
				.findViewById(R.id.scroll_forma_pagamento_cadastro);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirPorId.setOnClickListener(this);
		mButtonFormasPagamentoPesquisa.setOnClickListener(this);
		mInputCondicoesPagamento.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputDescricao.setOnFocusChangeListener(this);

		mInputDescricao.requestFocus();
		mScrollFormaPagamentoCadastro.fullScroll(View.FOCUS_UP);

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
			case R.id.input_descricao:
				if (!focused) {
					isDescricaoValido();
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
		getActivity().setTitle(R.string.forma_pagamento_cadastro_titulo);
		mButtonFormasPagamentoPesquisa.setVisibility(View.VISIBLE);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(FORMA_PAGAMENTO, mFormaPagamento);
		outState.putSerializable(FORMA_PAGAMENTO_AUXILIAR, mFormaPagamentoAuxiliar);
	}

	private void alternarButtonAdicionar() {
		mButtonAdicionar.setHint(mFormaPagamento.getId() == null
				? R.string.forma_pagamento_cadastro_button_adicionar_hint
				: R.string.forma_pagamento_cadastro_button_salvar_hint);
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(credencialLogado.isPerfilAdministrador()
				? View.VISIBLE : View.INVISIBLE);
	}

	private void alternarInputId() {
		mInputId.setEnabled(mFormaPagamento.getId() == null);
		mButtonConsumirPorId.setImageResource(mFormaPagamento.getId() == null
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
	}

	private ColecaoCallback<Colecao<CondicaoPagamentoDTO>> callbackFormaPagamentoGETCondicoesPagamento() {
		return new ColecaoCallback<Colecao<CondicaoPagamentoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<CondicaoPagamentoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonFormasPagamentoPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<CondicaoPagamentoDTO>> chamada,
			                       @NonNull Response<Colecao<CondicaoPagamentoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonFormasPagamentoPesquisa, resposta);
				} else {
					Set<CondicaoPagamentoDTO> dtos = resposta.body()
							.getEmbedded().getDtos();
					Set<CondicaoPagamento> condicoesPagamento =
							CondicaoPagamentoMapeamento
									.paraDominios(dtos, mFormaPagamento);
					mFormaPagamento.getCondicoesPagamento()
							.addAll(condicoesPagamento);
					transcreverCondicoesPagamentoFormaPagamento();
					preencherViewCondicoesPagamento();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ItemCallback<FormaPagamentoDTO> callbackFormaPagamentoGETPorId() {
		return new ItemCallback<FormaPagamentoDTO>() {

			@Override
			public void onFailure(@NonNull Call<FormaPagamentoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonFormasPagamentoPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<FormaPagamentoDTO> chamada,
			                       @NonNull Response<FormaPagamentoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonFormasPagamentoPesquisa, resposta);
					mInputId.getText().clear();
				} else {
					FormaPagamentoDTO dto = resposta.body();
					FormaPagamento formaPagamento = FormaPagamentoMapeamento
							.paraDominio(dto);
					setFormaPagamento(formaPagamento);
					if (mOnReferenciaFormaPagamentoAlteradoListener != null) {
						mOnReferenciaFormaPagamentoAlteradoListener
								.onReferenciaFormaPagamentoAlterado(mFormaPagamento);
					}
					preencherFormulario();
					mInputDescricao.requestFocus();
					mScrollFormaPagamentoCadastro.fullScroll(View.FOCUS_UP);

					consumirFormaPagamentoGETCondicoesPagamento();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private VazioCallback callbackFormaPagamentoPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonFormasPagamentoPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonFormasPagamentoPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mFormaPagamento
							.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					transcreverFormaPagamentoAuxiliar();
					consumirFormaPagamentoPOSTCondicoesPagamento();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackFormaPagamentoPOSTCondicaoPagamento(final CondicaoPagamento condicaoPagamento) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonFormasPagamentoPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonFormasPagamentoPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					condicaoPagamento
							.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackFormaPagamentoPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonFormasPagamentoPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonFormasPagamentoPesquisa, resposta);
				} else {
					transcreverFormaPagamentoAuxiliar();
					consumirFormaPagamentoPOSTCondicoesPagamento();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirFormaPagamentoGETCondicoesPagamento() {
		// TODO: 9/25/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("formaspagamento/%d/condicoesPagamento");
		HRef href = new HRef(String.format(url, mFormaPagamento.getId()));
		++mTarefasPendentes;
		FormaPagamentoRequisicao
				.getCondicoesPagamento(callbackFormaPagamentoGETCondicoesPagamento(), href);
	}

	private void consumirFormaPagamentoGETPorId() {
		mTarefasPendentes = 0;
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
				FormaPagamentoRequisicao
						.getPorId(callbackFormaPagamentoGETPorId(), id);
			}
		}
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
	}

	private void consumirFormaPagamentoPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		FormaPagamentoDTO dto = FormaPagamentoMapeamento
				.paraDTO(mFormaPagamentoAuxiliar);
		++mTarefasPendentes;
		FormaPagamentoRequisicao.post(callbackFormaPagamentoPOST(), dto);
	}

	private void consumirFormaPagamentoPOSTCondicoesPagamento() {
		for (CondicaoPagamento condicaoPagamento : mFormaPagamento.getCondicoesPagamento()) {
			if (condicaoPagamento.getId() == null) {
				CondicaoPagamentoDTO dto = CondicaoPagamentoMapeamento
						.paraDTO(condicaoPagamento);
				++mTarefasPendentes;
				CondicaoPagamentoRequisicao
						.post(callbackFormaPagamentoPOSTCondicaoPagamento(condicaoPagamento), dto);
			}
		}
	}

	private void consumirFormaPagamentoPUT() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		FormaPagamentoDTO dto = FormaPagamentoMapeamento
				.paraDTO(mFormaPagamentoAuxiliar);
		++mTarefasPendentes;
		FormaPagamentoRequisicao
				.put(callbackFormaPagamentoPUT(), mFormaPagamento.getId(), dto);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isFormularioValido() {
		return isDescricaoValido();
	}

	private boolean isDescricaoValido() {
		if (TextUtils.isEmpty(mInputDescricao.getText())) {
			mLabelDescricao.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelDescricao.setError(null);
		mLabelDescricao.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mFormaPagamentoAuxiliar
				.setDescricao(mInputDescricao.getText().toString());
	}

	private void limparErros() {
		mLabelId.setError(null);
		mLabelId.setErrorEnabled(false);
		mLabelDescricao.setError(null);
		mLabelDescricao.setErrorEnabled(false);
	}

	private void limparFormulario() {
		setFormaPagamento(FormaPagamento.builder().build());
		if (mOnReferenciaFormaPagamentoAlteradoListener != null) {
			mOnReferenciaFormaPagamentoAlteradoListener
					.onReferenciaFormaPagamentoAlterado(mFormaPagamento);
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
				if (mOnFormaPagamentoAdicionadoListener != null) {
					mOnFormaPagamentoAdicionadoListener
							.onFormaPagamentoAdicionado(mButtonAdicionar, mFormaPagamento);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mFormaPagamentoAuxiliar.getId() == null
				? "" : mFormaPagamentoAuxiliar.getId().toString());
		mInputDescricao.setText(mFormaPagamentoAuxiliar.getDescricao() == null
				? "" : mFormaPagamentoAuxiliar.getDescricao());
		preencherViewCondicoesPagamento();
		alternarButtonAdicionar();
	}

	private void preencherViewCondicoesPagamento() {
		mLabelCondicoesPagamento
				.setHint(mFormaPagamento.getCondicoesPagamento().isEmpty()
						? getString(R.string.forma_pagamento_cadastro_label_nenhum_condicao_pagamento_hint)
						: getString(R.string.forma_pagamento_cadastro_label_condicoes_pagamento_hint));
		mInputCondicoesPagamento
				.setText(mFormaPagamento.getCondicoesPagamento().isEmpty()
						? ""
						: getString(R.string.forma_pagamento_cadastro_input_condicoes_pagamento_text));
	}

	public void setOnCondicoesPagamentoPesquisaListener(@Nullable OnCondicoesPagamentoPesquisaListener ouvinte) {
		mOnCondicoesPagamentoPesquisaListener = ouvinte;
	}

	public void setOnFormaPagamentoAdicionadoListener(@Nullable OnFormaPagamentoAdicionadoListener ouvinte) {
		mOnFormaPagamentoAdicionadoListener = ouvinte;
	}

	public void setOnFormasPagamentoPesquisaListener(@Nullable OnFormasPagamentoPesquisaListener ouvinte) {
		mOnFormasPagamentoPesquisaListener = ouvinte;
	}

	public void setOnReferenciaFormaPagamentoAlteradoListener(@Nullable OnReferenciaFormaPagamentoAlteradoListener ouvinte) {
		mOnReferenciaFormaPagamentoAlteradoListener = ouvinte;
	}

	public void setFormaPagamento(@NonNull FormaPagamento formaPagamento) {
		mFormaPagamento = formaPagamento;
		if (getArguments() != null) {
			getArguments().putSerializable(FORMA_PAGAMENTO, mFormaPagamento);
		}
		if (mFormaPagamentoAuxiliar != null) {
			transcreverFormaPagamento();
		}
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mFormaPagamento.getId() != null) {
			consumirFormaPagamentoPUT();
		} else {
			consumirFormaPagamentoPOST();
		}
	}

	private void transcreverCondicoesPagamentoFormaPagamento() {
		mFormaPagamentoAuxiliar.getCondicoesPagamento().clear();
		mFormaPagamentoAuxiliar.getCondicoesPagamento()
				.addAll(mFormaPagamento.getCondicoesPagamento());
	}

	private void transcreverFormaPagamento() {
		mFormaPagamentoAuxiliar.setId(mFormaPagamento.getId());
		mFormaPagamentoAuxiliar.setDescricao(mFormaPagamento.getDescricao());
		transcreverCondicoesPagamentoFormaPagamento();
	}

	private void transcreverFormaPagamentoAuxiliar() {
		mFormaPagamento.setDescricao(mFormaPagamentoAuxiliar.getDescricao());
	}

	public interface OnCondicoesPagamentoPesquisaListener {

		void onCondicoesPagamentoPesquisa(@NonNull View view);
	}

	public interface OnFormaPagamentoAdicionadoListener {

		void onFormaPagamentoAdicionado(@NonNull View view,
		                                @NonNull FormaPagamento formaPagamento);
	}

	public interface OnFormasPagamentoPesquisaListener {

		void onFormasPagamentoPesquisa(@NonNull View view);
	}

	public interface OnReferenciaFormaPagamentoAlteradoListener {

		void onReferenciaFormaPagamentoAlterado(@NonNull FormaPagamento novaReferencia);
	}
}
