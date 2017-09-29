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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerFormasPagamento;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.FormaPagamentoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CondicaoPagamentoMapeamento;
import com.kinlhp.steve.mapeamento.FormaPagamentoMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.FormaPagamentoRequisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.Links;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class FormasPagamentoPesquisaFragment extends Fragment
		implements AdaptadorRecyclerFormasPagamento.OnItemClickListener,
		AdaptadorRecyclerFormasPagamento.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final long serialVersionUID = 408735893653785118L;
	private static final String FORMAS_PAGAMENTO = "formasPagamento";
	private static final String LINKS = "_links";
	private static final String PAGINA_0 =
			"formaspagamento?sort=descricao,asc&page=0&size=20";
	private AdaptadorRecyclerFormasPagamento mAdaptadorFormasPagamento;
	private FormaPagamento mFormaPagamentoSelecionada;
	private ArrayList<FormaPagamento> mFormasPagamento = new ArrayList<>();
	private Links mLinks;
	private OnFormaPagamentoSelecionadoListener mOnFormaPagamentoSelecionadoListener;
	private OnLongoFormaPagamentoSelecionadoListener mOnLongoFormaPagamentoSelecionadoListener;
	private int mTarefasPendentes;
	private View mViewSelecionada;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirFormasPagamentoPaginado;
	private RecyclerView mRecyclerFormasPagamento;

	/**
	 * Construtor padrão é obrigatório
	 */
	public FormasPagamentoPesquisaFragment() {
	}

	public static FormasPagamentoPesquisaFragment newInstance() {
		FormasPagamentoPesquisaFragment fragmento =
				new FormasPagamentoPesquisaFragment();
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
				.inflate(R.layout.fragment_formas_pagamento_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirFormasPagamentoPaginado = view
				.findViewById(R.id.progress_bar_consumir_formas_pagamento_paginado);
		mRecyclerFormasPagamento = view
				.findViewById(R.id.recycler_formas_pagamento);

		mRecyclerFormasPagamento.setHasFixedSize(true);
		mAdaptadorFormasPagamento =
				new AdaptadorRecyclerFormasPagamento(mFormasPagamento);
		mAdaptadorFormasPagamento.setOnItemClickListener(this);
		mAdaptadorFormasPagamento.setOnItemLongClickListener(this);
		mRecyclerFormasPagamento.setAdapter(mAdaptadorFormasPagamento);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerFormasPagamento.setLayoutManager(gerenciador);

		mRecyclerFormasPagamento
				.addOnScrollListener(new OnFormaPagamentoScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mFormaPagamentoSelecionada = mFormasPagamento.get(posicao);
		mViewSelecionada = view;
		consumirFormaPagamentoGETCondicoesPagamento();
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		mFormaPagamentoSelecionada = mFormasPagamento.get(posicao);
		mViewSelecionada = view;
		consumirFormaPagamentoGETCondicoesPagamento();
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
						.append("formaspagamento/")
						.append("search/")
						.append("findByDescricaoContaining")
						.append("?descricao=").append(query)
						.append("&page=0&size=20");
		HRef pagina0 = new HRef(url.toString());
		consumirFormasPagamentoGETPaginado(pagina0);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.formas_pagamento_pesquisa_titulo);
		if (mFormasPagamento.isEmpty()) {
			String url = getString(R.string.requisicao_url_base) + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirFormasPagamentoGETPaginado(pagina0);
		}
		alternarLabel0Registros();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(FORMAS_PAGAMENTO, mFormasPagamento);
	}

	private void addFormaPagamento(@NonNull FormaPagamento formaPagamento) {
		if (formaPagamento.getId().compareTo(BigInteger.ZERO) > 0) {
			mFormasPagamento.add(formaPagamento);
			int indice = mFormasPagamento.indexOf(formaPagamento);
			mAdaptadorFormasPagamento.notifyItemInserted(indice);
		}
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mFormasPagamento.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ColecaoCallback<Colecao<CondicaoPagamentoDTO>> callbackFormaPagamentoGETCondicoesPagamento() {
		return new ColecaoCallback<Colecao<CondicaoPagamentoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<CondicaoPagamentoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirFormasPagamentoPaginado, false);
				Falha.tratar(mProgressBarConsumirFormasPagamentoPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<CondicaoPagamentoDTO>> chamada,
			                       @NonNull Response<Colecao<CondicaoPagamentoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirFormasPagamentoPaginado, false);
					Falha.tratar(mProgressBarConsumirFormasPagamentoPaginado, resposta);
				} else {
					Set<CondicaoPagamentoDTO> dtos = resposta.body()
							.getEmbedded().getDtos();
					Set<CondicaoPagamento> condicoesPagamento =
							CondicaoPagamentoMapeamento
									.paraDominios(dtos, mFormaPagamentoSelecionada);
					mFormaPagamentoSelecionada.getCondicoesPagamento()
							.addAll(condicoesPagamento);
					ocultarProgresso(mProgressBarConsumirFormasPagamentoPaginado, true);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<FormaPagamentoDTO>> callbackFormasPagamentoGETPaginado() {
		return new ColecaoCallback<Colecao<FormaPagamentoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<FormaPagamentoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirFormasPagamentoPaginado, false);
				Falha.tratar(mProgressBarConsumirFormasPagamentoPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<FormaPagamentoDTO>> chamada,
			                       @NonNull Response<Colecao<FormaPagamentoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirFormasPagamentoPaginado, resposta);
				} else {
					Colecao<FormaPagamentoDTO> colecao = resposta.body();
					Set<FormaPagamentoDTO> dtos = colecao.getEmbedded()
							.getDtos();
					Set<FormaPagamento> formasPagamento =
							FormaPagamentoMapeamento.paraDominios(dtos);
					for (FormaPagamento formaPagamento : formasPagamento) {
						addFormaPagamento(formaPagamento);
					}
					alternarLabel0Registros();
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirFormasPagamentoPaginado, false);
			}
		};
	}

	private void consumirFormaPagamentoGETCondicoesPagamento() {
		exibirProgresso(mProgressBarConsumirFormasPagamentoPaginado);
		// TODO: 9/25/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("formaspagamento/%d/condicoesPagamento");
		HRef href =
				new HRef(String.format(url, mFormaPagamentoSelecionada.getId()));
		++mTarefasPendentes;
		FormaPagamentoRequisicao
				.getCondicoesPagamento(callbackFormaPagamentoGETCondicoesPagamento(), href);
	}

	private void consumirFormasPagamentoGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirFormasPagamentoPaginado);
		exibirProgresso(mProgressBarConsumirFormasPagamentoPaginado);
		int tamanho = mFormasPagamento.size();
		mFormasPagamento.clear();
		mAdaptadorFormasPagamento.notifyItemRangeRemoved(0, tamanho);
		++mTarefasPendentes;
		FormaPagamentoRequisicao
				.getPaginado(callbackFormasPagamentoGETPaginado(), href);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso) {
		progresso.setVisibility(View.VISIBLE);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              boolean chamarOuvinte) {
		if (mTarefasPendentes <= 0) {
			alternarLabel0Registros();
			progresso.setVisibility(View.GONE);
			if (chamarOuvinte) {
				// TODO: 9/18/17 definir implementações diferentes para clique curto e longo
				if (mOnLongoFormaPagamentoSelecionadoListener != null) {
					mOnLongoFormaPagamentoSelecionadoListener
							.onLongoFormaPagamentoSelecionado(mViewSelecionada, mFormaPagamentoSelecionada);
				}
				if (mOnFormaPagamentoSelecionadoListener != null) {
					mOnFormaPagamentoSelecionadoListener
							.onFormaPagamentoSelecionado(mViewSelecionada, mFormaPagamentoSelecionada);
				}
				getActivity().onBackPressed();
			}
		}
	}

	public void setOnFormaPagamentoSelecionadoListener(@Nullable OnFormaPagamentoSelecionadoListener ouvinte) {
		mOnFormaPagamentoSelecionadoListener = ouvinte;
	}

	public void setOnLongoFormaPagamentoSelecionadoListener(@Nullable OnLongoFormaPagamentoSelecionadoListener ouvinte) {
		mOnLongoFormaPagamentoSelecionadoListener = ouvinte;
	}

	public interface OnFormaPagamentoSelecionadoListener {

		void onFormaPagamentoSelecionado(@NonNull View view,
		                                 @NonNull FormaPagamento formaPagamento);
	}

	public interface OnLongoFormaPagamentoSelecionadoListener {

		void onLongoFormaPagamentoSelecionado(@NonNull View view,
		                                      @NonNull FormaPagamento formaPagamento);
	}

	private final class OnFormaPagamentoScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = -5316240269269038979L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerFormasPagamento
							.getLayoutManager();
			if (mFormasPagamento.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirFormasPagamentoGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
