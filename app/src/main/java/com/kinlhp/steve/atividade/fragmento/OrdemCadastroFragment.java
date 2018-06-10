package com.kinlhp.steve.atividade.fragmento;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Servico;
import com.kinlhp.steve.dominio.Uf;
import com.kinlhp.steve.dto.ItemOrdemServicoDTO;
import com.kinlhp.steve.dto.OrdemDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.ItemOrdemServicoMapeamento;
import com.kinlhp.steve.mapeamento.OrdemMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.mapeamento.ServicoMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.ItemOrdemServicoRequisicao;
import com.kinlhp.steve.requisicao.OrdemRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class OrdemCadastroFragment extends Fragment
		implements AdapterView.OnItemSelectedListener,
		RadioGroup.OnCheckedChangeListener, Serializable,
		TextView.OnEditorActionListener, View.OnClickListener,
		View.OnFocusChangeListener {
	private static final long serialVersionUID = 2281886527875973324L;
	private static final int CODIGO_REQUISICAO_PERMISSAO = 200;
	private static final String ORDEM = "ordem";
	private static final String ORDEM_AUXILIAR = "ordemAuxiliar";
	private AdaptadorSpinner<Ordem.Situacao> mAdaptadorSituacoes;
	private OnClientesPesquisaListener mOnClientesPesquisaListener;
	private OnItensOrdemServicoPesquisaListener mOnItensOrdemServicoPesquisaListener;
	private OnOrdemAdicionadoListener mOnOrdemAdicionadoListener;
	private OnOrdensPesquisaListener mOnOrdensPesquisaListener;
	private OnReferenciaOrdemAlteradoListener mOnReferenciaOrdemAlteradoListener;
	private Ordem mOrdem;
	private Ordem mOrdemAuxiliar;
	private File mPDFFile;
	private boolean mPressionarVoltar;
	private ArrayList<Ordem.Situacao> mSituacoes;
	private int mTarefasPendentes;

	private AppCompatButton mButtonAdicionar;
	private AppCompatImageButton mButtonConsumirPorId;
	private FloatingActionButton mButtonOrdensPesquisa;
	private TextInputEditText mInputCliente;
	private TextInputEditText mInputId;
	private TextInputEditText mInputItensOrdemServico;
	private TextInputEditText mInputObservacao;
	private TextInputLayout mLabelCliente;
	private TextInputLayout mLabelId;
	private TextInputLayout mLabelItensOrdemServico;
	private ProgressBar mProgressBarAdicionar;
	private ProgressBar mProgressBarConsumirOrdens;
	private ProgressBar mProgressBarConsumirPorId;
	private RadioGroup mRadioGroupTipo;
	private ScrollView mScrollOrdemCadastro;
	private AppCompatSpinner mSpinnerSituacao;

	/**
	 * Construtor padrão é obrigatório
	 */
	public OrdemCadastroFragment() {
	}

	public static OrdemCadastroFragment newInstance(@NonNull Ordem ordem) {
		OrdemCadastroFragment fragmento = new OrdemCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(ORDEM, ordem);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int id) {
		switch (id) {
			case R.id.radio_button_orcamento:
				mOrdemAuxiliar.setTipo(Ordem.Tipo.ORCAMENTO);
				break;
			case R.id.radio_button_ordem_servico:
				mOrdemAuxiliar.setTipo(Ordem.Tipo.ORDEM_SERVICO);
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					submeterFormulario();
				} else {
					mScrollOrdemCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
			case R.id.button_consumir_por_id:
				if (mOrdem.getId() == null) {
					consumirOrdemGETPorId();
				} else {
					limparFormulario();
				}
				break;
			case R.id.button_ordens_pesquisa:
				if (mOnOrdensPesquisaListener != null) {
					mOnOrdensPesquisaListener.onOrdensPesquisa(view);
				}
				break;
			case R.id.input_cliente:
				if (mOnClientesPesquisaListener != null) {
					mOnClientesPesquisaListener.onClientesPesquisa(view);
				}
				break;
			case R.id.input_itens_ordem_servico:
				if (mOnItensOrdemServicoPesquisaListener != null) {
					mOnItensOrdemServicoPesquisaListener
							.onItensOrdemServicoPesquisa(view);
				}
				break;
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mOrdem = (Ordem) savedInstanceState.getSerializable(ORDEM);
			mOrdemAuxiliar = (Ordem) savedInstanceState
					.getSerializable(ORDEM_AUXILIAR);
		} else if (getArguments() != null) {
			mOrdem = (Ordem) getArguments().getSerializable(ORDEM);
			mOrdemAuxiliar = (Ordem) getArguments()
					.getSerializable(ORDEM_AUXILIAR);
		}
		if (mOrdemAuxiliar == null) {
			mOrdemAuxiliar = Ordem.builder().build();
			transcreverOrdem();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (mOrdem.getId() != null) {
			inflater.inflate(R.menu.print_menu, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_ordem_cadastro, container, false);
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mButtonConsumirPorId = view.findViewById(R.id.button_consumir_por_id);
		mButtonOrdensPesquisa = view.findViewById(R.id.button_ordens_pesquisa);
		mInputCliente = view.findViewById(R.id.input_cliente);
		mInputId = view.findViewById(R.id.input_id);
		mInputItensOrdemServico = view
				.findViewById(R.id.input_itens_ordem_servico);
		mInputObservacao = view.findViewById(R.id.input_observacao);
		mLabelCliente = view.findViewById(R.id.label_cliente);
		mLabelId = view.findViewById(R.id.label_id);
		mLabelItensOrdemServico = view
				.findViewById(R.id.label_itens_ordem_servico);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mProgressBarConsumirOrdens = view
				.findViewById(R.id.progress_bar_consumir_ordens);
		mProgressBarConsumirPorId = view
				.findViewById(R.id.progress_bar_consumir_por_id);
		mRadioGroupTipo = view.findViewById(R.id.radio_group_tipo);
		mScrollOrdemCadastro = view.findViewById(R.id.scroll_ordem_cadastro);
		mSpinnerSituacao = view.findViewById(R.id.spinner_situacao);

		mSituacoes = new ArrayList<>(Arrays.asList(Ordem.Situacao.values()));
		mAdaptadorSituacoes = new AdaptadorSpinner<>(getActivity(), mSituacoes);
		mSpinnerSituacao.setAdapter(mAdaptadorSituacoes);

		mButtonAdicionar.setOnClickListener(this);
		mButtonConsumirPorId.setOnClickListener(this);
		mButtonOrdensPesquisa.setOnClickListener(this);
		mInputCliente.setOnClickListener(this);
		mInputId.setOnEditorActionListener(this);
		mInputItensOrdemServico.setOnClickListener(this);
		mRadioGroupTipo.setOnCheckedChangeListener(this);
		mSpinnerSituacao.setOnItemSelectedListener(this);

		mScrollOrdemCadastro.fullScroll(View.FOCUS_UP);

		setHasOptionsMenu(true);
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
			case R.id.input_cliente:
				if (!focused) {
					isClienteValido();
				}
				break;
			case R.id.input_itens_ordem_servico:
				if (!focused) {
					isItensOrdemServicoValido();
				}
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	                           long id) {
		switch (parent.getId()) {
			case R.id.spinner_situacao:
				mOrdemAuxiliar
						.setSituacao((Ordem.Situacao) parent.getItemAtPosition(position));
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		/*
		 */
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_imprimir:
				exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
				new BarraProgresso().execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		iterarFormulario();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		switch (requestCode) {
			case CODIGO_REQUISICAO_PERMISSAO:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					getActivity().runOnUiThread(() -> {
						try {
							gerarPDF();
						} catch (Exception e) {
							ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
							String mensagem =
									getString(R.string.suporte_mensagem_pdf_escrever);
							Falha.tratar(mButtonOrdensPesquisa, new Exception(mensagem));
						}
					});
				} else {
					Toast.makeText(getActivity(), "Permissão negada!", Toast.LENGTH_SHORT)
							.show();
					if (mOnOrdemAdicionadoListener != null) {
						mOnOrdemAdicionadoListener
								.onOrdemAdicionado(mButtonAdicionar, mOrdem);
					}
					getActivity().onBackPressed();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.ordem_cadastro_titulo);
		mButtonOrdensPesquisa.setVisibility(View.VISIBLE);
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ORDEM, mOrdem);
		outState.putSerializable(ORDEM_AUXILIAR, mOrdemAuxiliar);
	}

	public void abrirDialogoImpressao() {
		AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
		alerta.setTitle("Impressão")
				.setMessage("Deseja imprimir a ordem de serviço?")
				.setCancelable(false)
				.setPositiveButton("Sim", (dialogInterface, i) -> {
					exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
					new BarraProgresso().execute();
				})
				.setNegativeButton("Não", (dialogInterface, i) -> {
					if (mOnOrdemAdicionadoListener != null) {
						mOnOrdemAdicionadoListener
								.onOrdemAdicionado(mButtonAdicionar, mOrdem);
					}
					getActivity().onBackPressed();
				});
		AlertDialog alertDialog = alerta.create();
		alertDialog.show();

	}

	private void alternarButtonAdicionar() {
		mButtonAdicionar.setHint(mOrdem.getId() == null
				? R.string.pessoa_cadastro_button_adicionar_hint
				: R.string.pessoa_cadastro_button_salvar_hint);
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mButtonAdicionar.setVisibility(mOrdem.getId() == null
				|| credencialLogado.isPerfilAdministrador()
				|| mOrdem.getSituacao().equals(Ordem.Situacao.ABERTO)
				|| mOrdem.getSituacao().equals(Ordem.Situacao.FINALIZADO)
				? View.VISIBLE : View.INVISIBLE);
		if (mOrdem.getSituacao().equals(Ordem.Situacao.GERADO) && !credencialLogado.isPerfilSistema()) {
			mSpinnerSituacao.setEnabled(false);
			mButtonAdicionar.setVisibility(View.GONE);
		}
	}

	private void alternarInputCliente() {
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mInputCliente.setEnabled(credencialLogado.isPerfilAdministrador()
				|| mOrdem.getId() == null);
	}

	private void alternarInputId() {
		mInputId.setEnabled(mOrdem.getId() == null);
		mButtonConsumirPorId.setImageResource(mOrdem.getId() == null
				? R.drawable.ic_consumir_por_id_accent_24dp
				: R.drawable.ic_borracha_accent_24dp);
		alternarMenuImpressao();
	}

	private void alternarMenuImpressao() {
		getActivity().invalidateOptionsMenu();
	}

	private void alternarRadioTipo() {
		switch (mOrdemAuxiliar.getTipo()) {
			case ORCAMENTO:
				mRadioGroupTipo.check(R.id.radio_button_orcamento);
				break;
			case ORDEM_SERVICO:
				mRadioGroupTipo.check(R.id.radio_button_ordem_servico);
				break;
		}
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		mRadioGroupTipo.setEnabled(credencialLogado.isPerfilAdministrador()
				|| mOrdem.getId() == null
				|| mOrdem.getTipo().equals(Ordem.Tipo.ORCAMENTO));
	}

	private void chamarOuvinteAlteracaoReferenciaOrdem() {
		if (mOnReferenciaOrdemAlteradoListener != null) {
			mOnReferenciaOrdemAlteradoListener
					.onReferenciaOrdemAlterado(mOrdem);
		}
	}

	private ItemCallback<ServicoDTO> callbackItemOrdemServicoGETServico(@NonNull ItemOrdemServico itemOrdemServico) {
		return new ItemCallback<ServicoDTO>() {

			@Override
			public void onFailure(@NonNull Call<ServicoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonOrdensPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<ServicoDTO> chamada,
			                       @NonNull Response<ServicoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonOrdensPesquisa, resposta);
				} else {
					ServicoDTO dto = resposta.body();
					Servico servico = ServicoMapeamento.paraDominio(dto);
					itemOrdemServico.setServico(servico);
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ItemCallback<PessoaDTO> callbackOrdemGETCliente() {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonOrdensPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonOrdensPesquisa, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa cliente = PessoaMapeamento.paraDominio(dto);
					mOrdem.setCliente(cliente);
					transcreverClienteOrdem();
					preencherViewCliente();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ColecaoCallback<Colecao<ItemOrdemServicoDTO>> callbackOrdemGETItensOrdemServico() {
		return new ColecaoCallback<Colecao<ItemOrdemServicoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ItemOrdemServicoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonOrdensPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ItemOrdemServicoDTO>> chamada,
			                       @NonNull Response<Colecao<ItemOrdemServicoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonOrdensPesquisa, resposta);
				} else {
					Set<ItemOrdemServicoDTO> dtos = resposta.body()
							.getEmbedded().getDtos();
					Set<ItemOrdemServico> itensOrdemServico =
							ItemOrdemServicoMapeamento
									.paraDominios(dtos, mOrdem);
					mOrdem.getItens().addAll(itensOrdemServico);
					preencherViewItensOrdemServico();
					for (ItemOrdemServico itemOrdemServico : itensOrdemServico) {
						consumirItemOrdemServicoGETServico(itemOrdemServico);
					}
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private ItemCallback<OrdemDTO> callbackOrdemGETPorId() {

		return new ItemCallback<OrdemDTO>() {
			@Override
			public void onFailure(@NonNull Call<OrdemDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
				Falha.tratar(mButtonOrdensPesquisa, causa);
				mInputId.getText().clear();
			}

			@Override
			public void onResponse(@NonNull Call<OrdemDTO> chamada,
			                       @NonNull Response<OrdemDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonOrdensPesquisa, resposta);
					mInputId.getText().clear();
				} else {
					OrdemDTO dto = resposta.body();
					Ordem ordem = OrdemMapeamento.paraDominio(dto);
					setOrdem(ordem);
					chamarOuvinteAlteracaoReferenciaOrdem();
					preencherFormulario();
					mInputObservacao.requestFocus();
					mScrollOrdemCadastro.fullScroll(View.FOCUS_UP);

					consumirOrdemGETCliente();
					consumirOrdemGETItensOrdemServico();
				}
				ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
			}
		};
	}

	private VazioCallback callbackOrdemPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonOrdensPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonOrdensPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					mOrdem.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
					transcreverOrdemAuxiliar();
					consumirOrdemPOSTItensOrdemServico();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackOrdemPOSTItemOrdemServico(ItemOrdemServico itemOrdemServico) {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonOrdensPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonOrdensPesquisa, resposta);
				} else {
					String location = resposta.headers()
							.get(Requisicao.LOCATION_HEADER);
					itemOrdemServico
							.setId(new BigInteger(location.substring(location.lastIndexOf("/") + 1)));
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private VazioCallback callbackOrdemPUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				mPressionarVoltar = false;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonOrdensPesquisa, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					mPressionarVoltar = false;
					Falha.tratar(mButtonOrdensPesquisa, resposta);
				} else {
					transcreverOrdemAuxiliar();
					consumirOrdemPOSTItensOrdemServico();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirItemOrdemServicoGETServico(@NonNull ItemOrdemServico itemOrdemServico) {
		// TODO: 9/21/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("itensOrdemServico/%d/servico");
		HRef href = new HRef(String.format(url, itemOrdemServico.getId()));
		++mTarefasPendentes;
		ItemOrdemServicoRequisicao
				.getServico(callbackItemOrdemServicoGETServico(itemOrdemServico), href);
	}

	private void consumirOrdemGETCliente() {
		// TODO: 9/21/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("ordens/%d/cliente");
		HRef href = new HRef(String.format(url, mOrdem.getId()));
		++mTarefasPendentes;
		OrdemRequisicao.getCliente(callbackOrdemGETCliente(), href);
	}

	private void consumirOrdemGETItensOrdemServico() {
		// TODO: 9/21/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("ordens/%d/itens");
		HRef href = new HRef(String.format(url, mOrdem.getId()));
		++mTarefasPendentes;
		OrdemRequisicao.getItens(callbackOrdemGETItensOrdemServico(), href);
	}

	private void consumirOrdemGETPorId() {
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
				OrdemRequisicao.getPorId(callbackOrdemGETPorId(), id);
			}
		}
		ocultarProgresso(mProgressBarConsumirPorId, mButtonConsumirPorId);
	}

	private void consumirOrdemPOST() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		OrdemDTO dto = OrdemMapeamento.paraDTO(mOrdemAuxiliar);
		++mTarefasPendentes;
		OrdemRequisicao.post(callbackOrdemPOST(), dto);
	}

	private void consumirOrdemPOSTItensOrdemServico() {
		for (ItemOrdemServico itemOrdemServico : mOrdem.getItens()) {
			if (itemOrdemServico.getId() == null) {
				ItemOrdemServicoDTO dto = ItemOrdemServicoMapeamento
						.paraDTO(itemOrdemServico);
				++mTarefasPendentes;
				ItemOrdemServicoRequisicao
						.post(callbackOrdemPOSTItemOrdemServico(itemOrdemServico), dto);
			}
		}
	}

	private void consumirOrdemPUT() {
		mTarefasPendentes = 0;
		mPressionarVoltar = true;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		OrdemDTO dto = OrdemMapeamento.paraDTO(mOrdemAuxiliar);
		++mTarefasPendentes;
		OrdemRequisicao.put(callbackOrdemPUT(), mOrdem.getId(), dto);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private String gerarHTML() {
		StringBuilder sb = new StringBuilder();
		BigDecimal valorTotalOrcao = BigDecimal.ZERO;
		BigDecimal valorTotalAplicado = BigDecimal.ZERO;

		// TODO: 9/26/17 obter endereço pelo cliente da ordem
		Endereco endereco = Endereco.builder().logradouro("Rua Regente Feijo")
				.numero("336")
				.bairro("Zona 6")
				.cep("87205-012")
				.cidade("Cianorte")
				.uf(Uf.builder().sigla(Uf.Sigla.PR).build()).build();

		sb.append("<table style=\"width:100%;border:1px solid #000;border-collapse:collapse border=1 cellpadding=6\">\n")
				.append("    <tbody>\n")
				.append("        <tr>\n")
				.append("            <td colspan=\"4\" style=\"text-align:center;font-size:1.2em\"><strong>Ordem de serviço: ").append(mOrdem.getId()).append(" </strong></td>\n")
				.append("        </tr>\n")
//				.append("        <tr>\n")
//				.append("            <td colspan=\"4\" style=\"text-align:center\"><strong>" + getString(R.string.app_name) + "</strong></td>\n")
//				.append("        </tr>\n")
				.append("        <tr>\n")
				.append("            <td colspan=\"4\" style=\"text-align:center\"><strong>ConTorno Tornearia Mecânica</strong></td>\n")
				.append("        </tr>\n")
				.append("        <tr>\n")
				.append("            <td colspan=\"4\" style=\"text-align:center\"><strong>CNPJ 95.142.024/0001-91</strong></td>\n")
				.append("        </tr>\n")
				.append("        <tr>\n")
				.append("            <td colspan=\"4\" style=\"text-align:center\"><strong>Rua Pompéia, 627</strong></td>\n")
				.append("        </tr>\n")
				.append("        <tr>\n")
				.append("            <td colspan=\"4\" style=\"text-align:center\"><strong>(044) 3018-8103</strong></td>\n")
				.append("        </tr>\n")
				.append("    </tbody>\n")
				.append("</table>\n")
				.append("<table style=\"width:100%;border-collapse:collapse border=0 cellpadding=6\">\n")
				.append("    <tbody>\n")
				.append("        <tr colspan=\"6\" style=\"font-size:1.2em\">\n")
				.append("            <td>Dados do cliente</td>\n")
				.append("        </tr>\n")
				.append("        <tr>\n")
				.append("            <td>Nome: " + mOrdem.getCliente().toString() + "</td>\n")
				.append("            <td>CNPJ/CPF: " + mOrdem.getCliente().getCnpjCpf() + "</td>\n")
				.append("        </tr>\n")
//				.append("        <tr>\n")
//				.append("            <td>Endereço: " + endereco.getLogradouro() + "</td>\n")
//				.append("            <td>Bairro: " + endereco.getBairro() + "</td>\n")
//				.append("        </tr>\n")
//				.append("        <tr>\n")
//				.append("            <td>Cidade: " + endereco.getCidade() + "</td>\n")
//				.append("            <td>UF: " + endereco.getUf().getSigla().name() + "</td>\n")
//				.append("        </tr>\n")
//				.append("        <tr>\n")
//				.append("            <td>CEP: " + endereco.getCep() + "</td>\n")
//				.append("        </tr>\n")
//				.append("        <tr>\n")
//				.append("            <td>Telefone: 44 3322-8855</td>\n")
//				.append("        </tr>\n")
				.append("    </tbody>\n")
				.append("</table>\n")
				.append("<br></br>\n")
				.append("<table style=\"width:100%;border:1px solid #cccccc;border-collapse:collapse;border:none\">\n")
				.append("    <thead>\n")
				.append("        <tr>\n")
				.append("            <th style=\"width:60%;border:1px solid #cccccc\">Serviço</th>\n")
				.append("            <th style=\"width:12%;border:1px solid #cccccc\">Situação</th>")
				.append("            <th style=\"width:14%;border:1px solid #cccccc\">Valor orçado</th>\n")
				.append("            <th style=\"width:14%;border:1px solid #cccccc\">Valor aplicado</th>\n")
				.append("        </tr>\n")
				.append("    </thead>\n")
				.append("    <tbody>\n");
		for (ItemOrdemServico itemOrdemServico : mOrdem.getItens()) {
			sb.append("        <tr>\n")
					.append("            <td style=\"border:1px solid #cccccc\">" + itemOrdemServico.getServico().toString() + "</td>\n")
					.append("            <td style=\"border:1px solid #cccccc\">" + itemOrdemServico.getSituacao() + "</td>\n")
					.append("            <td style=\"border:1px solid #cccccc;text-align:right\">" + itemOrdemServico.getValorOrcamento().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString().replace(".", ",") + "</td>\n")
					.append("            <td style=\"border:1px solid #cccccc;text-align:right\">" + itemOrdemServico.getValorServico().setScale(2, BigDecimal.ROUND_HALF_EVEN).toString().replace(".", ",") + "</td>\n")
					.append("        </tr>\n");
			valorTotalOrcao = valorTotalOrcao.add(itemOrdemServico.getValorOrcamento());
			valorTotalAplicado = valorTotalAplicado.add(itemOrdemServico.getValorServico());
		}
		sb.append("    </tbody>\n")
				.append("    <tfoot>\n")
				.append("        <tr>\n")
				.append("            <th colspan=\"3\" style=\"text-align:right\">Total orçado :</th>\n")
				.append("            <td style=\"text-align:right\">" + valorTotalOrcao.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString().replace(".", ",") + "</td>\n")
				.append("        </tr>\n")
				.append("        <tr>\n")
				.append("            <th colspan=\"3\" style=\"text-align:right\">Total aplicado :</th>\n")
				.append("            <td style=\"text-align:right\">" + valorTotalAplicado.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString().replace(".", ",") + "</td>\n")
				.append("        </tr>\n")
				.append("    </tfoot>\n")
				.append("</table>");
		return sb.toString();
	}

	private void gerarPDF() throws IOException, DocumentException {

		File docsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
		if (!docsFolder.exists()) {
			docsFolder.mkdir();
		}

		mPDFFile =
				new File(docsFolder.getAbsolutePath(), mOrdem.getId() + ".pdf");
		OutputStream output = new FileOutputStream(mPDFFile);
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, output);
		document.open();

		InputStream is = new ByteArrayInputStream(gerarHTML().getBytes());
		XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);

		document.close();
		previewPdf();

	}

	private boolean isClienteValido() {
		if (mOrdem.getCliente() == null
				|| mOrdem.getCliente().getId() == null) {
			mLabelCliente.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!mOrdem.getCliente().isPerfilCliente()) {
			mLabelCliente
					.setError(getString(R.string.ordem_cadastro_mensagem_cliente_nao_cliente));
			return false;
		}
		mLabelCliente.setError(null);
		mLabelCliente.setErrorEnabled(false);
		return true;
	}

	private boolean isFormularioValido() {
		return isClienteValido()
				&& isItensOrdemServicoValido();
	}

	private boolean isItensOrdemServicoValido() {
		if (mOrdemAuxiliar.getItens() == null
				|| mOrdemAuxiliar.getItens().isEmpty()) {
			mLabelItensOrdemServico
					.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		mLabelItensOrdemServico.setError(null);
		mLabelItensOrdemServico.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		transcreverClienteOrdem();
		mOrdemAuxiliar.setObservacao(mInputObservacao.getText().toString());
	}

	private void limitarSituacoes() {
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		if (!credencialLogado.isPerfilAdministrador()
				&& mOrdem.getId() != null) {
			switch (mOrdem.getSituacao()) {
				case CANCELADO:
					mAdaptadorSituacoes.clear();
					mAdaptadorSituacoes.add(Ordem.Situacao.CANCELADO);
					break;
				case FINALIZADO:
					mAdaptadorSituacoes.clear();
					mAdaptadorSituacoes.add(Ordem.Situacao.FINALIZADO);
					break;
				case GERADO:
					mAdaptadorSituacoes.clear();
					mAdaptadorSituacoes.add(Ordem.Situacao.GERADO);
					break;
			}
			mAdaptadorSituacoes.notifyDataSetChanged();
		}
	}

	private void limparErros() {
		mLabelId.setError(null);
		mLabelId.setErrorEnabled(false);
		mLabelCliente.setError(null);
		mLabelCliente.setErrorEnabled(false);
		mLabelItensOrdemServico.setError(null);
		mLabelItensOrdemServico.setErrorEnabled(false);
	}

	private void limparFormulario() {
		setOrdem(Ordem.builder().build());
		if (mOnReferenciaOrdemAlteradoListener != null) {
			mOnReferenciaOrdemAlteradoListener
					.onReferenciaOrdemAlterado(mOrdem);
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
				abrirDialogoImpressao();
			}
		}
	}

	private void preencherFormulario() {
		limparErros();
		alternarInputId();
		mInputId.setText(mOrdemAuxiliar.getId() == null
				? "" : mOrdemAuxiliar.getId().toString());
		preencherViewCliente();
		alternarInputCliente();
		alternarRadioTipo();
		limitarSituacoes();
		mSpinnerSituacao.setSelection(mOrdemAuxiliar.getTipo() == null
				? 0
				: mAdaptadorSituacoes.getPosition(mOrdemAuxiliar.getSituacao()));
		preencherViewItensOrdemServico();
		mInputObservacao.setText(mOrdemAuxiliar.getObservacao() == null
				? "" : mOrdemAuxiliar.getObservacao());
		alternarButtonAdicionar();
	}

	private void preencherViewCliente() {
		mLabelCliente.setHint(mOrdem.getCliente() == null
				? getString(R.string.ordem_cadastro_label_nenhum_cliente_hint)
				: getString(R.string.ordem_cadastro_label_cliente_hint));
		mInputCliente.setText(mOrdem.getCliente() == null
				? "" : mOrdem.getCliente().toString());
	}

	private void preencherViewItensOrdemServico() {
		mLabelItensOrdemServico.setHint(mOrdemAuxiliar.getItens().isEmpty()
				? getString(R.string.ordem_cadastro_label_nenhum_item_ordem_servico_hint)
				: getString(R.string.ordem_cadastro_label_itens_ordem_servico_hint));
		mInputItensOrdemServico.setText(mOrdemAuxiliar.getItens().isEmpty()
				? ""
				: getString(R.string.ordem_cadastro_input_itens_ordem_servico_text));
	}

	private void previewPdf() {
		Intent intentPDF = new Intent(Intent.ACTION_VIEW);
		String caminhoArquivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath().concat(File.separator).concat(String.valueOf(mOrdem.getId())).concat(".pdf");
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			intentPDF.setDataAndType(Uri.fromFile(new File(caminhoArquivo)), "application/pdf");
		} else {
			Uri uri = Uri.parse(caminhoArquivo);
			File arquivo = new File(uri.getPath());
			if (arquivo.exists()) {
				uri = FileProvider.getUriForFile(getActivity(), getString(R.string.file_provider_authority), arquivo);
				intentPDF.setDataAndType(uri, "application/pdf");
				intentPDF.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}
			intentPDF.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		try {
			getActivity().startActivity(intentPDF);
		} catch (Exception e) {
			e.printStackTrace();
			ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			Toast.makeText(getActivity(), R.string.suporte_mensagem_pdf_ler, Toast.LENGTH_SHORT).show();
		}
		getActivity().finish();
	}

	public void setOnClientesPesquisaListener(@Nullable OnClientesPesquisaListener ouvinte) {
		mOnClientesPesquisaListener = ouvinte;
	}

	public void setOnItensOrdemServicoPesquisaListener(@Nullable OnItensOrdemServicoPesquisaListener ouvinte) {
		mOnItensOrdemServicoPesquisaListener = ouvinte;
	}

	public void setOnOrdemAdicionadoListener(@Nullable OnOrdemAdicionadoListener ouvinte) {
		mOnOrdemAdicionadoListener = ouvinte;
	}

	public void setOnOrdensPesquisaListener(@Nullable OnOrdensPesquisaListener ouvinte) {
		mOnOrdensPesquisaListener = ouvinte;
	}

	public void setOnReferenciaOrdemAlteradoListener(@Nullable OnReferenciaOrdemAlteradoListener ouvinte) {
		mOnReferenciaOrdemAlteradoListener = ouvinte;
	}

	public void setOrdem(@NonNull Ordem ordem) {
		mOrdem = ordem;
		if (getArguments() != null) {
			getArguments().putSerializable(ORDEM, mOrdem);
			getArguments().putSerializable(ORDEM_AUXILIAR, mOrdemAuxiliar);
		}
		if (mOrdemAuxiliar != null) {
			transcreverOrdem();
		}
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mOrdem.getId() != null) {
			consumirOrdemPUT();
		} else {
			consumirOrdemPOST();
		}
	}

	private void transcreverClienteOrdem() {
		mOrdemAuxiliar.setCliente(mOrdem.getCliente());
	}

	private void transcreverOrdem() {
		transcreverClienteOrdem();
		mOrdemAuxiliar.setId(mOrdem.getId());
		mOrdemAuxiliar.setItens(mOrdem.getItens());
		mOrdemAuxiliar.setObservacao(mOrdem.getObservacao());
		mOrdemAuxiliar.setSituacao(mOrdem.getSituacao());
		mOrdemAuxiliar.setTipo(mOrdem.getTipo());
	}

	private void transcreverOrdemAuxiliar() {
		mOrdem.setObservacao(mOrdemAuxiliar.getObservacao());
		mOrdem.setSituacao(mOrdemAuxiliar.getSituacao());
		mOrdem.setTipo(mOrdemAuxiliar.getTipo());
	}

	private void verificarPermissão() {
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODIGO_REQUISICAO_PERMISSAO);
		} else {
			getActivity().runOnUiThread(() -> {
				try {
					gerarPDF();
				} catch (Exception e) {
					ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
					String mensagem =
							getString(R.string.suporte_mensagem_pdf_escrever);
					Falha.tratar(mButtonOrdensPesquisa, new Exception(mensagem));
				}
			});
		}
	}

	public interface OnClientesPesquisaListener {

		void onClientesPesquisa(@NonNull View view);
	}

	public interface OnItensOrdemServicoPesquisaListener {

		void onItensOrdemServicoPesquisa(@NonNull View view);
	}

	public interface OnOrdemAdicionadoListener {

		void onOrdemAdicionado(@NonNull View view, @NonNull Ordem ordem);
	}

	public interface OnOrdensPesquisaListener {

		void onOrdensPesquisa(@NonNull View view);
	}

	public interface OnReferenciaOrdemAlteradoListener {

		void onReferenciaOrdemAlterado(@NonNull Ordem novaReferencia);
	}

	private class BarraProgresso extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			verificarPermissão();
			return null;
		}
	}
}