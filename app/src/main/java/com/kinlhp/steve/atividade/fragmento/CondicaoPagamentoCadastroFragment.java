package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.mapeamento.CondicaoPagamentoMapeamento;
import com.kinlhp.steve.requisicao.CondicaoPagamentoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CondicaoPagamentoCadastroFragment extends Fragment
		implements View.OnClickListener, View.OnFocusChangeListener,
		Serializable {
	private static final long serialVersionUID = -3073909665522638048L;
	private static final String CONDICAO_PAGAMENTO = "condicaoPagamento";
	private static final String CONDICAO_PAGAMENTO_AUXILIAR = "condicaoPagamentoAuxiliar";
	private CondicaoPagamento mCondicaoPagamento;
	private CondicaoPagamento mCondicaoPagamentoAuxiliar;
	private OnCondicaoPagamentoAdicionadoListener mOnCondicaoPagamentoAdicionadoListener;
	private int mTarefasPendentes;

	private AppCompatButton mButtonAdicionar;
	private TextInputEditText mInputDescricao;
	private TextInputEditText mInputPeriodoEntreParcelas;
	private TextInputEditText mInputQuantidadeParcelas;
	private TextInputLayout mLabelDescricao;
	private TextInputLayout mLabelPeriodoEntreParcelas;
	private TextInputLayout mLabelQuantidadeParcelas;
	private ProgressBar mProgressBarAdicionar;
	private ScrollView mScrollCondicaoPagamentoCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public CondicaoPagamentoCadastroFragment() {
	}

	public static CondicaoPagamentoCadastroFragment newInstance(@NonNull CondicaoPagamento condicaoPagamento) {
		CondicaoPagamentoCadastroFragment fragmento =
				new CondicaoPagamentoCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(CONDICAO_PAGAMENTO, condicaoPagamento);
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
					mScrollCondicaoPagamentoCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCondicaoPagamentoAuxiliar = (CondicaoPagamento) savedInstanceState
					.getSerializable(CONDICAO_PAGAMENTO_AUXILIAR);
		}
		if (getArguments() != null) {
			mCondicaoPagamento = (CondicaoPagamento) getArguments()
					.getSerializable(CONDICAO_PAGAMENTO);
		}
		if (mCondicaoPagamentoAuxiliar == null) {
			mCondicaoPagamentoAuxiliar = CondicaoPagamento.builder().build();
			transcreverCondicaoPagamento();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_condicao_pagamento_cadastro, container, false);
		mScrollCondicaoPagamentoCadastro = (ScrollView) view;
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputDescricao = view.findViewById(R.id.input_descricao);
		mInputPeriodoEntreParcelas = view
				.findViewById(R.id.input_periodo_entre_parcelas);
		mInputQuantidadeParcelas = view
				.findViewById(R.id.input_quantidade_parcelas);
		mLabelDescricao = view.findViewById(R.id.label_descricao);
		mLabelPeriodoEntreParcelas = view
				.findViewById(R.id.label_periodo_entre_parcelas);
		mLabelQuantidadeParcelas = view
				.findViewById(R.id.label_quantidade_parcelas);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);

		mButtonAdicionar.setOnClickListener(this);
		mInputDescricao.setOnFocusChangeListener(this);

		mInputDescricao.requestFocus();
		mScrollCondicaoPagamentoCadastro.fullScroll(View.FOCUS_UP);

		return view;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_descricao:
				if (!focused) {
					isDescricaoValido();
				}
				break;
			case R.id.input_periodo_entre_parcelas:
				if (!focused) {
					isPeriodoEntreParcelasValido();
				}
				break;
			case R.id.input_quantidade_parcelas:
				if (!focused) {
					isQuantidadeParcelasValido();
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
		getActivity().setTitle(R.string.condicao_pagamento_cadastro_titulo);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		outState.putSerializable(CONDICAO_PAGAMENTO_AUXILIAR, mCondicaoPagamentoAuxiliar);
	}

	private void alternarButtonAdicionar() {
		// TODO: 9/15/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<CondicaoPagamento> condicoesPagamento =
				new ArrayList<>(mCondicaoPagamento.getFormaPagamento().getCondicoesPagamento());
		if (condicoesPagamento.contains(mCondicaoPagamento)) {
			mButtonAdicionar.setHint(mCondicaoPagamento.getId() == null
					? R.string.condicao_pagamento_cadastro_button_alterar_hint
					: R.string.condicao_pagamento_cadastro_button_salvar_hint);
		}
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(credencialLogado.isPerfilAdministrador()
				? View.VISIBLE : View.INVISIBLE);
	}

	private VazioCallback callbackCondicaoPagamentoPUT() {
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
					transcreverCondicaoPagamentoAuxiliar();
					if (mOnCondicaoPagamentoAdicionadoListener != null) {
						mOnCondicaoPagamentoAdicionadoListener
								.onCondicaoPagamentoAdicionado(mButtonAdicionar, mCondicaoPagamento);
					}
					getActivity().onBackPressed();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirCondicaoPagamentoPUT() {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		CondicaoPagamentoDTO dto = CondicaoPagamentoMapeamento
				.paraDTO(mCondicaoPagamentoAuxiliar);
		++mTarefasPendentes;
		CondicaoPagamentoRequisicao
				.put(callbackCondicaoPagamentoPUT(), mCondicaoPagamento.getId(), dto);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
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

	private boolean isFormularioValido() {
		return isDescricaoValido()
				&& isQuantidadeParcelasValido()
				&& isPeriodoEntreParcelasValido();
	}

	private boolean isPeriodoEntreParcelasValido() {
		if (TextUtils.isEmpty(mInputPeriodoEntreParcelas.getText())) {
			mLabelPeriodoEntreParcelas
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigInteger periodo =
				new BigInteger(mInputPeriodoEntreParcelas.getText().toString());
		if (periodo.compareTo(BigInteger.ZERO) < 0) {
			mLabelPeriodoEntreParcelas
					.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelPeriodoEntreParcelas.setError(null);
		mLabelPeriodoEntreParcelas.setErrorEnabled(false);
		return true;
	}

	private boolean isQuantidadeParcelasValido() {
		if (TextUtils.isEmpty(mInputQuantidadeParcelas.getText())) {
			mLabelQuantidadeParcelas
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigInteger quantidade =
				new BigInteger(mInputQuantidadeParcelas.getText().toString());
		if (quantidade.compareTo(BigInteger.ZERO) < 0) {
			mLabelQuantidadeParcelas
					.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelQuantidadeParcelas.setError(null);
		mLabelQuantidadeParcelas.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mCondicaoPagamentoAuxiliar
				.setDescricao(mInputDescricao.getText().toString());
		mCondicaoPagamentoAuxiliar
				.setPeriodoEntreParcelas(TextUtils.isEmpty(mInputPeriodoEntreParcelas.getText())
						? BigInteger.ZERO
						: new BigInteger(mInputPeriodoEntreParcelas.getText().toString()));
		mCondicaoPagamentoAuxiliar
				.setQuantidadeParcelas(TextUtils.isEmpty(mInputQuantidadeParcelas.getText())
						? BigInteger.ZERO
						: new BigInteger(mInputQuantidadeParcelas.getText().toString()));
	}

	private void limparErros() {
		mLabelDescricao.setError(null);
		mLabelDescricao.setErrorEnabled(false);
		mLabelQuantidadeParcelas.setError(null);
		mLabelQuantidadeParcelas.setErrorEnabled(false);
		mLabelPeriodoEntreParcelas.setError(null);
		mLabelPeriodoEntreParcelas.setErrorEnabled(false);
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
		mInputDescricao
				.setText(mCondicaoPagamentoAuxiliar.getDescricao() == null
						? "" : mCondicaoPagamentoAuxiliar.getDescricao());
		mInputQuantidadeParcelas
				.setText(mCondicaoPagamentoAuxiliar.getQuantidadeParcelas() == null
						? ""
						: mCondicaoPagamentoAuxiliar.getQuantidadeParcelas().toString());
		mInputPeriodoEntreParcelas
				.setText(mCondicaoPagamentoAuxiliar.getPeriodoEntreParcelas() == null
						? ""
						: mCondicaoPagamentoAuxiliar.getPeriodoEntreParcelas().toString());
		alternarButtonAdicionar();
	}

	public void setCondicaoPagamento(@NonNull CondicaoPagamento condicaoPagamento) {
		mCondicaoPagamento = condicaoPagamento;
		if (getArguments() != null) {
			getArguments()
					.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		}
		if (mCondicaoPagamentoAuxiliar != null) {
			transcreverCondicaoPagamento();
		}
	}

	public void setOnCondicaoPagamentoAdicionadoListener(@Nullable OnCondicaoPagamentoAdicionadoListener ouvinte) {
		mOnCondicaoPagamentoAdicionadoListener = ouvinte;
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mCondicaoPagamento.getId() != null) {
			consumirCondicaoPagamentoPUT();
		} else {
			transcreverCondicaoPagamentoAuxiliar();
			if (mOnCondicaoPagamentoAdicionadoListener != null) {
				mOnCondicaoPagamentoAdicionadoListener
						.onCondicaoPagamentoAdicionado(mButtonAdicionar, mCondicaoPagamento);
			}
			getActivity().onBackPressed();
		}
	}

	private void transcreverCondicaoPagamento() {
		mCondicaoPagamentoAuxiliar
				.setDescricao(mCondicaoPagamento.getDescricao());
		mCondicaoPagamentoAuxiliar
				.setFormaPagamento(mCondicaoPagamento.getFormaPagamento());
		mCondicaoPagamentoAuxiliar.setId(mCondicaoPagamento.getId());
		mCondicaoPagamentoAuxiliar
				.setPeriodoEntreParcelas(mCondicaoPagamento.getPeriodoEntreParcelas());
		mCondicaoPagamentoAuxiliar
				.setQuantidadeParcelas(mCondicaoPagamento.getQuantidadeParcelas());
	}

	private void transcreverCondicaoPagamentoAuxiliar() {
		mCondicaoPagamento
				.setDescricao(mCondicaoPagamentoAuxiliar.getDescricao());
		mCondicaoPagamento
				.setPeriodoEntreParcelas(mCondicaoPagamentoAuxiliar.getPeriodoEntreParcelas());
		mCondicaoPagamento
				.setQuantidadeParcelas(mCondicaoPagamentoAuxiliar.getQuantidadeParcelas());
	}

	public interface OnCondicaoPagamentoAdicionadoListener {

		void onCondicaoPagamentoAdicionado(@NonNull View view,
		                                   @NonNull CondicaoPagamento condicaoPagamento);
	}
}
