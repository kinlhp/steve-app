package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.componente.DialogoCalendario;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.dominio.Servico;
import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.mapeamento.ItemOrdemServicoMapeamento;
import com.kinlhp.steve.mapeamento.ServicoMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.ItemOrdemServicoRequisicao;
import com.kinlhp.steve.requisicao.ServicoRequisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Data;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ItemOrdemServicoCadastroFragment extends Fragment
		implements View.OnClickListener, View.OnFocusChangeListener,
		AdapterView.OnItemSelectedListener, Serializable,
		View.OnLongClickListener {
	private static final long serialVersionUID = -4448003762998380020L;
	private static final String ITEM_ORDEM_SERVICO = "itemOrdemServico";
	private static final String ITEM_ORDEM_SERVICO_AUXILIAR =
			"itemOrdemServicoAuxiliar";
	private AdaptadorSpinner<Servico> mAdaptadorServicos;
	private AdaptadorSpinner<ItemOrdemServico.Situacao> mAdaptadorSituacoes;
	private ItemOrdemServico mItemOrdemServico;
	private OnItemOrdemServicoAdicionadoListener mOnItemOrdemServicoAdicionadoListener;
	private ItemOrdemServico mItemOrdemServicoAuxiliar;
	private boolean mPressionarVoltar;
	private ArrayList<Servico> mServicos;
	private ArrayList<ItemOrdemServico.Situacao> mSituacoes;
	private int mTarefasPendentes;

	private AppCompatButton mButtonAdicionar;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirServicos;
	private AppCompatSpinner mSpinnerServico;
	private TextInputEditText mInputValorOrcamento;
	private TextInputLayout mLabelValorOrcamento;
	private TextInputEditText mInputValorServico;
	private TextInputLayout mLabelValorServico;
	private TextInputEditText mInputDataFinalizacaoPrevista;
	private TextInputLayout mLabelDataFinalizacaoPrevista;
	private AppCompatSpinner mSpinnerSituacao;
	private TextInputEditText mInputDescricao;
	private ScrollView mScrollItemOrdemServicoCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ItemOrdemServicoCadastroFragment() {
	}

	public static ItemOrdemServicoCadastroFragment newInstance(@NonNull ItemOrdemServico itemOrdemServico) {
		ItemOrdemServicoCadastroFragment fragmento =
				new ItemOrdemServicoCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(ITEM_ORDEM_SERVICO, itemOrdemServico);
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
					mScrollItemOrdemServicoCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.input_data_finalizacao_prevista:
				exibirCalendario();
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mItemOrdemServicoAuxiliar = (ItemOrdemServico) savedInstanceState
					.getSerializable(ITEM_ORDEM_SERVICO_AUXILIAR);
		}
		if (getArguments() != null) {
			mItemOrdemServico = (ItemOrdemServico) getArguments()
					.getSerializable(ITEM_ORDEM_SERVICO);
		}
		if (mItemOrdemServicoAuxiliar == null) {
			mItemOrdemServicoAuxiliar = ItemOrdemServico.builder()
					.situacao(null).build();
			transcreverItemOrdemServico();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_item_ordem_servico_cadastro, container, false);
		mScrollItemOrdemServicoCadastro = (ScrollView) view;
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputValorOrcamento = view.findViewById(R.id.input_valor_orcamento);
		mInputValorServico = view.findViewById(R.id.input_valor_servico);
		mInputDataFinalizacaoPrevista = view
				.findViewById(R.id.input_data_finalizacao_prevista);
		mInputDescricao = view.findViewById(R.id.input_descricao);
		mLabelValorOrcamento = view.findViewById(R.id.label_valor_orcamento);
		mLabelValorServico = view.findViewById(R.id.label_valor_servico);
		mLabelDataFinalizacaoPrevista = view
				.findViewById(R.id.label_data_finalizacao_prevista);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirServicos = view
				.findViewById(R.id.progress_bar_consumir_servicos);
		mSpinnerServico = view.findViewById(R.id.spinner_servico);
		mSpinnerSituacao = view.findViewById(R.id.spinner_situacao);

		mSituacoes =
				new ArrayList<>(Arrays.asList(ItemOrdemServico.Situacao.values()));
		mAdaptadorSituacoes = new AdaptadorSpinner<>(getActivity(), mSituacoes);
		mSpinnerSituacao.setAdapter(mAdaptadorSituacoes);

		mAdaptadorServicos =
				new AdaptadorSpinner<>(getActivity(), new ArrayList<>());
		mSpinnerServico.setAdapter(mAdaptadorServicos);

		mButtonAdicionar.setOnClickListener(this);
		mInputDataFinalizacaoPrevista.setOnClickListener(this);
		mInputDataFinalizacaoPrevista.setOnLongClickListener(this);
		mInputValorOrcamento.setOnFocusChangeListener(this);
		mInputValorServico.setOnFocusChangeListener(this);
		mInputDataFinalizacaoPrevista.setOnFocusChangeListener(this);
		mSpinnerServico.setOnItemSelectedListener(this);

		mScrollItemOrdemServicoCadastro.fullScroll(View.FOCUS_UP);

		return view;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_valor_orcamento:
				if (!focused) {
					isValorOrcamentoValido();
				}
				break;
			case R.id.input_valor_servico:
				if (!focused) {
					isValorServicoValido();
				}
				break;
			case R.id.input_data_finalizacao_prevista:
				if (!focused) {
					isDataFinalizacaoPrevistaValido();
				}
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	                           long id) {
		switch (parent.getId()) {
			case R.id.spinner_servico:
				mItemOrdemServicoAuxiliar
						.setServico((Servico) parent.getItemAtPosition(position));
				break;
			case R.id.spinner_situacao:
				mItemOrdemServicoAuxiliar
						.setSituacao((ItemOrdemServico.Situacao) parent.getItemAtPosition(position));
				break;
		}
	}

	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
			case R.id.input_data_finalizacao_prevista:
				mInputDataFinalizacaoPrevista.getText().clear();
				break;
		}
		return true;
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
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
		getActivity().setTitle(R.string.item_ordem_servico_cadastro_titulo);
		consumirServicosGET();
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ITEM_ORDEM_SERVICO, mItemOrdemServico);
		outState.putSerializable(ITEM_ORDEM_SERVICO_AUXILIAR, mItemOrdemServicoAuxiliar);
	}

	private void alternarButtonAdicionar() {
		// TODO: 9/15/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<ItemOrdemServico> itensOrdemServico =
				new ArrayList<>(mItemOrdemServico.getOrdem().getItensOrdemServico());
		if (itensOrdemServico.contains(mItemOrdemServico)) {
			mButtonAdicionar.setHint(mItemOrdemServico.getId() == null
					? R.string.item_ordem_servico_cadastro_button_alterar_hint
					: R.string.item_ordem_servico_cadastro_button_salvar_hint);
		}

		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(mItemOrdemServico.getId() == null
				|| credencialLogado.isPerfilAdministrador()
				|| mItemOrdemServico.getSituacao().equals(ItemOrdemServico.Situacao.ABERTO)
				? View.VISIBLE : View.INVISIBLE);
	}

	private VazioCallback callbackItemOrdemServicoPUT() {
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
					transcreverItemOrdemServicoAuxiliar();
					consumirItemOrdemServicoPUTServico();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackItemOrdemServicoPUTServico() {
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
					transcreverServicoItemOrdemServicoAuxiliar();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private ColecaoCallback<Colecao<ServicoDTO>> callbackServicosGET() {
		return new ColecaoCallback<Colecao<ServicoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ServicoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirServicos, mSpinnerServico);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ServicoDTO>> chamada,
			                       @NonNull Response<Colecao<ServicoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					Set<ServicoDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Servico> servicos = ServicoMapeamento
							.paraDominios(dtos);
					mServicos = new ArrayList<>(servicos);
					mAdaptadorServicos.addAll(mServicos);
					mAdaptadorServicos.notifyDataSetChanged();
					mSpinnerServico
							.setSelection(mItemOrdemServicoAuxiliar.getServico() == null
									? 0
									: mAdaptadorServicos.getPosition(mItemOrdemServicoAuxiliar.getServico()));
				}
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarConsumirServicos, mSpinnerServico);
			}
		};
	}

	private void consumirItemOrdemServicoPUT() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		ItemOrdemServicoDTO dto = ItemOrdemServicoMapeamento
				.paraDTO(mItemOrdemServicoAuxiliar);
		++mTarefasPendentes;
		ItemOrdemServicoRequisicao
				.put(callbackItemOrdemServicoPUT(), mItemOrdemServico.getId(), dto);
	}

	private void consumirItemOrdemServicoPUTServico() {
		// TODO: 9/22/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("servicos/%d");
		// TODO: 9/22/17 corrigir hard-coded
		RequestBody uriList = RequestBody
				.create(MediaType.parse("text/uri-list"), String.format(url, mItemOrdemServicoAuxiliar.getServico().getId()));
		++mTarefasPendentes;
		ItemOrdemServicoRequisicao
				.putServico(callbackItemOrdemServicoPUTServico(), mItemOrdemServico.getId(), uriList);
	}

	private void consumirServicosGET() {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarConsumirServicos, null);
		mSpinnerServico.setVisibility(View.INVISIBLE);
		Teclado.ocultar(getActivity(), mSpinnerServico);
		++mTarefasPendentes;
		ServicoRequisicao.get(callbackServicosGET());
	}

	private void exibirCalendario() {
		Date data = new Date(System.currentTimeMillis());
		if (!TextUtils.isEmpty(mInputDataFinalizacaoPrevista.getText())) {
			try {
				data = Data
						.deStringData(mInputDataFinalizacaoPrevista.getText().toString());
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
						.show();
			}
		}
		Calendar calendario = Calendar.getInstance(Locale.getDefault());
		calendario.setTime(data);
		DialogoCalendario
				.newInstance(mInputDataFinalizacaoPrevista.getId(),
						calendario.get(Calendar.YEAR),
						calendario.get(Calendar.MONTH),
						calendario.get(Calendar.DAY_OF_MONTH))
				.show(getFragmentManager(), getString(R.string.ordem_cadastro_label_calendario_hint));
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isDataFinalizacaoPrevistaValido() {
		if (!TextUtils.isEmpty(mInputDataFinalizacaoPrevista.getText())) {
			Date data = new Date(System.currentTimeMillis());
			try {
				data = Data
						.deStringData(mInputDataFinalizacaoPrevista.getText().toString());
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
						.show();
			}
			if (data.compareTo(Data.inicioDoDia()) < 0) {
				mLabelDataFinalizacaoPrevista
						.setError(getString(R.string.input_invalido));
				return false;
			}
		}
		mLabelDataFinalizacaoPrevista.setError(null);
		mLabelDataFinalizacaoPrevista.setErrorEnabled(false);
		return true;
	}

	private boolean isValorOrcamentoValido() {
		if (TextUtils.isEmpty(mInputValorOrcamento.getText())) {
			mLabelValorOrcamento
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigDecimal valor =
				new BigDecimal(mInputValorOrcamento.getText().toString())
						.setScale(2, RoundingMode.HALF_EVEN);
		if (valor.compareTo(BigDecimal.ZERO) < 0) {
			mLabelValorOrcamento.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelValorOrcamento.setError(null);
		mLabelValorOrcamento.setErrorEnabled(false);
		return true;
	}

	private boolean isValorServicoValido() {
		if (TextUtils.isEmpty(mInputValorServico.getText())) {
			mLabelValorServico.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigDecimal valor =
				new BigDecimal(mInputValorServico.getText().toString())
						.setScale(2, RoundingMode.HALF_EVEN);
		if (valor.compareTo(BigDecimal.ZERO) < 0) {
			mLabelValorServico.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelValorServico.setError(null);
		mLabelValorServico.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isValorOrcamentoValido()
				&& isValorServicoValido()
				&& isDataFinalizacaoPrevistaValido();
	}

	private void iterarDataFinalizacaoPrevista() throws ParseException {
		mItemOrdemServicoAuxiliar
				.setDataFinalizacaoPrevista(TextUtils.isEmpty(mInputDataFinalizacaoPrevista.getText())
						? null
						: Data.deStringData(mInputDataFinalizacaoPrevista.getText().toString()));
	}

	private void iterarFormulario() {
		mItemOrdemServicoAuxiliar
				.setValorOrcamento(TextUtils.isEmpty(mInputValorOrcamento.getText())
						? BigDecimal.ZERO
						: new BigDecimal(mInputValorOrcamento.getText().toString()));
		mItemOrdemServicoAuxiliar
				.setValorServico(TextUtils.isEmpty(mInputValorServico.getText())
						? BigDecimal.ZERO
						: new BigDecimal(mInputValorServico.getText().toString()));
		try {
			iterarDataFinalizacaoPrevista();
		} catch (ParseException e) {
			Snackbar.make(mButtonAdicionar, getString(R.string.suporte_mensagem_conversao_data), Snackbar.LENGTH_LONG)
					.show();
		}
		mItemOrdemServicoAuxiliar
				.setSituacao((ItemOrdemServico.Situacao) mSpinnerSituacao.getSelectedItem());
		mItemOrdemServicoAuxiliar
				.setDescricao(mInputDescricao.getText().toString());
	}

	private void limitarSituacoes() {
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		if (!credencialLogado.isPerfilAdministrador()
				&& mItemOrdemServico.getId() != null) {
			switch (mItemOrdemServico.getSituacao()) {
				case CANCELADO:
					mAdaptadorSituacoes.clear();
					mAdaptadorSituacoes
							.add(ItemOrdemServico.Situacao.CANCELADO);
					break;
				case FINALIZADO:
					mAdaptadorSituacoes.clear();
					mAdaptadorSituacoes
							.addAll(ItemOrdemServico.Situacao.FINALIZADO);
					break;
			}
			mAdaptadorSituacoes.notifyDataSetChanged();
		}
	}

	private void limparErros() {
		mLabelValorOrcamento.setError(null);
		mLabelValorOrcamento.setErrorEnabled(false);
		mLabelValorServico.setError(null);
		mLabelValorServico.setErrorEnabled(false);
		mLabelDataFinalizacaoPrevista.setError(null);
		mLabelDataFinalizacaoPrevista.setErrorEnabled(false);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
			if (mPressionarVoltar) {
				if (mOnItemOrdemServicoAdicionadoListener != null) {
					mOnItemOrdemServicoAdicionadoListener
							.onItemOrdemServicoAdicionado(mButtonAdicionar, mItemOrdemServico);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		mSpinnerServico
				.setSelection(mItemOrdemServicoAuxiliar.getServico() == null
						? 0
						: mAdaptadorServicos.getPosition(mItemOrdemServicoAuxiliar.getServico()));
		mInputValorOrcamento
				.setText(mItemOrdemServicoAuxiliar.getValorOrcamento() == null
						? ""
						: mItemOrdemServicoAuxiliar.getValorOrcamento().setScale(2, RoundingMode.HALF_EVEN).toString());
		mInputValorServico
				.setText(mItemOrdemServicoAuxiliar.getValorServico() == null
						? ""
						: mItemOrdemServicoAuxiliar.getValorServico().setScale(2, RoundingMode.HALF_EVEN).toString());
		mInputDataFinalizacaoPrevista
				.setText(mItemOrdemServicoAuxiliar.getDataFinalizacaoPrevista() == null
						? ""
						: Data.paraStringData(mItemOrdemServicoAuxiliar.getDataFinalizacaoPrevista()));
		mSpinnerSituacao
				.setSelection(mItemOrdemServicoAuxiliar.getSituacao() == null
						? 0
						: mAdaptadorSituacoes.getPosition(mItemOrdemServicoAuxiliar.getSituacao()));
		mInputDescricao.setText(mItemOrdemServicoAuxiliar.getDescricao() == null
				? "" : mItemOrdemServicoAuxiliar.getDescricao());
		limitarSituacoes();
		alternarButtonAdicionar();
	}

	public void setItemOrdemServico(@NonNull ItemOrdemServico itemOrdemServico) {
		mItemOrdemServico = itemOrdemServico;
		if (getArguments() != null) {
			getArguments()
					.putSerializable(ITEM_ORDEM_SERVICO, mItemOrdemServico);
		}
		if (mItemOrdemServicoAuxiliar != null) {
			transcreverItemOrdemServico();
		}
	}

	public void setOnItemOrdemServicoAdicionadoListener(@Nullable OnItemOrdemServicoAdicionadoListener ouvinte) {
		mOnItemOrdemServicoAdicionadoListener = ouvinte;
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mItemOrdemServico.getId() != null) {
			consumirItemOrdemServicoPUT();
		} else {
			transcreverItemOrdemServicoAuxiliar();
			if (mOnItemOrdemServicoAdicionadoListener != null) {
				mOnItemOrdemServicoAdicionadoListener
						.onItemOrdemServicoAdicionado(mButtonAdicionar, mItemOrdemServico);
			}
			getActivity().onBackPressed();
		}
	}

	private void transcreverItemOrdemServico() {
		mItemOrdemServicoAuxiliar
				.setDataFinalizacaoPrevista(mItemOrdemServico.getDataFinalizacaoPrevista());
		mItemOrdemServicoAuxiliar
				.setDescricao(mItemOrdemServico.getDescricao());
		mItemOrdemServicoAuxiliar.setId(mItemOrdemServico.getId());
		mItemOrdemServicoAuxiliar.setOrdem(mItemOrdemServico.getOrdem());
		mItemOrdemServicoAuxiliar.setServico(mItemOrdemServico.getServico());
		mItemOrdemServicoAuxiliar.setSituacao(mItemOrdemServico.getSituacao());
		mItemOrdemServicoAuxiliar
				.setValorOrcamento(mItemOrdemServico.getValorOrcamento());
		mItemOrdemServicoAuxiliar
				.setValorServico(mItemOrdemServico.getValorServico());
	}

	private void transcreverItemOrdemServicoAuxiliar() {
		mItemOrdemServico
				.setDataFinalizacaoPrevista(mItemOrdemServicoAuxiliar.getDataFinalizacaoPrevista());
		mItemOrdemServico
				.setDescricao(mItemOrdemServicoAuxiliar.getDescricao());
		mItemOrdemServico.setServico(mItemOrdemServicoAuxiliar.getServico());
		mItemOrdemServico.setSituacao(mItemOrdemServicoAuxiliar.getSituacao());
		mItemOrdemServico
				.setValorOrcamento(mItemOrdemServicoAuxiliar.getValorOrcamento());
		mItemOrdemServico
				.setValorServico(mItemOrdemServicoAuxiliar.getValorServico());
	}

	private void transcreverServicoItemOrdemServicoAuxiliar() {
		mItemOrdemServico.setServico(mItemOrdemServicoAuxiliar.getServico());
	}

	public interface OnItemOrdemServicoAdicionadoListener {

		void onItemOrdemServicoAdicionado(@NonNull View view,
		                                  @NonNull ItemOrdemServico itemOrdemServico);
	}
}
