package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerContasReceber;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.dominio.MovimentacaoContaReceber;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.ContaReceberDTO;
import com.kinlhp.steve.dto.MovimentacaoContaReceberDTO;
import com.kinlhp.steve.dto.OrdemDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CondicaoPagamentoMapeamento;
import com.kinlhp.steve.mapeamento.ContaReceberMapeamento;
import com.kinlhp.steve.mapeamento.MovimentacaoContaReceberMapeamento;
import com.kinlhp.steve.mapeamento.OrdemMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.ContaReceberRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.MovimentacaoContaReceberRequisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.Links;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class ContasReceberPesquisaFragment extends Fragment
		implements AdaptadorRecyclerContasReceber.OnItemClickListener,
		AdaptadorRecyclerContasReceber.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final long serialVersionUID = 1208743848156612458L;
	private static final String LINKS = "_links";
	private static final String PAGINA_0 = "contasReceber?sort=sacado.nomeRazao,asc&sort=dataVencimento,asc&page=0&size=20";
	private static final String CONTAS_RECEBER = "contasReceber";
	private AdaptadorRecyclerContasReceber mAdaptadorContasReceber;
	private ArrayList<ContaReceber> mContasReceber = new ArrayList<>();
	private ContaReceber mContaReceberSelecionada;
	private Links mLinks;
	private OnLongoContaReceberSelecionadoListener mOnLongoContaReceberSelecionadoListener;
	private OnContaReceberSelecionadoListener mOnContaReceberSelecionadoListener;
	private boolean mOuvinteJaChamado;
	private int mTarefasPendentes;
	private View mViewSelecionada;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirContasReceberPaginado;
	private RecyclerView mRecyclerContasReceber;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ContasReceberPesquisaFragment() {
	}

	public static ContasReceberPesquisaFragment newInstance() {
		ContasReceberPesquisaFragment fragmento = new ContasReceberPesquisaFragment();
		Bundle argumentos = new Bundle();
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(this);
		searchView.setQueryHint(getString(R.string.app_search));
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_contas_receber_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirContasReceberPaginado = view
				.findViewById(R.id.progress_bar_consumir_contas_receber_paginado);
		mRecyclerContasReceber = view.findViewById(R.id.recycler_contas_receber);

		mRecyclerContasReceber.setHasFixedSize(true);
		mAdaptadorContasReceber = new AdaptadorRecyclerContasReceber(mContasReceber);
		mAdaptadorContasReceber.setOnItemClickListener(this);
		mAdaptadorContasReceber.setOnItemLongClickListener(this);
		mRecyclerContasReceber.setAdapter(mAdaptadorContasReceber);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerContasReceber.setLayoutManager(gerenciador);

		mRecyclerContasReceber.addOnScrollListener(new OnContaReceberScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mContaReceberSelecionada = mContasReceber.get(posicao);
		if (ContaReceber.Situacao.ABERTO.equals(mContaReceberSelecionada.getSituacao())
				|| ContaReceber.Situacao.AMORTIZADO.equals(mContaReceberSelecionada.getSituacao())) {
			mViewSelecionada = view;
			if (mOnContaReceberSelecionadoListener != null) {
				mOnContaReceberSelecionadoListener
						.onContaReceberSelecionado(mViewSelecionada, mContaReceberSelecionada);
				getActivity().onBackPressed();
			}
		}
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		mContaReceberSelecionada = mContasReceber.get(posicao);
		if (ContaReceber.Situacao.ABERTO.equals(mContaReceberSelecionada.getSituacao())
				|| ContaReceber.Situacao.AMORTIZADO.equals(mContaReceberSelecionada.getSituacao())) {
			mViewSelecionada = view;
			if (mOnLongoContaReceberSelecionadoListener != null) {
				mOnLongoContaReceberSelecionadoListener
						.onLongoContaReceberSelecionado(mViewSelecionada, mContaReceberSelecionada);
				getActivity().onBackPressed();
			}
		}
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem menuItem) {
		return true;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem menuItem) {
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		StringBuilder url =
				new StringBuilder(getString(R.string.requisicao_url_base))
						.append("contasReceber/")
						.append("search/")
						.append("cnpjCpf-nomeRazao-fantasiaSobrenome")
						.append("?cnpjCpf=").append(query)
						.append("&nomeRazao=").append(query)
						.append("&fantasiaSobrenome=").append(query)
						.append("&sort=sacado.nomeRazao,asc&sort=dataVencimento,asc")
						.append("&page=0&size=20");
		HRef pagina0 = new HRef(url.toString());
		consumirContasReceberGETPesquisaPaginado(pagina0);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.contas_receber_pesquisa_titulo);
		if (mContasReceber.isEmpty()) {
			String url = getString(R.string.requisicao_url_base) + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirContasReceberGETPaginado(pagina0);
		}
		alternarLabel0Registros();
		mOuvinteJaChamado = false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(CONTAS_RECEBER, mContasReceber);
	}

	private void addContaReceber(@NonNull ContaReceber contaReceber) {
		if (contaReceber.getId().compareTo(BigInteger.ZERO) > 0
				&& !ContaReceber.Situacao.BAIXADO.equals(contaReceber.getSituacao())
				&& !ContaReceber.Situacao.CANCELADO.equals(contaReceber.getSituacao())) {
			if (mContasReceber.indexOf(contaReceber) < 0) {
				mContasReceber.add(contaReceber);
			}
			mAdaptadorContasReceber.notifyDataSetChanged();
		}
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mContasReceber.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ItemCallback<CondicaoPagamentoDTO> callbackMovimentacaoContaReceberGETCondicaoPagamento(@NonNull MovimentacaoContaReceber movimentacaoContaReceber) {
		return new ItemCallback<CondicaoPagamentoDTO>() {

			@Override
			public void onFailure(@NonNull Call<CondicaoPagamentoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				Falha.tratar(mProgressBarConsumirContasReceberPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<CondicaoPagamentoDTO> chamada,
			                       @NonNull Response<CondicaoPagamentoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
					Falha.tratar(mProgressBarConsumirContasReceberPaginado, resposta);
				} else {
					CondicaoPagamentoDTO dto = resposta.body();
					CondicaoPagamento condicaoPagamento = CondicaoPagamentoMapeamento.paraDominio(dto);
					movimentacaoContaReceber.setCondicaoPagamento(condicaoPagamento);
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				}
			}
		};
	}

	private ItemCallback<PessoaDTO> callbackContaReceberGETSacado(@NonNull ContaReceber contaReceber) {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				Falha.tratar(mProgressBarConsumirContasReceberPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
					Falha.tratar(mProgressBarConsumirContasReceberPaginado, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa cliente = PessoaMapeamento.paraDominio(dto);
					contaReceber.setSacado(cliente);
					addContaReceber(contaReceber);
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				}
			}
		};
	}

	private ItemCallback<OrdemDTO> callbackContaReceberGETOrdem(@NonNull ContaReceber contaReceber) {
		return new ItemCallback<OrdemDTO>() {

			@Override
			public void onFailure(@NonNull Call<OrdemDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				Falha.tratar(mProgressBarConsumirContasReceberPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<OrdemDTO> chamada,
			                       @NonNull Response<OrdemDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
					Falha.tratar(mProgressBarConsumirContasReceberPaginado, resposta);
				} else {
					OrdemDTO dto = resposta.body();
					Ordem ordem = OrdemMapeamento.paraDominio(dto);
					contaReceber.setOrdem(ordem);
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<MovimentacaoContaReceberDTO>> callbackContaReceberGETMovimentacoesContaReceber(@NonNull ContaReceber contaReceber) {
		return new ColecaoCallback<Colecao<MovimentacaoContaReceberDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<MovimentacaoContaReceberDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				Falha.tratar(mProgressBarConsumirContasReceberPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<MovimentacaoContaReceberDTO>> chamada,
			                       @NonNull Response<Colecao<MovimentacaoContaReceberDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
					Falha.tratar(mProgressBarConsumirContasReceberPaginado, resposta);
				} else {
					Set<MovimentacaoContaReceberDTO> dtos = resposta.body()
							.getEmbedded().getDtos();
					Set<MovimentacaoContaReceber> movimentacoesContaReceber =
							MovimentacaoContaReceberMapeamento
									.paraDominios(dtos, mContaReceberSelecionada);
//					mContaReceberSelecionada.getMovimentacoes().addAll(movimentacoesContaReceber);
					contaReceber.setMovimentacoes(movimentacoesContaReceber);
					addContaReceber(contaReceber);
					for (MovimentacaoContaReceber movimentacaoContaReceber : movimentacoesContaReceber) {
						consumirMovimentacaoContaReceberGETCondicaoPagamento(movimentacaoContaReceber);
					}
					ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<ContaReceberDTO>> callbackContasReceberGETPaginado() {
		return new ColecaoCallback<Colecao<ContaReceberDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ContaReceberDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				Falha.tratar(mProgressBarConsumirContasReceberPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ContaReceberDTO>> chamada,
			                       @NonNull Response<Colecao<ContaReceberDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirContasReceberPaginado, resposta);
				} else {
					Colecao<ContaReceberDTO> colecao = resposta.body();
					Set<ContaReceberDTO> dtos = colecao.getEmbedded().getDtos();
					Set<ContaReceber> contasReceber = ContaReceberMapeamento.paraDominios(dtos);
					for (ContaReceber contaReceber : contasReceber) {
						addContaReceber(contaReceber);
						consumirContaReceberGETOrdem(contaReceber);
						consumirContaReceberGETSacado(contaReceber);
						consumirContaReceberGETMovimentacoesContaReceber(contaReceber);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
			}
		};
	}

	private ColecaoCallback<Colecao<ContaReceberDTO>> callbackContasReceberGETPesquisaPaginado() {
		return new ColecaoCallback<Colecao<ContaReceberDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ContaReceberDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
				Falha.tratar(mProgressBarConsumirContasReceberPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ContaReceberDTO>> chamada,
			                       @NonNull Response<Colecao<ContaReceberDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirContasReceberPaginado, resposta);
				} else {
					Colecao<ContaReceberDTO> colecao = resposta.body();
					Set<ContaReceberDTO> dtos = colecao.getEmbedded().getDtos();
					Set<ContaReceber> contasReceber = ContaReceberMapeamento.paraDominios(dtos);
					for (ContaReceber contaReceber : contasReceber) {
						addContaReceber(contaReceber);
						consumirContaReceberGETSacado(contaReceber);
						consumirContaReceberGETMovimentacoesContaReceber(contaReceber);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirContasReceberPaginado);
			}
		};
	}

	private void consumirMovimentacaoContaReceberGETCondicaoPagamento(@NonNull MovimentacaoContaReceber movimentacaoContaReceber) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("movimentacoesContaReceber/%d/condicaoPagamento");
		HRef href = new HRef(String.format(url, movimentacaoContaReceber.getId()));
		++mTarefasPendentes;
		MovimentacaoContaReceberRequisicao
				.getCondicaoPagamento(callbackMovimentacaoContaReceberGETCondicaoPagamento(movimentacaoContaReceber), href);
	}

	private void consumirContaReceberGETSacado(@NonNull ContaReceber contaReceber) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("contasReceber/%d/sacado");
		HRef href = new HRef(String.format(url, contaReceber.getId()));
		++mTarefasPendentes;
		ContaReceberRequisicao.getSacado(callbackContaReceberGETSacado(contaReceber), href);
	}

	private void consumirContaReceberGETOrdem(@NonNull ContaReceber contaReceber) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("contasReceber/%d/ordem");
		HRef href = new HRef(String.format(url, contaReceber.getId()));
		++mTarefasPendentes;
		ContaReceberRequisicao.getOrdem(callbackContaReceberGETOrdem(contaReceber), href);
	}

	private void consumirContaReceberGETMovimentacoesContaReceber(@NonNull ContaReceber contaReceber) {
		exibirProgresso(mProgressBarConsumirContasReceberPaginado);
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("contasReceber/%d/movimentacoes");
		HRef href = new HRef(String.format(url, contaReceber.getId()));
		++mTarefasPendentes;
		ContaReceberRequisicao.getMovimentacoes(callbackContaReceberGETMovimentacoesContaReceber(contaReceber), href);
	}

	private void consumirContasReceberGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirContasReceberPaginado);
		exibirProgresso(mProgressBarConsumirContasReceberPaginado);
		mAdaptadorContasReceber.notifyItemRangeRemoved(0, mContasReceber.size());
		++mTarefasPendentes;
		ContaReceberRequisicao.getPaginado(callbackContasReceberGETPaginado(), href);
	}

	private void consumirContasReceberGETPesquisaPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirContasReceberPaginado);
		exibirProgresso(mProgressBarConsumirContasReceberPaginado);
		mAdaptadorContasReceber.notifyItemRangeRemoved(0, mContasReceber.size());
		mContasReceber.clear();
		mLinks = null;
		++mTarefasPendentes;
		ContaReceberRequisicao.getPaginado(callbackContasReceberGETPesquisaPaginado(), href);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso) {
		progresso.setVisibility(View.VISIBLE);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso) {
		if (mTarefasPendentes <= 0) {
			alternarLabel0Registros();
			progresso.setVisibility(View.GONE);
		}
	}

	public void setOnLongoContaReceberSelecionadoListener(@Nullable OnLongoContaReceberSelecionadoListener ouvinte) {
		mOnLongoContaReceberSelecionadoListener = ouvinte;
	}

	public void setOnContaReceberSelecionadoListener(@Nullable OnContaReceberSelecionadoListener ouvinte) {
		mOnContaReceberSelecionadoListener = ouvinte;
	}

	public interface OnLongoContaReceberSelecionadoListener {

		void onLongoContaReceberSelecionado(@NonNull View view, @NonNull ContaReceber contaReceber);
	}

	public interface OnContaReceberSelecionadoListener {

		void onContaReceberSelecionado(@NonNull View view, @NonNull ContaReceber contaReceber);
	}

	private final class OnContaReceberScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = 3588439901319351358L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerContasReceber.getLayoutManager();
			if (mContasReceber.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirContasReceberGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
