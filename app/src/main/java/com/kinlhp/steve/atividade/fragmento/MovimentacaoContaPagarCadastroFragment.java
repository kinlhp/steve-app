package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dominio.MovimentacaoContaPagar;
import com.kinlhp.steve.dto.MovimentacaoContaPagarDTO;
import com.kinlhp.steve.mapeamento.MovimentacaoContaPagarMapeamento;
import com.kinlhp.steve.requisicao.ContaPagarRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.MovimentacaoContaPagarRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Data;
import com.kinlhp.steve.util.Moeda;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class MovimentacaoContaPagarCadastroFragment extends Fragment
		implements Serializable, TextWatcher, View.OnClickListener {
	private static final String CONDICAO_PAGAMENTO = "condicaoPagamento";
	private static final String CONTA_PAGAR = "contaPagar";
	private static final String FORMA_PAGAMENTO = "formaPagamento";
	private static final String MOVIMENTACAO_CONTA_PAGAR = "movimentacaoContaPagar";
	private CondicaoPagamento mCondicaoPagamento;
	private ContaPagar mContaPagar;
	private FormaPagamento mFormaPagamento;
	private MovimentacaoContaPagar mMovimentacaoContaPagar;
	private OnCondicoesPagamentoPesquisaListener mOnCondicoesPagamentoPesquisaListener;
	private OnFormasPagamentoPesquisaListener mOnFormasPagamentoPesquisaListener;
	private OnContasPagarPesquisaListener mOnContasPagarPesquisaListener;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;

	private AppCompatButton mButtonGerar;
	private AppCompatButton mButtonEstornar;
	private AppCompatTextView mLabelValorContaPagar;
	private AppCompatTextView mLabelNumeroParcelaContaPagar;
	private AppCompatTextView mLabelDataVencimentoContaPagar;
	private AppCompatTextView mLabelMontantePagoContaPagar;
	private AppCompatTextView mLabelSaldoDevedorContaPagar;
	private AppCompatTextView mLabelTrocoADevolver;
	private TextInputEditText mInputCondicaoPagamento;
	private TextInputEditText mInputContaPagar;
	private TextInputEditText mInputDescontoConcedido;
	private TextInputEditText mInputFormaPagamento;
	private TextInputEditText mInputJuroAplicado;
	private TextInputEditText mInputValorPago;
	private TextInputLayout mLabelCondicaoPagamento;
	private TextInputLayout mLabelContaPagar;
	private TextInputLayout mLabelDescontoConcedido;
	private TextInputLayout mLabelFormaPagamento;
	private TextInputLayout mLabelJuroAplicado;
	private TextInputLayout mLabelValorPago;
	private ProgressBar mProgressBarGerar;
	private ScrollView mScrollMovimentacaoContaPagarCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public MovimentacaoContaPagarCadastroFragment() {
	}

	public static MovimentacaoContaPagarCadastroFragment newInstance(@NonNull MovimentacaoContaPagar movimentacaoContaPagar) {
		MovimentacaoContaPagarCadastroFragment fragmento =
				new MovimentacaoContaPagarCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(MOVIMENTACAO_CONTA_PAGAR, movimentacaoContaPagar);
		argumentos.putSerializable(CONTA_PAGAR, movimentacaoContaPagar.getContaPagar());
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void afterTextChanged(Editable s) {
		BigDecimal trocoADevolver = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		if (mContaPagar != null) {
			final BigDecimal baseCalculo = mContaPagar.getSaldoDevedor() != null
					? mContaPagar.getSaldoDevedor() : BigDecimal.ZERO;
			final BigDecimal juroAplicado = !TextUtils.isEmpty(mInputJuroAplicado.getText())
					? new BigDecimal(mInputJuroAplicado.getText().toString())
					: BigDecimal.ZERO;
			final BigDecimal descontoConcedido = !TextUtils.isEmpty(mInputDescontoConcedido.getText())
					? new BigDecimal(mInputDescontoConcedido.getText().toString())
					: BigDecimal.ZERO;
			final BigDecimal valorPago = !TextUtils.isEmpty(mInputValorPago.getText())
					? new BigDecimal(mInputValorPago.getText().toString())
					: BigDecimal.ZERO;
			trocoADevolver = calcularTrocoADevolver(baseCalculo, juroAplicado, descontoConcedido, valorPago);
		}
		mLabelTrocoADevolver.setText(getString(R.string.movimentacao_conta_pagar_cadastro_label_troco_a_receber_hint) + " " + Moeda.comSifra(trocoADevolver));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		/*
		 */
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_gerar:
				if (isFormularioValido()) {
					submeterFormulario();
				}
				break;
			case R.id.button_estornar:
				estornar();
				break;
			case R.id.input_condicao_pagamento:
				if (mOnCondicoesPagamentoPesquisaListener != null
						&& mFormaPagamento != null) {
					mOnCondicoesPagamentoPesquisaListener
							.onCondicoesPagamentoPesquisa(view, mFormaPagamento.getCondicoesPagamento());
				}
				break;
			case R.id.input_forma_pagamento:
				if (mOnFormasPagamentoPesquisaListener != null) {
					mOnFormasPagamentoPesquisaListener
							.onFormasPagamentoPesquisa(view);
				}
				break;
			case R.id.input_conta_pagar:
				if (mOnContasPagarPesquisaListener != null) {
					mOnContasPagarPesquisaListener.onContasPagarPesquisa(view);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mMovimentacaoContaPagar = (MovimentacaoContaPagar) savedInstanceState.getSerializable(MOVIMENTACAO_CONTA_PAGAR);
			mCondicaoPagamento = (CondicaoPagamento) savedInstanceState
					.getSerializable(CONDICAO_PAGAMENTO);
			mFormaPagamento = (FormaPagamento) savedInstanceState
					.getSerializable(FORMA_PAGAMENTO);
			mContaPagar = (ContaPagar) savedInstanceState.getSerializable(CONTA_PAGAR);
		}
		if (getArguments() != null) {
			mMovimentacaoContaPagar = (MovimentacaoContaPagar) getArguments().getSerializable(MOVIMENTACAO_CONTA_PAGAR);
			mCondicaoPagamento = (CondicaoPagamento) getArguments
					().getSerializable(CONDICAO_PAGAMENTO);
			mFormaPagamento = (FormaPagamento) getArguments()
					.getSerializable(FORMA_PAGAMENTO);
			mContaPagar = (ContaPagar) getArguments().getSerializable(CONTA_PAGAR);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_movimentacao_conta_pagar_cadastro, container, false);
		mButtonGerar = view.findViewById(R.id.button_gerar);
		mButtonEstornar = view.findViewById(R.id.button_estornar);
		mInputCondicaoPagamento = view
				.findViewById(R.id.input_condicao_pagamento);
		mInputContaPagar = view.findViewById(R.id.input_conta_pagar);
		mInputDescontoConcedido = view.findViewById(R.id.input_desconto_concedido);
		mInputFormaPagamento = view.findViewById(R.id.input_forma_pagamento);
		mInputJuroAplicado = view.findViewById(R.id.input_juro_aplicado);
		mInputValorPago = view.findViewById(R.id.input_valor_pago);
		mLabelCondicaoPagamento = view
				.findViewById(R.id.label_condicao_pagamento);
		mLabelContaPagar = view.findViewById(R.id.label_conta_pagar);
		mLabelDescontoConcedido = view.findViewById(R.id.label_desconto_concedido);
		mLabelFormaPagamento = view.findViewById(R.id.label_forma_pagamento);
		mLabelJuroAplicado = view.findViewById(R.id.label_juro_aplicado);
		mLabelValorContaPagar = view.findViewById(R.id.label_valor_conta_pagar);
		mLabelNumeroParcelaContaPagar = view.findViewById(R.id.label_numero_parcela_conta_pagar);
		mLabelDataVencimentoContaPagar = view.findViewById(R.id.label_data_vencimento_conta_pagar);
		mLabelMontantePagoContaPagar = view.findViewById(R.id.label_montante_pago_conta_pagar);
		mLabelSaldoDevedorContaPagar = view.findViewById(R.id.label_saldo_devedor_conta_pagar);
		mLabelTrocoADevolver = view.findViewById(R.id.label_troco_a_devolver);
		mLabelValorPago = view.findViewById(R.id.label_valor_pago);
		mProgressBarGerar = view.findViewById(R.id.progress_bar_gerar);
		mScrollMovimentacaoContaPagarCadastro = view
				.findViewById(R.id.scroll_movimentacao_conta_pagar_cadastro);

		mButtonGerar.setOnClickListener(this);
		mButtonEstornar.setOnClickListener(this);
		mInputCondicaoPagamento.setOnClickListener(this);
		mInputContaPagar.setOnClickListener(this);
		mInputFormaPagamento.setOnClickListener(this);
		mInputValorPago.addTextChangedListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.movimentacao_conta_pagar_cadastro_titulo);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(MOVIMENTACAO_CONTA_PAGAR, mMovimentacaoContaPagar);
		outState.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		outState.putSerializable(CONTA_PAGAR, mContaPagar);
		outState.putSerializable(FORMA_PAGAMENTO, mFormaPagamento);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		/*
		 */
	}

	private BigDecimal calcularTrocoADevolver(@NonNull BigDecimal baseCalculo,
	                                          @NonNull BigDecimal juroAplicado,
	                                          @NonNull BigDecimal descontoConcedido,
	                                          @NonNull BigDecimal valorPago) {
		BigDecimal trocoADevolver = baseCalculo
				.add(juroAplicado)
				.subtract(descontoConcedido)
				.subtract(valorPago)
				.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return BigDecimal.ZERO.compareTo(trocoADevolver) > 0
				? trocoADevolver.abs() : BigDecimal.ZERO;
	}

	private VazioCallback callbackMovimentacaoContaPagarPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
				Falha.tratar(mButtonGerar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonGerar, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mMovimentacaoContaPagar.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
			}
		};
	}

	private VazioCallback callbackMovimentacaoContaPagarEstorno() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
				Falha.tratar(mButtonGerar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonGerar, resposta);
				}
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
			}
		};
	}

	private void consumirMovimentacaoContaPagarPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarGerar, mButtonGerar);
		Teclado.ocultar(getActivity(), mButtonGerar);
		MovimentacaoContaPagarDTO dto = MovimentacaoContaPagarMapeamento.paraDTO(mMovimentacaoContaPagar);
		++mTarefasPendentes;
		MovimentacaoContaPagarRequisicao.post(callbackMovimentacaoContaPagarPOST(), dto);
	}

	private void consumirMovimentacaoContaPagarEstorno() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarGerar, mButtonGerar);
		Teclado.ocultar(getActivity(), mButtonGerar);
		++mTarefasPendentes;
		ContaPagarRequisicao.estorno(callbackMovimentacaoContaPagarEstorno(), mContaPagar.getId());
	}

	private void estornar() {
		if (mContaPagar != null && mContaPagar.getId() != null) {
			consumirMovimentacaoContaPagarEstorno();
		}
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isFormularioValido() {
		return
				isContaPagarValido()
						&& isFormaPagamentoValido()
						&& isCondicaoPagamentoValido()
						&& isJuroAplicadoValido()
						&& isDescontoConcedidoValido()
						&& isValorPagoValido();
	}

	private boolean isCondicaoPagamentoValido() {
		if (mCondicaoPagamento == null) {
			mLabelCondicaoPagamento
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelCondicaoPagamento.setError(null);
		mLabelCondicaoPagamento.setErrorEnabled(false);
		return true;
	}

	private boolean isContaPagarValido() {
		if (mContaPagar == null) {
			mLabelContaPagar.setError(getString(R.string.input_obrigatorio));
			mScrollMovimentacaoContaPagarCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		if (mContaPagar.getSituacao().equals(ContaPagar.Situacao.BAIXADO)) {
			mLabelContaPagar
					.setError(getString(R.string.movimentacao_conta_pagar_cadastro_mensagem_baixado));
			mScrollMovimentacaoContaPagarCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		if (mContaPagar.getSituacao().equals(ContaPagar.Situacao.CANCELADO)) {
			mLabelContaPagar
					.setError(getString(R.string.movimentacao_conta_pagar_cadastro_mensagem_cancelado));
			mScrollMovimentacaoContaPagarCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		if (!mContaPagar.hasSaldoDevedor()) {
			mLabelContaPagar
					.setError(getString(R.string.movimentacao_conta_pagar_cadastro_mensagem_saldo_devedor_0));
			mScrollMovimentacaoContaPagarCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		mLabelContaPagar.setError(null);
		mLabelContaPagar.setErrorEnabled(false);
		return true;
	}

	private boolean isDescontoConcedidoValido() {
		if (TextUtils.isEmpty(mInputDescontoConcedido.getText())) {
			mLabelDescontoConcedido.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigDecimal descontoConcedido = new BigDecimal(mInputDescontoConcedido.getText().toString())
				.setScale(2, RoundingMode.HALF_EVEN);
		if (descontoConcedido.compareTo(BigDecimal.ZERO) < 0) {
			mLabelDescontoConcedido.setError(getString(R.string.input_invalido));
			return false;
		}
		mMovimentacaoContaPagar.setDescontoConcedido(descontoConcedido);
		mLabelDescontoConcedido.setError(null);
		mLabelDescontoConcedido.setErrorEnabled(false);
		return true;
	}

	private boolean isFormaPagamentoValido() {
		if (mFormaPagamento == null) {
			mLabelFormaPagamento
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelFormaPagamento.setError(null);
		mLabelFormaPagamento.setErrorEnabled(false);
		return true;
	}

	private boolean isJuroAplicadoValido() {
		if (TextUtils.isEmpty(mInputJuroAplicado.getText())) {
			mLabelJuroAplicado.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigDecimal juroAplicado = new BigDecimal(mInputJuroAplicado.getText().toString())
				.setScale(2, RoundingMode.HALF_EVEN);
		if (juroAplicado.compareTo(BigDecimal.ZERO) < 0) {
			mLabelJuroAplicado.setError(getString(R.string.input_invalido));
			return false;
		}
		mMovimentacaoContaPagar.setJuroAplicado(juroAplicado);
		mLabelJuroAplicado.setError(null);
		mLabelJuroAplicado.setErrorEnabled(false);
		return true;
	}

	private boolean isValorPagoValido() {
		if (TextUtils.isEmpty(mInputValorPago.getText())) {
			mLabelValorPago.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigDecimal valorPago = new BigDecimal(mInputValorPago.getText().toString())
				.setScale(2, RoundingMode.HALF_EVEN);
		if (valorPago.compareTo(BigDecimal.ZERO) < 0) {
			mLabelValorPago.setError(getString(R.string.input_invalido));
			return false;
		}
		if (mContaPagar.getSaldoDevedor().compareTo(valorPago) < 0) {
			valorPago = mContaPagar.getSaldoDevedor();
		}
		mMovimentacaoContaPagar.setValorPago(valorPago);
		mLabelValorPago.setError(null);
		mLabelValorPago.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mMovimentacaoContaPagar.setBaseCalculo(mMovimentacaoContaPagar.getContaPagar().getSaldoDevedor());
	}

	private void limparErros() {
		mLabelContaPagar.setError(null);
		mLabelFormaPagamento.setErrorEnabled(false);
		mLabelCondicaoPagamento.setError(null);
		mLabelJuroAplicado.setErrorEnabled(false);
		mLabelDescontoConcedido.setError(null);
		mLabelValorPago.setErrorEnabled(false);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPressionarVoltar) {
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		preencherViewContaPagar();
		mLabelValorContaPagar.setText(mContaPagar.getValor() == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_valor_conta_pagar_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_valor_conta_pagar_hint) + " " + mContaPagar.getValor().setScale(2, RoundingMode.HALF_EVEN).toString());
		mLabelNumeroParcelaContaPagar.setText(mContaPagar.getNumeroParcela() == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_numero_parcela_conta_pagar_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_numero_parcela_conta_pagar_hint) + " " + mContaPagar.getNumeroParcela().toString());
		mLabelDataVencimentoContaPagar.setText(mContaPagar.getDataVencimento() == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_data_vencimento_conta_pagar_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_data_vencimento_conta_pagar_hint) + " " + Data.paraStringData(mContaPagar.getDataVencimento()));
		mLabelMontantePagoContaPagar.setText(mContaPagar.getMontantePago() == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_montante_pago_conta_pagar_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_montante_pago_conta_pagar_hint) + " " + mContaPagar.getMontantePago().setScale(2, RoundingMode.HALF_EVEN).toString());
		mLabelSaldoDevedorContaPagar.setText(mContaPagar.getSaldoDevedor() == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_saldo_devedor_conta_pagar_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_saldo_devedor_conta_pagar_hint) + " " + mContaPagar.getSaldoDevedor().setScale(2, RoundingMode.HALF_EVEN).toString());
		preencherViewFormaPagamento();
		preencherViewCondicaoPagamento();
		mButtonEstornar.setVisibility((mContaPagar != null && mContaPagar.getId() != null && mContaPagar.hasMontantePago()) ? View.VISIBLE : View.GONE);
	}

	private void preencherViewCondicaoPagamento() {
		mLabelCondicaoPagamento.setHint(mCondicaoPagamento == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_nenhum_condicao_pagamento_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_condicao_pagamento_hint));
		mInputCondicaoPagamento.setText(mCondicaoPagamento == null
				? "" : mCondicaoPagamento.toString());
	}

	private void preencherViewFormaPagamento() {
		mLabelFormaPagamento.setHint(mFormaPagamento == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_nenhum_forma_pagamento_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_forma_pagamento_hint));
		mInputFormaPagamento.setText(mFormaPagamento == null
				? "" : mFormaPagamento.toString());
	}

	private void preencherViewContaPagar() {
		mLabelContaPagar.setHint(mContaPagar == null
				? getString(R.string.movimentacao_conta_pagar_cadastro_label_nenhum_conta_pagar_hint)
				: getString(R.string.movimentacao_conta_pagar_cadastro_label_conta_pagar_hint));
		mInputContaPagar.setText(mContaPagar == null ? "" : mContaPagar.toString());
	}

	public void setCondicaoPagamento(@NonNull CondicaoPagamento condicaoPagamento) {
		mCondicaoPagamento = condicaoPagamento;
		mMovimentacaoContaPagar.setCondicaoPagamento(mCondicaoPagamento);
		if (getArguments() != null) {
			getArguments()
					.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		}
		isCondicaoPagamentoValido();
	}

	public void setFormaPagamento(@NonNull FormaPagamento formaPagamento) {
		mFormaPagamento = formaPagamento;
		if (getArguments() != null) {
			getArguments().putSerializable(FORMA_PAGAMENTO, mFormaPagamento);
		}
		mCondicaoPagamento = null;
		isFormaPagamentoValido();
	}

	public void setOnCondicoesPagamentoPesquisaListener(@Nullable OnCondicoesPagamentoPesquisaListener ouvinte) {
		mOnCondicoesPagamentoPesquisaListener = ouvinte;
	}

	public void setOnFormasPagamentoPesquisaListener(@Nullable OnFormasPagamentoPesquisaListener ouvinte) {
		mOnFormasPagamentoPesquisaListener = ouvinte;
	}

	public void setOnContasPagarPesquisaListener(@Nullable OnContasPagarPesquisaListener ouvinte) {
		mOnContasPagarPesquisaListener = ouvinte;
	}

	public void setContaPagar(@NonNull ContaPagar contaPagar) {
		mContaPagar = contaPagar;
		mMovimentacaoContaPagar.setContaPagar(mContaPagar);
		if (getArguments() != null) {
			getArguments().putSerializable(CONTA_PAGAR, mContaPagar);
		}
		isContaPagarValido();
	}

	private void submeterFormulario() {
		iterarFormulario();
		consumirMovimentacaoContaPagarPOST();
	}

	public interface OnCondicoesPagamentoPesquisaListener {

		void onCondicoesPagamentoPesquisa(@NonNull View view,
		                                  @NonNull Set<CondicaoPagamento> condicoesPagamento);
	}

	public interface OnFormasPagamentoPesquisaListener {

		void onFormasPagamentoPesquisa(@NonNull View view);
	}

	public interface OnContasPagarPesquisaListener {

		void onContasPagarPesquisa(@NonNull View view);
	}
}
