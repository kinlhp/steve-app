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
import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dominio.MovimentacaoContaReceber;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
import com.kinlhp.steve.mapeamento.MovimentacaoContaReceberMapeamento;
import com.kinlhp.steve.requisicao.ContaReceberRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.MovimentacaoContaReceberRequisicao;
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

public class MovimentacaoContaReceberCadastroFragment extends Fragment
		implements Serializable, TextWatcher, View.OnClickListener {
	private static final String CONDICAO_PAGAMENTO = "condicaoPagamento";
	private static final String CONTA_RECEBER = "contaReceber";
	private static final String FORMA_PAGAMENTO = "formaPagamento";
	private static final String MOVIMENTACAO_CONTA_RECEBER = "movimentacaoContaReceber";
	private CondicaoPagamento mCondicaoPagamento;
	private ContaReceber mContaReceber;
	private FormaPagamento mFormaPagamento;
	//	private ArrayList<ContaReceber> mParcelas = new ArrayList<>();
//	private OnClientesPesquisaListener mOnClientesPesquisaListener;
	private MovimentacaoContaReceber mMovimentacaoContaReceber;
	private OnCondicoesPagamentoPesquisaListener mOnCondicoesPagamentoPesquisaListener;
	private OnFormasPagamentoPesquisaListener mOnFormasPagamentoPesquisaListener;
	private OnContasReceberPesquisaListener mOnContasReceberPesquisaListener;
	//	private OnParcelasPesquisaListener mOnParcelasPesquisaListener;
//	private OnParcelasGeradoListener mOnParcelasGeradoListener;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;

	private AppCompatButton mButtonGerar;
	private AppCompatButton mButtonEstornar;
	private AppCompatTextView mLabelValorContaReceber;
	private AppCompatTextView mLabelNumeroParcelaContaReceber;
	private AppCompatTextView mLabelDataVencimentoContaReceber;
	private AppCompatTextView mLabelMontantePagoContaReceber;
	private AppCompatTextView mLabelSaldoDevedorContaReceber;
	private AppCompatTextView mLabelTrocoADevolver;
	//	private TextInputEditText mInputCliente;
	private TextInputEditText mInputCondicaoPagamento;
	private TextInputEditText mInputContaReceber;
	private TextInputEditText mInputDescontoConcedido;
	private TextInputEditText mInputFormaPagamento;
	private TextInputEditText mInputJuroAplicado;
	//	private TextInputEditText mInputParcelas;
	private TextInputEditText mInputValorPago;
	//	private TextInputLayout mLabelCliente;
	private TextInputLayout mLabelCondicaoPagamento;
	private TextInputLayout mLabelContaReceber;
	private TextInputLayout mLabelDescontoConcedido;
	private TextInputLayout mLabelFormaPagamento;
	private TextInputLayout mLabelJuroAplicado;
	//	private TextInputLayout mLabelParcelas;
	private TextInputLayout mLabelValorPago;
	private ProgressBar mProgressBarGerar;
	private ScrollView mScrollMovimentacaoContaReceberCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public MovimentacaoContaReceberCadastroFragment() {
	}

	public static MovimentacaoContaReceberCadastroFragment newInstance(@NonNull MovimentacaoContaReceber movimentacaoContaReceber) {
		MovimentacaoContaReceberCadastroFragment fragmento =
				new MovimentacaoContaReceberCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(MOVIMENTACAO_CONTA_RECEBER, movimentacaoContaReceber);
		argumentos.putSerializable(CONTA_RECEBER, movimentacaoContaReceber.getContaReceber());
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void afterTextChanged(Editable s) {
		BigDecimal trocoADevolver = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		if (mContaReceber != null) {
			final BigDecimal baseCalculo = mContaReceber.getSaldoDevedor() != null
					? mContaReceber.getSaldoDevedor() : BigDecimal.ZERO;
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
		mLabelTrocoADevolver.setText(getString(R.string.movimentacao_conta_receber_cadastro_label_troco_a_devolver_hint) + " " + Moeda.comSifra(trocoADevolver));
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
//			case R.id.input_cliente:
//				if (mOnClientesPesquisaListener != null) {
//					mOnClientesPesquisaListener.onClientesPesquisa(view);
//				}
//				break;
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
			case R.id.input_conta_receber:
				if (mOnContasReceberPesquisaListener != null) {
					mOnContasReceberPesquisaListener.onContasReceberPesquisa(view);
				}
				break;
//			case R.id.input_parcelas:
//				if (mOnParcelasPesquisaListener != null) {
//					mOnParcelasPesquisaListener
//							.onParcelasPesquisa(view);
//				}
//				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mMovimentacaoContaReceber = (MovimentacaoContaReceber) savedInstanceState.getSerializable(MOVIMENTACAO_CONTA_RECEBER);
			mCondicaoPagamento = (CondicaoPagamento) savedInstanceState
					.getSerializable(CONDICAO_PAGAMENTO);
			mFormaPagamento = (FormaPagamento) savedInstanceState
					.getSerializable(FORMA_PAGAMENTO);
			mContaReceber = (ContaReceber) savedInstanceState.getSerializable(CONTA_RECEBER);
		}
		if (getArguments() != null) {
			mMovimentacaoContaReceber = (MovimentacaoContaReceber) getArguments().getSerializable(MOVIMENTACAO_CONTA_RECEBER);
			mCondicaoPagamento = (CondicaoPagamento) getArguments
					().getSerializable(CONDICAO_PAGAMENTO);
			mFormaPagamento = (FormaPagamento) getArguments()
					.getSerializable(FORMA_PAGAMENTO);
			mContaReceber = (ContaReceber) getArguments().getSerializable(CONTA_RECEBER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_movimentacao_conta_receber_cadastro, container, false);
		mButtonGerar = view.findViewById(R.id.button_gerar);
		mButtonEstornar = view.findViewById(R.id.button_estornar);
//		mInputCliente = view.findViewById(R.id.input_cliente);
		mInputCondicaoPagamento = view
				.findViewById(R.id.input_condicao_pagamento);
		mInputContaReceber = view.findViewById(R.id.input_conta_receber);
		mInputDescontoConcedido = view.findViewById(R.id.input_desconto_concedido);
		mInputFormaPagamento = view.findViewById(R.id.input_forma_pagamento);
		mInputJuroAplicado = view.findViewById(R.id.input_juro_aplicado);
//		mInputParcelas = view.findViewById(R.id.input_parcelas);
//		mLabelCliente = view.findViewById(R.id.label_cliente);
		mInputValorPago = view.findViewById(R.id.input_valor_pago);
		mLabelCondicaoPagamento = view
				.findViewById(R.id.label_condicao_pagamento);
		mLabelContaReceber = view.findViewById(R.id.label_conta_receber);
		mLabelDescontoConcedido = view.findViewById(R.id.label_desconto_concedido);
		mLabelFormaPagamento = view.findViewById(R.id.label_forma_pagamento);
		mLabelJuroAplicado = view.findViewById(R.id.label_juro_aplicado);
//		mLabelParcelas = view.findViewById(R.id.label_parcelas);
		mLabelValorContaReceber = view.findViewById(R.id.label_valor_conta_receber);
		mLabelNumeroParcelaContaReceber = view.findViewById(R.id.label_numero_parcela_conta_receber);
		mLabelDataVencimentoContaReceber = view.findViewById(R.id.label_data_vencimento_conta_receber);
		mLabelMontantePagoContaReceber = view.findViewById(R.id.label_montante_pago_conta_receber);
		mLabelSaldoDevedorContaReceber = view.findViewById(R.id.label_saldo_devedor_conta_receber);
		mLabelTrocoADevolver = view.findViewById(R.id.label_troco_a_devolver);
		mLabelValorPago = view.findViewById(R.id.label_valor_pago);
		mProgressBarGerar = view.findViewById(R.id.progress_bar_gerar);
		mScrollMovimentacaoContaReceberCadastro = view
				.findViewById(R.id.scroll_movimentacao_conta_receber_cadastro);

		mButtonGerar.setOnClickListener(this);
		mButtonEstornar.setOnClickListener(this);
//		mInputCliente.setOnClickListener(this);
		mInputCondicaoPagamento.setOnClickListener(this);
		mInputContaReceber.setOnClickListener(this);
		mInputFormaPagamento.setOnClickListener(this);
//		mInputParcelas.setOnClickListener(this);
		mInputValorPago.addTextChangedListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.movimentacao_conta_receber_cadastro_titulo);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(MOVIMENTACAO_CONTA_RECEBER, mMovimentacaoContaReceber);
		outState.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		outState.putSerializable(CONTA_RECEBER, mContaReceber);
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

	private VazioCallback callbackMovimentacaoContaReceberPOST() {
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
					mMovimentacaoContaReceber.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
//					if (mTarefasPendentes <= 0) {
//						consumirOrdemPUTSituacao();
//					}
				}
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
			}
		};
	}

	private VazioCallback callbackMovimentacaoContaReceberEstorno() {
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
//				} else {
//					String location = resposta.headers()
//							.get(Requisicao.LOCATION_HEADER);
//					mMovimentacaoContaReceber.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
//					if (mTarefasPendentes <= 0) {
//						consumirOrdemPUTSituacao();
//					}
				}
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
			}
		};
	}

//	private VazioCallback callbackOrdemPUTSituacao() {
//		return new VazioCallback() {
//
//			@Override
//			public void onFailure(@NonNull Call<Void> chamada,
//			                      @NonNull Throwable causa) {
//				mOrdem.setSituacao(mSituacaoAnterior);
//				--mTarefasPendentes;
//				mPressionarVoltar = false;
//				ocultarProgresso(mProgressBarGerar, mButtonGerar);
//				Falha.tratar(mButtonGerar, causa);
//			}
//
//			@Override
//			public void onResponse(@NonNull Call<Void> chamada,
//			                       @NonNull Response<Void> resposta) {
//				--mTarefasPendentes;
//				if (!resposta.isSuccessful()) {
//					mOrdem.setSituacao(mSituacaoAnterior);
//					mPressionarVoltar = false;
//					Falha.tratar(mButtonGerar, resposta);
//				}
//				ocultarProgresso(mProgressBarGerar, mButtonGerar);
//			}
//		};
//	}

	private void consumirMovimentacaoContaReceberPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarGerar, mButtonGerar);
		Teclado.ocultar(getActivity(), mButtonGerar);
		MovimentacaoContaReceberDTO dto = MovimentacaoContaReceberMapeamento.paraDTO(mMovimentacaoContaReceber);
		++mTarefasPendentes;
		MovimentacaoContaReceberRequisicao.post(callbackMovimentacaoContaReceberPOST(), dto);
	}

	private void consumirMovimentacaoContaReceberEstorno() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarGerar, mButtonGerar);
		Teclado.ocultar(getActivity(), mButtonGerar);
		++mTarefasPendentes;
		ContaReceberRequisicao.estorno(callbackMovimentacaoContaReceberEstorno(), mContaReceber.getId());
	}

//	private void consumirOrdemPUTSituacao() {
//		mSituacaoAnterior = mOrdem.getSituacao();
//		mOrdem.setSituacao(Ordem.Situacao.GERADO);
//		OrdemDTO dto = OrdemMapeamento.paraDTO(mOrdem);
//		++mTarefasPendentes;
//		OrdemRequisicao.put(callbackOrdemPUTSituacao(), mOrdem.getId(), dto);
//	}

	private void estornar() {
		if (mContaReceber != null && mContaReceber.getId() != null)
		{
			consumirMovimentacaoContaReceberEstorno();
		}
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

//	private void gerarParcelas() {
//		isFormularioValido();
//		if (mOrdem != null && mSacado != null
//				&& mCondicaoPagamento != null) {
//			BigDecimal valorTotal = BigDecimal.ZERO
//					.setScale(2, RoundingMode.HALF_EVEN);
//			BigDecimal totalGerado = BigDecimal.ZERO
//					.setScale(2, RoundingMode.HALF_EVEN);
//			for (ItemOrdemServico itemOrdemServico : mOrdem.getItens()) {
//				valorTotal = valorTotal.add(itemOrdemServico.getValorServico());
//			}
//			long prazo = (1_000 * 60 * 60 * 24) * mCondicaoPagamento
//					.getPeriodoEntreParcelas().longValue();
//			ArrayList<ContaReceber> parcelas = new ArrayList<>();
//			if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
//				if (mCondicaoPagamento.getQuantidadeParcelas().compareTo(BigInteger.ONE) < 1) {
//					ContaReceber contaReceber = ContaReceber.builder()
//							.condicaoPagamento(mCondicaoPagamento)
//							.numeroParcela(mCondicaoPagamento.getQuantidadeParcelas())
//							.ordem(mOrdem)
//							.sacado(mSacado)
//							.valor(valorTotal)
//							.build();
//					contaReceber
//							.setDataVencimento(new Date(Data.inicioDoDia().getTime() + prazo));
//					parcelas.add(contaReceber);
//					totalGerado = totalGerado.add(contaReceber.getValor());
//				} else {
//					BigDecimal valor = valorTotal
//							.divide(new BigDecimal(mCondicaoPagamento.getQuantidadeParcelas()), 2, RoundingMode.HALF_EVEN);
//					for (int numeroParcela = 1; numeroParcela <= mCondicaoPagamento.getQuantidadeParcelas().intValue(); numeroParcela++) {
//						ContaReceber contaReceber = ContaReceber.builder()
//								.condicaoPagamento(mCondicaoPagamento)
//								.numeroParcela(BigInteger.valueOf(numeroParcela))
//								.ordem(mOrdem)
//								.sacado(mSacado)
//								.valor(valor)
//								.build();
//						contaReceber
//								.setDataVencimento(new Date(Data.inicioDoDia().getTime() + (prazo * numeroParcela)));
//						parcelas.add(contaReceber);
//						totalGerado = totalGerado.add(contaReceber.getValor());
//					}
//				}
//			}
//			if (valorTotal.compareTo(totalGerado) != 0) {
//				totalGerado = BigDecimal.ZERO
//						.setScale(2, RoundingMode.HALF_EVEN);
//				for (int i = 0; i < parcelas.size() - 1; i++) {
//					totalGerado = totalGerado.add(parcelas.get(i).getValor());
//				}
//				ContaReceber ultimaParcela = parcelas.get(parcelas.size() - 1);
//				ultimaParcela.setValor(valorTotal.subtract(totalGerado));
//			}
//			mParcelas.clear();
//			mParcelas.addAll(parcelas);
//		}
//		preencherViewParcelas();
//	}

	private boolean isFormularioValido() {
		return
//				isClienteValido()
				isContaReceberValido()
						&& isFormaPagamentoValido()
						&& isCondicaoPagamentoValido()
						&& isJuroAplicadoValido()
						&& isDescontoConcedidoValido()
						&& isValorPagoValido();
//				&& isParcelasValido();
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

	private boolean isContaReceberValido() {
		if (mContaReceber == null) {
			mLabelContaReceber.setError(getString(R.string.input_obrigatorio));
			mScrollMovimentacaoContaReceberCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		if (mContaReceber.getSituacao().equals(ContaReceber.Situacao.BAIXADO)) {
			mLabelContaReceber
					.setError(getString(R.string.movimentacao_conta_receber_cadastro_mensagem_baixado));
			mScrollMovimentacaoContaReceberCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		if (mContaReceber.getSituacao().equals(ContaReceber.Situacao.CANCELADO)) {
			mLabelContaReceber
					.setError(getString(R.string.movimentacao_conta_receber_cadastro_mensagem_cancelado));
			mScrollMovimentacaoContaReceberCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		if (!mContaReceber.hasSaldoDevedor()) {
			mLabelContaReceber
					.setError(getString(R.string.movimentacao_conta_receber_cadastro_mensagem_saldo_devedor_0));
			mScrollMovimentacaoContaReceberCadastro.fullScroll(View.FOCUS_UP);
			return false;
		}
		mLabelContaReceber.setError(null);
		mLabelContaReceber.setErrorEnabled(false);
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
		mMovimentacaoContaReceber.setDescontoConcedido(descontoConcedido);
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
		mMovimentacaoContaReceber.setJuroAplicado(juroAplicado);
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
		if (mContaReceber.getSaldoDevedor().compareTo(valorPago) < 0) {
			valorPago = mContaReceber.getSaldoDevedor();
		}
		mMovimentacaoContaReceber.setValorPago(valorPago);
		mLabelValorPago.setError(null);
		mLabelValorPago.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mMovimentacaoContaReceber.setBaseCalculo(mMovimentacaoContaReceber.getContaReceber().getSaldoDevedor());
	}

	private void limparErros() {
		mLabelContaReceber.setError(null);
		mLabelFormaPagamento.setErrorEnabled(false);
		mLabelCondicaoPagamento.setError(null);
		mLabelJuroAplicado.setErrorEnabled(false);
		mLabelDescontoConcedido.setError(null);
		mLabelValorPago.setErrorEnabled(false);
	}

//	private boolean isParcelasValido() {
//		if (mParcelas == null) {
//			mLabelParcelas.setError(getString(R.string.input_obrigatorio));
//			return false;
//		}
//		if (mParcelas.isEmpty()) {
//			mLabelParcelas.setError(getString(R.string.input_invalido));
//			return false;
//		}
//		mLabelParcelas.setError(null);
//		mLabelParcelas.setErrorEnabled(false);
//		return true;
//	}

//	private boolean isClienteValido() {
//		if (mCliente == null) {
//			mLabelCliente.setError(getString(R.string.input_obrigatorio));
//			return false;
//		}
////		if (!mCliente.isPerfilCliente()) {
////			mLabelCliente
////					.setError(getString(R.string.conta_receber_cadastro_mensagem_sacado_nao_cliente));
////			return false;
////		}
//		mLabelCliente.setError(null);
//		mLabelCliente.setErrorEnabled(false);
//		return true;
//	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPressionarVoltar) {
//				if (mOnParcelasGeradoListener != null) {
//					mOnParcelasGeradoListener
//							.onParcelasGerado(mButtonGerar, mParcelas);
//				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
//		preencherViewCliente();
		preencherViewContaReceber();
		mLabelValorContaReceber.setText(mContaReceber.getValor() == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_valor_conta_receber_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_valor_conta_receber_hint) + " " + mContaReceber.getValor().setScale(2, RoundingMode.HALF_EVEN).toString());
		mLabelNumeroParcelaContaReceber.setText(mContaReceber.getNumeroParcela() == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_numero_parcela_conta_receber_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_numero_parcela_conta_receber_hint) + " " + mContaReceber.getNumeroParcela().toString());
		mLabelDataVencimentoContaReceber.setText(mContaReceber.getDataVencimento() == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_data_vencimento_conta_receber_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_data_vencimento_conta_receber_hint) + " " + Data.paraStringData(mContaReceber.getDataVencimento()));
		mLabelMontantePagoContaReceber.setText(mContaReceber.getMontantePago() == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_montante_pago_conta_receber_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_montante_pago_conta_receber_hint) + " " +mContaReceber.getMontantePago().setScale(2, RoundingMode.HALF_EVEN).toString());
		mLabelSaldoDevedorContaReceber.setText(mContaReceber.getSaldoDevedor() == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_saldo_devedor_conta_receber_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_saldo_devedor_conta_receber_hint) + " " + mContaReceber.getSaldoDevedor().setScale(2, RoundingMode.HALF_EVEN).toString());
		preencherViewFormaPagamento();
		preencherViewCondicaoPagamento();
//		preencherViewParcelas();
		mButtonEstornar.setVisibility((mContaReceber != null && mContaReceber.getId() != null && mContaReceber.hasMontantePago()) ? View.VISIBLE : View.GONE);
	}

	private void preencherViewCondicaoPagamento() {
		mLabelCondicaoPagamento.setHint(mCondicaoPagamento == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_condicao_pagamento_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_condicao_pagamento_hint));
		mInputCondicaoPagamento.setText(mCondicaoPagamento == null
				? "" : mCondicaoPagamento.toString());
	}

	private void preencherViewFormaPagamento() {
		mLabelFormaPagamento.setHint(mFormaPagamento == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_forma_pagamento_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_forma_pagamento_hint));
		mInputFormaPagamento.setText(mFormaPagamento == null
				? "" : mFormaPagamento.toString());
	}

	private void preencherViewContaReceber() {
		mLabelContaReceber.setHint(mContaReceber == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_conta_receber_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_conta_receber_hint));
		mInputContaReceber.setText(mContaReceber == null ? "" : mContaReceber.toString());
	}

//	private void preencherViewParcelas() {
//		mLabelParcelas.setHint(mParcelas == null || mParcelas.isEmpty()
//				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_parcela_hint)
//				: getString(R.string.movimentacao_conta_receber_cadastro_label_parcelas_hint));
//		mInputParcelas.setText(mParcelas == null || mParcelas.isEmpty()
//				? ""
//				: getString(R.string.movimentacao_conta_receber_cadastro_input_parcelas_text));
//	}

//	private void preencherViewCliente() {
//		mLabelCliente.setHint(mCliente == null
//				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_cliente_hint)
//				: getString(R.string.movimentacao_conta_receber_cadastro_label_cliente_hint));
//		mInputCliente.setText(mCliente == null ? "" : mCliente.toString());
//	}

	public void setCondicaoPagamento(@NonNull CondicaoPagamento condicaoPagamento) {
		mCondicaoPagamento = condicaoPagamento;
		mMovimentacaoContaReceber.setCondicaoPagamento(mCondicaoPagamento);
		if (getArguments() != null) {
			getArguments()
					.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		}
//		gerarParcelas();
//		calcularDescontoOuJuro();
		isCondicaoPagamentoValido();
	}

	public void setFormaPagamento(@NonNull FormaPagamento formaPagamento) {
		mFormaPagamento = formaPagamento;
		if (getArguments() != null) {
			getArguments().putSerializable(FORMA_PAGAMENTO, mFormaPagamento);
		}
		mCondicaoPagamento = null;
//		mParcelas.clear();
//		gerarParcelas();
//		calcularDescontoOuJuro();
		isFormaPagamentoValido();
	}

//	public void setOnClientesPesquisaListener(@Nullable OnClientesPesquisaListener ouvinte) {
//		mOnClientesPesquisaListener = ouvinte;
//	}

	public void setOnCondicoesPagamentoPesquisaListener(@Nullable OnCondicoesPagamentoPesquisaListener ouvinte) {
		mOnCondicoesPagamentoPesquisaListener = ouvinte;
	}

	public void setOnFormasPagamentoPesquisaListener(@Nullable OnFormasPagamentoPesquisaListener ouvinte) {
		mOnFormasPagamentoPesquisaListener = ouvinte;
	}

	public void setOnContasReceberPesquisaListener(@Nullable OnContasReceberPesquisaListener ouvinte) {
		mOnContasReceberPesquisaListener = ouvinte;
	}

//	public void setOnParcelasGeradoListener(@Nullable OnParcelasGeradoListener ouvinte) {
//		mOnParcelasGeradoListener = ouvinte;
//	}

//	public void setOnParcelasPesquisaListener(@Nullable OnParcelasPesquisaListener ouvinte) {
//		mOnParcelasPesquisaListener = ouvinte;
//	}

	public void setContaReceber(@NonNull ContaReceber contaReceber) {
		mContaReceber = contaReceber;
		mMovimentacaoContaReceber.setContaReceber(mContaReceber);
		if (getArguments() != null) {
			getArguments().putSerializable(CONTA_RECEBER, mContaReceber);
		}
//		gerarParcelas();
//		calcularDescontoOuJuro();
		isContaReceberValido();
	}

//	public void setCliente(@NonNull Pessoa cliente) {
//		mCliente = cliente;
//		if (getArguments() != null) {
//			getArguments().putSerializable(CLIENTE, mCliente);
//		}
////		gerarParcelas();
////		calcularDescontoOuJuro();
//		isClienteValido();
//	}

	private void submeterFormulario() {
		iterarFormulario();
		consumirMovimentacaoContaReceberPOST();
	}

//	public interface OnClientesPesquisaListener {
//
//		void onClientesPesquisa(@NonNull View view);
//	}

	public interface OnCondicoesPagamentoPesquisaListener {

		void onCondicoesPagamentoPesquisa(@NonNull View view,
		                                  @NonNull Set<CondicaoPagamento> condicoesPagamento);
	}

	public interface OnFormasPagamentoPesquisaListener {

		void onFormasPagamentoPesquisa(@NonNull View view);
	}

	public interface OnContasReceberPesquisaListener {

		void onContasReceberPesquisa(@NonNull View view);
	}

//	public interface OnParcelasGeradoListener {
//
//		void onParcelasGerado(@NonNull View view,
//		                      @NonNull ArrayList<ContaReceber> contasReceber);
//	}

//	public interface OnParcelasPesquisaListener {
//
//		void onParcelasPesquisa(@NonNull View view);
//	}
}
