package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.componente.DialogoCalendario;
import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.ContaPagarDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.ContaPagarMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.ContaPagarRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Data;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class ContaPagarCadastroFragment extends Fragment
		implements Serializable, TextView.OnEditorActionListener,
		View.OnClickListener, View.OnLongClickListener {
	private static final long serialVersionUID = -7636964916019642314L;
	private static final String CONTA_PAGAR = "contaPagar";
	private Pessoa mCedente;
	private ContaPagar mContaPagar;
	private OnCedentesPesquisaListener mOnCedentesPesquisaListener;
	private OnReferenciaContaPagarAlteradoListener mOnReferenciaContaPagarAlteradoListener;
	private boolean mPressionarVoltar;
	private int mTarefasPendentes;

	private AppCompatImageButton mButtonConsumirPorId;
	private AppCompatButton mButtonGerar;
	private TextInputEditText mInputCedente;
	private TextInputEditText mInputDataEmissao;
	private TextInputEditText mInputDataVencimento;
	private TextInputEditText mInputFatura;
	private TextInputEditText mInputId;
	private TextInputEditText mInputMesReferente;
	private TextInputEditText mInputNumeroParcela;
	private TextInputEditText mInputObservacao;
	private TextInputEditText mInputValor;
	private TextInputLayout mLabelCedente;
	private TextInputLayout mLabelDataEmissao;
	private TextInputLayout mLabelDataVencimento;
	private TextInputLayout mLabelFatura;
	private TextInputLayout mLabelId;
	private TextInputLayout mLabelMesReferente;
	private TextInputLayout mLabelNumeroParcela;
	private ProgressBar mProgressBarConsumirPorId;
	private ProgressBar mProgressBarGerar;
	private TextInputLayout mLabelValor;
	private ScrollView mScrollContaPagarCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ContaPagarCadastroFragment() {
	}

	public static ContaPagarCadastroFragment newInstance(@NonNull ContaPagar contaPagar) {
		ContaPagarCadastroFragment fragment = new ContaPagarCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(CONTA_PAGAR, contaPagar);
		fragment.setArguments(argumentos);
		return fragment;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_consumir_por_id:
				if (mContaPagar.getId() == null) {
					consumirContaPagarGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_gerar:
				if (isFormularioValido()) {
					submeterFormulario();
				} else {
					mScrollContaPagarCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.input_cedente:
				if (mOnCedentesPesquisaListener != null) {
					mOnCedentesPesquisaListener.onCedentesPesquisa(view);
				}
				break;
			case R.id.input_data_emissao:
				exibirCalendario(mInputDataEmissao);
				break;
			case R.id.input_data_vencimento:
				exibirCalendario(mInputDataVencimento);
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mContaPagar = (ContaPagar) savedInstanceState
					.getSerializable(CONTA_PAGAR);
		} else if (getArguments() != null) {
			mContaPagar = (ContaPagar) getArguments()
					.getSerializable(CONTA_PAGAR);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_conta_pagar_cadastro, container, false);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonGerar = view.findViewById(R.id.button_gerar);
		mInputCedente = view.findViewById(R.id.input_cedente);
		mInputDataEmissao = view.findViewById(R.id.input_data_emissao);
		mInputDataVencimento = view.findViewById(R.id.input_data_vencimento);
		mInputFatura = view.findViewById(R.id.input_fatura);
		mInputId = view.findViewById(R.id.input_id);
		mInputMesReferente = view.findViewById(R.id.input_mes_referente);
		mInputNumeroParcela = view.findViewById(R.id.input_numero_parcela);
		mInputObservacao = view.findViewById(R.id.input_observacao);
		mInputValor = view.findViewById(R.id.input_valor);
		mLabelCedente = view.findViewById(R.id.label_cedente);
		mLabelDataEmissao = view.findViewById(R.id.label_data_emissao);
		mLabelDataVencimento = view.findViewById(R.id.label_data_vencimento);
		mLabelFatura = view.findViewById(R.id.label_fatura);
		mLabelId = view.findViewById(R.id.label_id);
		mLabelMesReferente = view.findViewById(R.id.label_mes_referente);
		mLabelNumeroParcela = view.findViewById(R.id.label_numero_parcela);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mProgressBarGerar = view.findViewById(R.id.progress_bar_gerar);
		mLabelValor = view.findViewById(R.id.label_valor);
		mScrollContaPagarCadastro = view
				.findViewById(R.id.scroll_conta_pagar_cadastro);

		mButtonConsumirPorId.setOnClickListener(this);
		mButtonGerar.setOnClickListener(this);
		mInputCedente.setOnClickListener(this);
		mInputDataEmissao.setOnClickListener(this);
		mInputDataEmissao.setOnLongClickListener(this);
		mInputDataVencimento.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);

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
	public boolean onLongClick(View view) {
		switch (view.getId()) {
			case R.id.input_data_emissao:
				mInputDataEmissao.getText().clear();
				mContaPagar.setDataEmissao(null);
				break;
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.conta_pagar_cadastro_titulo);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CONTA_PAGAR, mContaPagar);
		getArguments().putSerializable(CONTA_PAGAR, mContaPagar);
	}

	private void alternarButtonGerar() {
		mButtonGerar.setHint(mContaPagar.getId() == null
				? R.string.conta_pagar_cadastro_button_gerar_hint
				: R.string.conta_pagar_cadastro_button_salvar_hint);
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonGerar.setVisibility(mContaPagar.getId() == null
				|| credencialLogado.isPerfilAdministrador()
				|| mContaPagar.getSituacao().equals(ContaPagar.Situacao.ABERTO)
				? View.VISIBLE : View.INVISIBLE);
	}

	private void alternarInputId() {
		mInputId.setEnabled(mContaPagar.getId() == null);
		mButtonConsumirPorId.setImageResource(mContaPagar.getId() == null
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
	}

	private ItemCallback<ContaPagarDTO> callbackContaPagarGETPorId() {
		return new ItemCallback<ContaPagarDTO>() {

			@Override
			public void onFailure(@NonNull Call<ContaPagarDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				// TODO: 10/10/17 usar fab
				Falha.tratar(mButtonGerar, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<ContaPagarDTO> chamada,
			                       @NonNull Response<ContaPagarDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					// TODO: 10/10/17 usar fab
					Falha.tratar(mButtonGerar, resposta);
					mInputId.getText().clear();
				} else {
					ContaPagarDTO dto = resposta.body();
					ContaPagar contaPagar = ContaPagarMapeamento
							.paraDominio(dto);
					setContaPagar(contaPagar);
					if (mOnReferenciaContaPagarAlteradoListener != null) {
						mOnReferenciaContaPagarAlteradoListener
								.onReferenciaContaPagarAlterado(mContaPagar);
					}
					preencherFormulario();
					mInputFatura.requestFocus();
					mScrollContaPagarCadastro.fullScroll(View.FOCUS_UP);

					consumirContaPagarGETCedente();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private VazioCallback callbackContaPagarPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
				// TODO: 10/10/17 user fab
				Falha.tratar(mButtonGerar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					// TODO: 10/10/17 usar fab
					Falha.tratar(mButtonGerar, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mContaPagar
							.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					preencherFormulario();
				}
				ocultarProgresso(mProgressBarGerar, mButtonGerar);
			}
		};
	}

	private void consumirContaPagarGETCedente() {
		// TODO: 9/21/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("contaspagar/%d/cedente");
		HRef href = new HRef(String.format(url, mContaPagar.getId()));
		++mTarefasPendentes;
		ContaPagarRequisicao.getCedente(callbackContaPagarGETCedente(), href);
	}

	private ItemCallback<PessoaDTO> callbackContaPagarGETCedente() {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				// TODO: 10/11/17 usar fab
				Falha.tratar(mButtonGerar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					// TODO: 10/11/17 usar fab
					Falha.tratar(mButtonGerar, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa cedente = PessoaMapeamento.paraDominio(dto);
					mContaPagar.setCedente(cedente);
					preencherViewCedente();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private void consumirContaPagarGETPorId() {
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
				ContaPagarRequisicao.getPorId(callbackContaPagarGETPorId(), id);
			}
		}
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
	}

	private void consumirContaPagarPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarGerar, mButtonGerar);
		Teclado.ocultar(getActivity(), mButtonGerar);
		ContaPagarDTO dto = ContaPagarMapeamento.paraDTO(mContaPagar);
		++mTarefasPendentes;
		ContaPagarRequisicao.post(callbackContaPagarPOST(), dto);
	}

	private void exibirCalendario(EditText view) {
		Date data = new Date(System.currentTimeMillis());
		if (!TextUtils.isEmpty(view.getText())) {
			try {
				data = Data.deStringData(view.getText().toString());
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
						.show();
			}
		}
		Calendar calendario = Calendar.getInstance(Locale.getDefault());
		calendario.setTime(data);
		DialogoCalendario
				.newInstance(view.getId(),
						calendario.get(Calendar.YEAR),
						calendario.get(Calendar.MONTH),
						calendario.get(Calendar.DAY_OF_MONTH))
				.show(getFragmentManager(), getString(R.string.conta_pagar_cadastro_label_calendario_hint));
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isCedenteValido() {
		if (mContaPagar.getCedente() == null) {
			mLabelCedente.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!mContaPagar.getCedente().isPerfilFornecedor()
				&& !mContaPagar.getCedente().isPerfilTransportador()) {
			mLabelCedente
					.setError(getString(R.string.conta_pagar_cadastro_mensagem_cedente_nao_fornecedor_nao_transportador));
			return false;
		}
		mLabelCedente.setError(null);
		mLabelCedente.setErrorEnabled(false);
		return true;
	}

	private boolean isDataEmissaoValido() {
		if (!TextUtils.isEmpty(mInputDataEmissao.getText())) {
			try {
				mContaPagar
						.setDataEmissao(Data.deStringData(mInputDataEmissao.getText().toString()));
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
						.show();
				mContaPagar.setDataEmissao(null);
				mLabelDataEmissao.setError(null);
				mLabelDataEmissao.setErrorEnabled(false);
				return true;
			}
			if (mContaPagar.getDataEmissao().compareTo(Data.fimDoDia()) > 0
					&& mContaPagar.getId() == null) {
				mLabelDataEmissao.setError(getString(R.string.input_invalido));
				return false;
			}
		}
		mLabelDataEmissao.setError(null);
		mLabelDataEmissao.setErrorEnabled(false);
		return true;
	}

	private boolean isDataVencimentoValido() {
		if (TextUtils.isEmpty(mInputDataVencimento.getText())) {
			mLabelDataVencimento
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		try {
			mContaPagar
					.setDataVencimento(Data.deStringData(mInputDataVencimento.getText().toString()));
		} catch (ParseException e) {
			Toast.makeText(getActivity(), getString(R.string.suporte_mensagem_conversao_data), Toast.LENGTH_LONG)
					.show();
			mLabelDataVencimento.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelDataVencimento.setError(null);
		mLabelDataVencimento.setErrorEnabled(false);
		return true;
	}

	private boolean isFaturaValido() {
		if (TextUtils.isEmpty(mInputFatura.getText())) {
			mLabelFatura.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mContaPagar.setFatura(mInputFatura.getText().toString());
		mLabelFatura.setError(null);
		mLabelFatura.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isCedenteValido()
				&& isDataEmissaoValido()
				&& isFaturaValido()
				&& isMesReferenteValido()
				&& isDataVencimentoValido()
				&& isNumeroParcelaValido()
				&& isValorValido();
	}

	private boolean isMesReferenteValido() {
		if (TextUtils.isEmpty(mInputMesReferente.getText())) {
			mLabelMesReferente.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (mInputMesReferente.getText().length() != 7
				|| !mInputMesReferente.getText().toString().contains("/")
				|| mInputMesReferente.getText().toString().lastIndexOf("/") != 2) {
			mLabelMesReferente.setError(getString(R.string.input_invalido));
			return false;
		}
		String mes = mInputMesReferente.getText().toString().substring(0, 2);
		String ano = mInputMesReferente.getText().toString().substring(3);
		mContaPagar.setMesReferente(ano + "-" + mes);
		mLabelMesReferente.setError(null);
		mLabelMesReferente.setErrorEnabled(false);
		return true;
	}

	private boolean isNumeroParcelaValido() {
		if (TextUtils.isEmpty(mInputNumeroParcela.getText())) {
			mLabelNumeroParcela.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigInteger parcela =
				new BigInteger(mInputNumeroParcela.getText().toString());
		if (parcela.compareTo(BigInteger.ZERO) < 0) {
			mLabelNumeroParcela.setError(getString(R.string.input_invalido));
			return false;
		}
		mContaPagar.setNumeroParcela(parcela);
		mLabelNumeroParcela.setError(null);
		mLabelNumeroParcela.setErrorEnabled(false);
		return true;
	}

	private boolean isValorValido() {
		if (TextUtils.isEmpty(mInputValor.getText())) {
			mLabelValor.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		BigDecimal valor = new BigDecimal(mInputValor.getText().toString())
				.setScale(2, RoundingMode.HALF_EVEN);
		if (valor.compareTo(BigDecimal.ZERO) < 0) {
			mLabelValor.setError(getString(R.string.input_invalido));
			return false;
		}
		mContaPagar.setValor(valor);
		mLabelValor.setError(null);
		mLabelValor.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mContaPagar.setObservacao(mInputObservacao.getText().toString());
	}

	private void limparErros() {
		mLabelId.setError(null);
		mLabelId.setErrorEnabled(false);
		mLabelCedente.setError(null);
		mLabelCedente.setErrorEnabled(false);
		mLabelDataEmissao.setError(null);
		mLabelDataEmissao.setErrorEnabled(false);
		mLabelFatura.setError(null);
		mLabelFatura.setErrorEnabled(false);
		mLabelMesReferente.setError(null);
		mLabelMesReferente.setErrorEnabled(false);
		mLabelDataVencimento.setError(null);
		mLabelDataVencimento.setErrorEnabled(false);
		mLabelNumeroParcela.setError(null);
		mLabelNumeroParcela.setErrorEnabled(false);
	}

	private void limparFormulario() {
		setContaPagar(ContaPagar.builder().numeroParcela(null).build());
		if (mOnReferenciaContaPagarAlteradoListener != null) {
			mOnReferenciaContaPagarAlteradoListener
					.onReferenciaContaPagarAlterado(mContaPagar);
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
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mContaPagar.getId() == null
				? "" : mContaPagar.getId().toString());
		preencherViewCedente();
		mInputDataEmissao.setText(mContaPagar.getDataEmissao() == null
				? "" : Data.paraStringData(mContaPagar.getDataEmissao()));
		mInputFatura.setText(mContaPagar.getFatura() == null
				? "" : mContaPagar.getFatura());
		preencherViewMesReferente();
		mInputDataVencimento.setText(mContaPagar.getDataVencimento() == null
				? "" : Data.paraStringData(mContaPagar.getDataVencimento()));
		mInputNumeroParcela.setText(mContaPagar.getNumeroParcela() == null
				? "" : mContaPagar.getNumeroParcela().toString());
		mInputValor.setText(mContaPagar.getValor() == null
				? ""
				: mContaPagar.getValor().setScale(2, RoundingMode.HALF_EVEN).toString());
		mInputObservacao.setText(mContaPagar.getObservacao() == null
				? "" : mContaPagar.getObservacao());
		alternarButtonGerar();
	}

	private void preencherViewCedente() {
		mLabelCedente.setHint(mContaPagar.getCedente() == null
				? getString(R.string.conta_pagar_cadastro_label_nenhum_cedente_hint)
				: getString(R.string.conta_pagar_cadastro_label_cedente_hint));
		mInputCedente.setText(mContaPagar.getCedente() == null
				? "" : mContaPagar.getCedente().toString());
	}

	private void preencherViewMesReferente() {
		if (mContaPagar.getMesReferente() != null) {
			String ano = mContaPagar.getMesReferente().substring(0, 4);
			String mes = mContaPagar.getMesReferente().substring(5);
			mInputMesReferente.setText(mes + "/" + ano);
		} else {
			mInputMesReferente.getText().clear();
		}
	}

	public void setCedente(@NonNull Pessoa cedente) {
		mCedente = cedente;
		mContaPagar.setCedente(mCedente);
		isCedenteValido();
	}

	public void setContaPagar(@NonNull ContaPagar contaPagar) {
		mContaPagar = contaPagar;
		if (getArguments() != null) {
			getArguments().putSerializable(CONTA_PAGAR, mContaPagar);
		}
	}

	public void setOnCedentesPesquisaListener(@Nullable OnCedentesPesquisaListener ouvinte) {
		mOnCedentesPesquisaListener = ouvinte;
	}

	public void setOnReferenciaContaPagarAlteradoListener(@Nullable OnReferenciaContaPagarAlteradoListener ouvinte) {
		mOnReferenciaContaPagarAlteradoListener = ouvinte;
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mContaPagar.getId() != null) {
//			consumirContaPagarPUT();
		} else {
			consumirContaPagarPOST();
		}
	}

	public interface OnCedentesPesquisaListener {

		void onCedentesPesquisa(@NonNull View view);
	}

	public interface OnReferenciaContaPagarAlteradoListener {

		void onReferenciaContaPagarAlterado(@NonNull ContaPagar novaReferencia);
	}
}
