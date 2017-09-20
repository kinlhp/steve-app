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
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Servico;
import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.mapeamento.ServicoMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.requisicao.ServicoRequisicao;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.Response;

public class ServicoCadastroFragment extends Fragment
		implements View.OnClickListener, TextView.OnEditorActionListener,
		View.OnFocusChangeListener, Serializable {
	private static final long serialVersionUID = -5532241873151236142L;
	private static final String SERVICO = "servico";
	private static final String SERVICO_AUXILIAR = "servicoAuxiliar";
	private OnReferenciaServicoAlteradoListener mOnReferenciaServicoAlteradoListener;
	private OnServicoAdicionadoListener mOnServicoAdicionadoListener;
	private OnServicosPesquisaListener mOnServicosPesquisaListener;
	private boolean mPressionarVoltar;
	private Servico mServico;
	private Servico mServicoAuxiliar;
	private int mTarefasPendentes;

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonServicosPesquisa;
	private TextInputEditText mInputId;
	private TextInputEditText mInputDescricao;
	private TextInputLayout mLabelId;
	private TextInputLayout mLabelDescricao;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirPorId;
	private ProgressBar mProgressBarConsumirServicos;
	private ScrollView mScrollServicoCadastro;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ServicoCadastroFragment() {
	}

	public static ServicoCadastroFragment newInstance(@NonNull Servico servico) {
		ServicoCadastroFragment fragmento = new ServicoCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(SERVICO, servico);
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
					mScrollServicoCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.button_consumir_por_id:
				if (mServico.getId() == null) {
					consumirServicoGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_servicos_pesquisa:
				if (mOnServicosPesquisaListener != null) {
					mOnServicosPesquisaListener.onServicosPesquisa(view);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mServicoAuxiliar = (Servico) savedInstanceState
					.getSerializable(SERVICO_AUXILIAR);
		}
		if (getArguments() != null) {
			mServico = (Servico) getArguments().getSerializable(SERVICO);
		}
		if (mServicoAuxiliar == null) {
			mServicoAuxiliar = Servico.builder().build();
			transcreverServico();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_servico_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonServicosPesquisa = view
				.findViewById(R.id.button_servicos_pesquisa);
		mInputId = view.findViewById(R.id.input_id);
		mInputDescricao = view.findViewById(R.id.input_descricao);
		mLabelId = view.findViewById(R.id.label_id);
		mLabelDescricao = view.findViewById(R.id.label_descricao);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mProgressBarConsumirServicos = view
				.findViewById(R.id.progress_bar_consumir_servicos);
		mScrollServicoCadastro = view
				.findViewById(R.id.scroll_servico_cadastro);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirPorId.setOnClickListener(this);
		mButtonServicosPesquisa.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputDescricao.setOnFocusChangeListener(this);

		mInputDescricao.requestFocus();
		mScrollServicoCadastro.fullScroll(View.FOCUS_UP);

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
		getActivity().setTitle(R.string.servico_cadastro_titulo);
		mButtonServicosPesquisa.setVisibility(View.VISIBLE);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(SERVICO, mServico);
		outState.putSerializable(SERVICO_AUXILIAR, mServicoAuxiliar);
	}

	private void alternarButtonAdicionar() {
		mButtonAdicionar.setHint(mServico.getId() == null
				? R.string.servico_cadastro_button_adicionar_hint
				: R.string.servico_cadastro_button_salvar_hint);
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(credencialLogado.isPerfilAdministrador()
				? View.VISIBLE : View.INVISIBLE);
	}

	private void alternarInputId() {
		mInputId.setEnabled(mServico.getId() == null);
		mButtonConsumirPorId.setImageResource(mServico.getId() == null
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
	}

	private ItemCallback<ServicoDTO> callbackServicoGETPorId() {
		return new ItemCallback<ServicoDTO>() {

			@Override
			public void onFailure(@NonNull Call<ServicoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonServicosPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<ServicoDTO> chamada,
			                       @NonNull Response<ServicoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonServicosPesquisa, resposta);
					mInputId.getText().clear();
				} else {
					ServicoDTO dto = resposta.body();
					Servico servico = ServicoMapeamento.paraDominio(dto);
					setServico(servico);
					if (mOnReferenciaServicoAlteradoListener != null) {
						mOnReferenciaServicoAlteradoListener
								.onReferenciaServicoAlterado(mServico);
					}
					preencherFormulario();
					mInputDescricao.requestFocus();
					mScrollServicoCadastro.fullScroll(View.FOCUS_UP);
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private VazioCallback callbackServicoPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonServicosPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonServicosPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mServico.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					transcreverServicoAuxiliar();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackServicoPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonServicosPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonServicosPesquisa, resposta);
				} else {
					transcreverServicoAuxiliar();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirServicoGETPorId() {
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
				ServicoRequisicao.getPorId(callbackServicoGETPorId(), id);
			}
		}
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
	}

	private void consumirServicoPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		ServicoDTO dto = ServicoMapeamento.paraDTO(mServicoAuxiliar);
		++mTarefasPendentes;
		ServicoRequisicao.post(callbackServicoPOST(), dto);
	}

	private void consumirServicoPUT() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		ServicoDTO dto = ServicoMapeamento.paraDTO(mServicoAuxiliar);
		++mTarefasPendentes;
		ServicoRequisicao.put(callbackServicoPUT(), mServico.getId(), dto);
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
		mServicoAuxiliar.setDescricao(mInputDescricao.getText().toString());
	}

	private void limparErros() {
		mLabelId.setError(null);
		mLabelId.setErrorEnabled(false);
		mLabelDescricao.setError(null);
		mLabelDescricao.setErrorEnabled(false);
	}

	private void limparFormulario() {
		setServico(Servico.builder().build());
		if (mOnReferenciaServicoAlteradoListener != null) {
			mOnReferenciaServicoAlteradoListener
					.onReferenciaServicoAlterado(mServico);
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
				if (mOnServicoAdicionadoListener != null) {
					mOnServicoAdicionadoListener
							.onServicoAdicionado(mButtonAdicionar, mServico);
				}
				getActivity().onBackPressed();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mServicoAuxiliar.getId() == null
				? "" : mServicoAuxiliar.getId().toString());
		mInputDescricao.setText(mServicoAuxiliar.getDescricao() == null
				? "" : mServicoAuxiliar.getDescricao());
		alternarButtonAdicionar();
	}

	public void setOnServicoAdicionadoListener(@Nullable OnServicoAdicionadoListener ouvinte) {
		mOnServicoAdicionadoListener = ouvinte;
	}

	public void setOnServicosPesquisaListener(@Nullable OnServicosPesquisaListener ouvinte) {
		mOnServicosPesquisaListener = ouvinte;
	}

	public void setOnReferenciaServicoAlteradoListener(@Nullable OnReferenciaServicoAlteradoListener ouvinte) {
		mOnReferenciaServicoAlteradoListener = ouvinte;
	}

	public void setServico(@NonNull Servico servico) {
		mServico = servico;
		if (getArguments() != null) {
			getArguments().putSerializable(SERVICO, mServico);
		}
		if (mServicoAuxiliar != null) {
			transcreverServico();
		}
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mServico.getId() != null) {
			consumirServicoPUT();
		} else {
			consumirServicoPOST();
		}
	}

	private void transcreverServico() {
		mServicoAuxiliar.setId(mServico.getId());
		mServicoAuxiliar.setDescricao(mServico.getDescricao());
	}

	private void transcreverServicoAuxiliar() {
		mServico.setDescricao(mServicoAuxiliar.getDescricao());
	}

	public interface OnServicoAdicionadoListener {

		void onServicoAdicionado(@NonNull View view, @NonNull Servico servico);
	}

	public interface OnServicosPesquisaListener {

		void onServicosPesquisa(@NonNull View view);
	}

	public interface OnReferenciaServicoAlteradoListener {

		void onReferenciaServicoAlterado(@NonNull Servico novaReferencia);
	}
}
