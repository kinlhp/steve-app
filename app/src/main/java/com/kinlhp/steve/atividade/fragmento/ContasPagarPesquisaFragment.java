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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerContasPagar;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dominio.MovimentacaoContaPagar;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.CondicaoPagamentoDTO;
import com.kinlhp.steve.dto.ContaPagarDTO;
import com.kinlhp.steve.dto.MovimentacaoContaPagarDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CondicaoPagamentoMapeamento;
import com.kinlhp.steve.mapeamento.ContaPagarMapeamento;
import com.kinlhp.steve.mapeamento.MovimentacaoContaPagarMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.ContaPagarRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.MovimentacaoContaPagarRequisicao;
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

public class ContasPagarPesquisaFragment extends Fragment
		implements AdaptadorRecyclerContasPagar.OnItemClickListener,
		AdaptadorRecyclerContasPagar.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final String LINKS = "_links";
	private static final String PAGINA_0 = "contasPagar?sort=cedente.nomeRazao,asc&sort=dataVencimento,asc&page=0&size=20";
	private static final String CONTAS_PAGAR = "contasPagar";
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();
	private AdaptadorRecyclerContasPagar mAdaptadorContasPagar;
	private ArrayList<ContaPagar> mContasPagar = new ArrayList<>();
	private ContaPagar mContaPagarSelecionada;
	private Links mLinks;
	private OnLongoContaPagarSelecionadoListener mOnLongoContaPagarSelecionadoListener;
	private OnContaPagarSelecionadoListener mOnContaPagarSelecionadoListener;
	private boolean mOuvinteJaChamado;
	private int mTarefasPendentes;
	private View mViewSelecionada;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirContasPagarPaginado;
	private RecyclerView mRecyclerContasPagar;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ContasPagarPesquisaFragment() {
	}

	public static ContasPagarPesquisaFragment newInstance() {
		ContasPagarPesquisaFragment fragmento = new ContasPagarPesquisaFragment();
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
				.inflate(R.layout.fragment_contas_pagar_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirContasPagarPaginado = view
				.findViewById(R.id.progress_bar_consumir_contas_pagar_paginado);
		mRecyclerContasPagar = view.findViewById(R.id.recycler_contas_pagar);

		mRecyclerContasPagar.setHasFixedSize(true);
		mAdaptadorContasPagar = new AdaptadorRecyclerContasPagar(mContasPagar);
		mAdaptadorContasPagar.setOnItemClickListener(this);
		mAdaptadorContasPagar.setOnItemLongClickListener(this);
		mRecyclerContasPagar.setAdapter(mAdaptadorContasPagar);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerContasPagar.setLayoutManager(gerenciador);

		mRecyclerContasPagar.addOnScrollListener(new OnContaPagarScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mContaPagarSelecionada = mContasPagar.get(posicao);
		if (ContaPagar.Situacao.ABERTO.equals(mContaPagarSelecionada.getSituacao())
				|| ContaPagar.Situacao.AMORTIZADO.equals(mContaPagarSelecionada.getSituacao())) {
			mViewSelecionada = view;
			if (mOnContaPagarSelecionadoListener != null) {
				mOnContaPagarSelecionadoListener
						.onContaPagarSelecionado(mViewSelecionada, mContaPagarSelecionada);
				getActivity().onBackPressed();
			}
		}
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		mContaPagarSelecionada = mContasPagar.get(posicao);
		if (ContaPagar.Situacao.ABERTO.equals(mContaPagarSelecionada.getSituacao())
				|| ContaPagar.Situacao.AMORTIZADO.equals(mContaPagarSelecionada.getSituacao())) {
			mViewSelecionada = view;
			if (mOnLongoContaPagarSelecionadoListener != null) {
				mOnLongoContaPagarSelecionadoListener
						.onLongoContaPagarSelecionado(mViewSelecionada, mContaPagarSelecionada);
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
				new StringBuilder(URL_BASE)
						.append("contasPagar/")
						.append("search/")
						.append("cnpjCpf-nomeRazao-fantasiaSobrenome")
						.append("?cnpjCpf=").append(query)
						.append("&nomeRazao=").append(query)
						.append("&fantasiaSobrenome=").append(query)
						.append("&sort=cedente.nomeRazao,asc&sort=dataVencimento,asc")
						.append("&page=0&size=20");
		HRef pagina0 = new HRef(url.toString());
		consumirContasPagarGETPesquisaPaginado(pagina0);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.contas_pagar_pesquisa_titulo);
		if (mContasPagar.isEmpty()) {
			String url = URL_BASE + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirContasPagarGETPaginado(pagina0);
		}
		alternarLabel0Registros();
		mOuvinteJaChamado = false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(CONTAS_PAGAR, mContasPagar);
	}

	private void addContaPagar(@NonNull ContaPagar contaPagar) {
		if (contaPagar.getId().compareTo(BigInteger.ZERO) > 0
				&& !ContaPagar.Situacao.BAIXADO.equals(contaPagar.getSituacao())
				&& !ContaPagar.Situacao.CANCELADO.equals(contaPagar.getSituacao())) {
			if (mContasPagar.indexOf(contaPagar) < 0) {
				mContasPagar.add(contaPagar);
			}
			mAdaptadorContasPagar.notifyDataSetChanged();
		}
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mContasPagar.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ItemCallback<CondicaoPagamentoDTO> callbackMovimentacaoContaPagarGETCondicaoPagamento(@NonNull MovimentacaoContaPagar movimentacaoContaPagar) {
		return new ItemCallback<CondicaoPagamentoDTO>() {

			@Override
			public void onFailure(@NonNull Call<CondicaoPagamentoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				Falha.tratar(mProgressBarConsumirContasPagarPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<CondicaoPagamentoDTO> chamada,
			                       @NonNull Response<CondicaoPagamentoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
					Falha.tratar(mProgressBarConsumirContasPagarPaginado, resposta);
				} else {
					CondicaoPagamentoDTO dto = resposta.body();
					CondicaoPagamento condicaoPagamento = CondicaoPagamentoMapeamento.paraDominio(dto);
					movimentacaoContaPagar.setCondicaoPagamento(condicaoPagamento);
					ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				}
			}
		};
	}

	private ItemCallback<PessoaDTO> callbackContaPagarGETCedente(@NonNull ContaPagar contaPagar) {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				Falha.tratar(mProgressBarConsumirContasPagarPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
					Falha.tratar(mProgressBarConsumirContasPagarPaginado, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa fornecedor = PessoaMapeamento.paraDominio(dto);
					contaPagar.setCedente(fornecedor);
					addContaPagar(contaPagar);
					ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<MovimentacaoContaPagarDTO>> callbackContaPagarGETMovimentacoesContaPagar(@NonNull ContaPagar contaPagar) {
		return new ColecaoCallback<Colecao<MovimentacaoContaPagarDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<MovimentacaoContaPagarDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				Falha.tratar(mProgressBarConsumirContasPagarPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<MovimentacaoContaPagarDTO>> chamada,
			                       @NonNull Response<Colecao<MovimentacaoContaPagarDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
					Falha.tratar(mProgressBarConsumirContasPagarPaginado, resposta);
				} else {
					Set<MovimentacaoContaPagarDTO> dtos = resposta.body()
							.getEmbedded().getDtos();
					Set<MovimentacaoContaPagar> movimentacoesContaPagar =
							MovimentacaoContaPagarMapeamento
									.paraDominios(dtos, mContaPagarSelecionada);
//					mContaPagarSelecionada.getMovimentacoes().addAll(movimentacoesContaPagar);
					contaPagar.setMovimentacoes(movimentacoesContaPagar);
					addContaPagar(contaPagar);
					for (MovimentacaoContaPagar movimentacaoContaPagar : movimentacoesContaPagar) {
						consumirMovimentacaoContaPagarGETCondicaoPagamento(movimentacaoContaPagar);
					}
					ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<ContaPagarDTO>> callbackContasPagarGETPaginado() {
		return new ColecaoCallback<Colecao<ContaPagarDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ContaPagarDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				Falha.tratar(mProgressBarConsumirContasPagarPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ContaPagarDTO>> chamada,
			                       @NonNull Response<Colecao<ContaPagarDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirContasPagarPaginado, resposta);
				} else {
					Colecao<ContaPagarDTO> colecao = resposta.body();
					Set<ContaPagarDTO> dtos = colecao.getEmbedded().getDtos();
					Set<ContaPagar> contasPagar = ContaPagarMapeamento.paraDominios(dtos);
					for (ContaPagar contaPagar : contasPagar) {
						addContaPagar(contaPagar);
						consumirContaPagarGETCedente(contaPagar);
						consumirContaPagarGETMovimentacoesContaPagar(contaPagar);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
			}
		};
	}

	private ColecaoCallback<Colecao<ContaPagarDTO>> callbackContasPagarGETPesquisaPaginado() {
		return new ColecaoCallback<Colecao<ContaPagarDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ContaPagarDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
				Falha.tratar(mProgressBarConsumirContasPagarPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ContaPagarDTO>> chamada,
			                       @NonNull Response<Colecao<ContaPagarDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirContasPagarPaginado, resposta);
				} else {
					Colecao<ContaPagarDTO> colecao = resposta.body();
					Set<ContaPagarDTO> dtos = colecao.getEmbedded().getDtos();
					Set<ContaPagar> contasPagar = ContaPagarMapeamento.paraDominios(dtos);
					for (ContaPagar contaPagar : contasPagar) {
						addContaPagar(contaPagar);
						consumirContaPagarGETCedente(contaPagar);
						consumirContaPagarGETMovimentacoesContaPagar(contaPagar);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirContasPagarPaginado);
			}
		};
	}

	private void consumirMovimentacaoContaPagarGETCondicaoPagamento(@NonNull MovimentacaoContaPagar movimentacaoContaPagar) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("movimentacoesContaPagar/%d/condicaoPagamento");
		HRef href = new HRef(String.format(url, movimentacaoContaPagar.getId()));
		++mTarefasPendentes;
		MovimentacaoContaPagarRequisicao
				.getCondicaoPagamento(callbackMovimentacaoContaPagarGETCondicaoPagamento(movimentacaoContaPagar), href);
	}

	private void consumirContaPagarGETCedente(@NonNull ContaPagar contaPagar) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("contasPagar/%d/cedente");
		HRef href = new HRef(String.format(url, contaPagar.getId()));
		++mTarefasPendentes;
		ContaPagarRequisicao.getCedente(callbackContaPagarGETCedente(contaPagar), href);
	}

	private void consumirContaPagarGETMovimentacoesContaPagar(@NonNull ContaPagar contaPagar) {
		exibirProgresso(mProgressBarConsumirContasPagarPaginado);
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("contasPagar/%d/movimentacoes");
		HRef href = new HRef(String.format(url, contaPagar.getId()));
		++mTarefasPendentes;
		ContaPagarRequisicao.getMovimentacoes(callbackContaPagarGETMovimentacoesContaPagar(contaPagar), href);
	}

	private void consumirContasPagarGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirContasPagarPaginado);
		exibirProgresso(mProgressBarConsumirContasPagarPaginado);
		mAdaptadorContasPagar.notifyItemRangeRemoved(0, mContasPagar.size());
		++mTarefasPendentes;
		ContaPagarRequisicao.getPaginado(callbackContasPagarGETPaginado(), href);
	}

	private void consumirContasPagarGETPesquisaPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirContasPagarPaginado);
		exibirProgresso(mProgressBarConsumirContasPagarPaginado);
		mAdaptadorContasPagar.notifyItemRangeRemoved(0, mContasPagar.size());
		mContasPagar.clear();
		mLinks = null;
		++mTarefasPendentes;
		ContaPagarRequisicao.getPaginado(callbackContasPagarGETPesquisaPaginado(), href);
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

	public void setOnLongoContaPagarSelecionadoListener(@Nullable OnLongoContaPagarSelecionadoListener ouvinte) {
		mOnLongoContaPagarSelecionadoListener = ouvinte;
	}

	public void setOnContaPagarSelecionadoListener(@Nullable OnContaPagarSelecionadoListener ouvinte) {
		mOnContaPagarSelecionadoListener = ouvinte;
	}

	public interface OnLongoContaPagarSelecionadoListener {

		void onLongoContaPagarSelecionado(@NonNull View view, @NonNull ContaPagar contaPagar);
	}

	public interface OnContaPagarSelecionadoListener {

		void onContaPagarSelecionado(@NonNull View view, @NonNull ContaPagar contaPagar);
	}

	private final class OnContaPagarScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = 3588439901319351358L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerContasPagar.getLayoutManager();
			if (mContasPagar.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirContasPagarGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
