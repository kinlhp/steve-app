package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
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
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
import com.kinlhp.steve.mapeamento.MovimentacaoContaReceberMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.MovimentacaoContaReceberRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class MovimentacaoContaReceberCadastroFragment extends Fragment
		implements Serializable, View.OnClickListener {
	private static final String CLIENTE = "cliente";
	private static final String CONDICAO_PAGAMENTO = "condicaoPagamento";
	private static final String FORMA_PAGAMENTO = "formaPagamento";
	private static final String ORDEM = "ordem";
	private Pessoa mCliente;
	private CondicaoPagamento mCondicaoPagamento;
	private FormaPagamento mFormaPagamento;
	private Ordem mOrdem;
	private ArrayList<ContaReceber> mParcelas = new ArrayList<>();
	private OnClientesPesquisaListener mOnClientesPesquisaListener;
	private OnCondicoesPagamentoPesquisaListener mOnCondicoesPagamentoPesquisaListener;
	private OnFormasPagamentoPesquisaListener mOnFormasPagamentoPesquisaListener;
	private OnOrdensPesquisaListener mOnOrdensPesquisaListener;
	private OnParcelasPesquisaListener mOnParcelasPesquisaListener;
	private OnParcelasGeradoListener mOnParcelasGeradoListener;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;

	private AppCompatButton mButtonGerar;
	private TextInputEditText mInputCliente;
	private TextInputEditText mInputCondicaoPagamento;
	private TextInputEditText mInputFormaPagamento;
	private TextInputEditText mInputOrdem;
	private TextInputEditText mInputParcelas;
	private TextInputLayout mLabelCliente;
	private TextInputLayout mLabelCondicaoPagamento;
	private TextInputLayout mLabelFormaPagamento;
	private TextInputLayout mLabelOrdem;
	private TextInputLayout mLabelParcelas;
	private ProgressBar mProgressBarGerar;
	private ScrollView mScrollMovimentacaoContaReceberCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public MovimentacaoContaReceberCadastroFragment() {
	}

	public static MovimentacaoContaReceberCadastroFragment newInstance() {
		MovimentacaoContaReceberCadastroFragment fragmento =
				new MovimentacaoContaReceberCadastroFragment();
		Bundle argumentos = new Bundle();
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_gerar:
				if (isFormularioValido()) {
					submeterFormulario();
				}
				break;
			case R.id.input_cliente:
				if (mOnClientesPesquisaListener != null) {
					mOnClientesPesquisaListener.onClientesPesquisa(view);
				}
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
			case R.id.input_ordem:
				if (mOnOrdensPesquisaListener != null) {
					mOnOrdensPesquisaListener.onOrdensPesquisa(view);
				}
				break;
			case R.id.input_parcelas:
				if (mOnParcelasPesquisaListener != null) {
					mOnParcelasPesquisaListener
							.onParcelasPesquisa(view, mParcelas);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCliente = (Pessoa) savedInstanceState.getSerializable(CLIENTE);
			mCondicaoPagamento = (CondicaoPagamento) savedInstanceState
					.getSerializable(CONDICAO_PAGAMENTO);
			mFormaPagamento = (FormaPagamento) savedInstanceState
					.getSerializable(FORMA_PAGAMENTO);
			mOrdem = (Ordem) savedInstanceState.getSerializable(ORDEM);
		}
		if (getArguments() != null) {
			mCliente = (Pessoa) getArguments().getSerializable(CLIENTE);
			mCondicaoPagamento = (CondicaoPagamento) getArguments
					().getSerializable(CONDICAO_PAGAMENTO);
			mFormaPagamento = (FormaPagamento) getArguments()
					.getSerializable(FORMA_PAGAMENTO);
			mOrdem = (Ordem) getArguments().getSerializable(ORDEM);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_movimentacao_conta_receber_cadastro, container, false);
		mButtonGerar = view.findViewById(R.id.button_gerar);
		mInputCliente = view.findViewById(R.id.input_cliente);
		mInputCondicaoPagamento = view
				.findViewById(R.id.input_condicao_pagamento);
		mInputFormaPagamento = view.findViewById(R.id.input_forma_pagamento);
		mInputOrdem = view.findViewById(R.id.input_ordem);
		mInputParcelas = view.findViewById(R.id.input_parcelas);
		mLabelCliente = view.findViewById(R.id.label_cliente);
		mLabelCondicaoPagamento = view
				.findViewById(R.id.label_condicao_pagamento);
		mLabelFormaPagamento = view.findViewById(R.id.label_forma_pagamento);
		mLabelOrdem = view.findViewById(R.id.label_ordem);
		mLabelParcelas = view.findViewById(R.id.label_parcelas);
		mProgressBarGerar = view.findViewById(R.id.progress_bar_gerar);
		mScrollMovimentacaoContaReceberCadastro = view
				.findViewById(R.id.scroll_movimentacao_conta_receber_cadastro);

		mButtonGerar.setOnClickListener(this);
		mInputCliente.setOnClickListener(this);
		mInputCondicaoPagamento.setOnClickListener(this);
		mInputFormaPagamento.setOnClickListener(this);
		mInputOrdem.setOnClickListener(this);
		mInputParcelas.setOnClickListener(this);

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
		outState.putSerializable(CLIENTE, mCliente);
		outState.putSerializable(CONDICAO_PAGAMENTO, mCondicaoPagamento);
		outState.putSerializable(FORMA_PAGAMENTO, mFormaPagamento);
		outState.putSerializable(ORDEM, mOrdem);
	}

	private VazioCallback callbackMovimentacaoContaReceberPOST(@NonNull MovimentacaoContaReceber movimentacaoContaReceber) {
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
					movimentacaoContaReceber.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
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

	private void consumirMovimentacaoContaReceberPOST(@NonNull MovimentacaoContaReceber movimentacaoContaReceber) {
		MovimentacaoContaReceberDTO dto = MovimentacaoContaReceberMapeamento
				.paraDTO(movimentacaoContaReceber);
		++mTarefasPendentes;
		MovimentacaoContaReceberRequisicao
				.post(callbackMovimentacaoContaReceberPOST(movimentacaoContaReceber), dto);
	}

//	private void consumirOrdemPUTSituacao() {
//		mSituacaoAnterior = mOrdem.getSituacao();
//		mOrdem.setSituacao(Ordem.Situacao.GERADO);
//		OrdemDTO dto = OrdemMapeamento.paraDTO(mOrdem);
//		++mTarefasPendentes;
//		OrdemRequisicao.put(callbackOrdemPUTSituacao(), mOrdem.getId(), dto);
//	}

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
		return isClienteValido()
				&& isOrdemValido()
				&& isFormaPagamentoValido()
				&& isCondicaoPagamentoValido()
				&& isParcelasValido();
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

	private boolean isOrdemValido() {
		if (mOrdem == null) {
			mLabelOrdem.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!mOrdem.getSituacao().equals(Ordem.Situacao.FINALIZADO)) {
			mLabelOrdem
					.setError(getString(R.string.conta_receber_cadastro_mensagem_ordem_nao_finalizado));
			return false;
		}
		mLabelOrdem.setError(null);
		mLabelOrdem.setErrorEnabled(false);
		return true;
	}

	private boolean isParcelasValido() {
		if (mParcelas == null) {
			mLabelParcelas.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (mParcelas.isEmpty()) {
			mLabelParcelas.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelParcelas.setError(null);
		mLabelParcelas.setErrorEnabled(false);
		return true;
	}

	private boolean isClienteValido() {
		if (mCliente == null) {
			mLabelCliente.setError(getString(R.string.input_obrigatorio));
			return false;
		}
//		if (!mCliente.isPerfilCliente()) {
//			mLabelCliente
//					.setError(getString(R.string.conta_receber_cadastro_mensagem_sacado_nao_cliente));
//			return false;
//		}
		mLabelCliente.setError(null);
		mLabelCliente.setErrorEnabled(false);
		return true;
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPressionarVoltar) {
				if (mOnParcelasGeradoListener != null) {
					mOnParcelasGeradoListener
							.onParcelasGerado(mButtonGerar, mParcelas);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		preencherViewCliente();
		preencherViewOrdem();
		preencherViewFormaPagamento();
		preencherViewCondicaoPagamento();
		preencherViewParcelas();
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

	private void preencherViewOrdem() {
		mLabelOrdem.setHint(mOrdem == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_ordem_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_ordem_hint));
		mInputOrdem.setText(mOrdem == null ? "" : mOrdem.toString());
	}

	private void preencherViewParcelas() {
		mLabelParcelas.setHint(mParcelas == null || mParcelas.isEmpty()
				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_parcela_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_parcelas_hint));
		mInputParcelas.setText(mParcelas == null || mParcelas.isEmpty()
				? ""
				: getString(R.string.movimentacao_conta_receber_cadastro_input_parcelas_text));
	}

	private void preencherViewCliente() {
		mLabelCliente.setHint(mCliente == null
				? getString(R.string.movimentacao_conta_receber_cadastro_label_nenhum_cliente_hint)
				: getString(R.string.movimentacao_conta_receber_cadastro_label_cliente_hint));
		mInputCliente.setText(mCliente == null ? "" : mCliente.toString());
	}

	public void setCondicaoPagamento(@NonNull CondicaoPagamento condicaoPagamento) {
		mCondicaoPagamento = condicaoPagamento;
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
		mParcelas.clear();
//		gerarParcelas();
//		calcularDescontoOuJuro();
		isFormaPagamentoValido();
	}

	public void setOnClientesPesquisaListener(@Nullable OnClientesPesquisaListener ouvinte) {
		mOnClientesPesquisaListener = ouvinte;
	}

	public void setOnCondicoesPagamentoPesquisaListener(@Nullable OnCondicoesPagamentoPesquisaListener ouvinte) {
		mOnCondicoesPagamentoPesquisaListener = ouvinte;
	}

	public void setOnFormasPagamentoPesquisaListener(@Nullable OnFormasPagamentoPesquisaListener ouvinte) {
		mOnFormasPagamentoPesquisaListener = ouvinte;
	}

	public void setOnOrdensPesquisaListener(@Nullable OnOrdensPesquisaListener ouvinte) {
		mOnOrdensPesquisaListener = ouvinte;
	}

	public void setOnParcelasGeradoListener(@Nullable OnParcelasGeradoListener ouvinte) {
		mOnParcelasGeradoListener = ouvinte;
	}

	public void setOnParcelasPesquisaListener(@Nullable OnParcelasPesquisaListener ouvinte) {
		mOnParcelasPesquisaListener = ouvinte;
	}

	public void setOrdem(@NonNull Ordem ordem) {
		mOrdem = ordem;
		if (getArguments() != null) {
			getArguments().putSerializable(ORDEM, mOrdem);
		}
//		gerarParcelas();
//		calcularDescontoOuJuro();
		isOrdemValido();
	}

	public void setCliente(@NonNull Pessoa cliente) {
		mCliente = cliente;
		if (getArguments() != null) {
			getArguments().putSerializable(CLIENTE, mCliente);
		}
//		gerarParcelas();
//		calcularDescontoOuJuro();
		isClienteValido();
	}

	private void submeterFormulario() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarGerar, mButtonGerar);
		Teclado.ocultar(getActivity(), mButtonGerar);
		for (ContaReceber contaReceber : mParcelas) {
			if (contaReceber.getId() == null) {
//				consumirContaReceberPOST(contaReceber);
			}
		}
		ocultarProgresso(mProgressBarGerar, mButtonGerar);
	}

	public interface OnClientesPesquisaListener {

		void onClientesPesquisa(@NonNull View view);
	}

	public interface OnCondicoesPagamentoPesquisaListener {

		void onCondicoesPagamentoPesquisa(@NonNull View view,
		                                  @NonNull Set<CondicaoPagamento> condicoesPagamento);
	}

	public interface OnFormasPagamentoPesquisaListener {

		void onFormasPagamentoPesquisa(@NonNull View view);
	}

	public interface OnOrdensPesquisaListener {

		void onOrdensPesquisa(@NonNull View view);
	}

	public interface OnParcelasGeradoListener {

		void onParcelasGerado(@NonNull View view,
		                      @NonNull ArrayList<ContaReceber> contasReceber);
	}

	public interface OnParcelasPesquisaListener {

		void onParcelasPesquisa(@NonNull View view,
		                        @NonNull ArrayList<ContaReceber> contasReceber);
	}
}
