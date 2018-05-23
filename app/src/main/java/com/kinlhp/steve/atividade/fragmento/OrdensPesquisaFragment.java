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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerOrdens;
import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Servico;
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

public class OrdensPesquisaFragment extends Fragment
		implements AdaptadorRecyclerOrdens.OnItemClickListener,
		AdaptadorRecyclerOrdens.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final long serialVersionUID = 1208743848156612458L;
	private static final String LINKS = "_links";
	private static final String PAGINA_0 = "ordens?sort=id,asc&page=0&size=20";
	private static final String ORDENS = "ordens";
	private AdaptadorRecyclerOrdens mAdaptadorOrdens;
	private ArrayList<Ordem> mOrdens = new ArrayList<>();
	private Ordem mOrdemSelecionada;
	private Links mLinks;
	private OnLongoOrdemSelecionadoListener mOnLongoOrdemSelecionadoListener;
	private OnOrdemSelecionadoListener mOnOrdemSelecionadoListener;
	private int mTarefasPendentes;
	private View mViewSelecionada;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirOrdensPaginado;
	private RecyclerView mRecyclerOrdens;

	/**
	 * Construtor padrão é obrigatório
	 */
	public OrdensPesquisaFragment() {
	}

	public static OrdensPesquisaFragment newInstance() {
		OrdensPesquisaFragment fragmento = new OrdensPesquisaFragment();
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
				.inflate(R.layout.fragment_ordens_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirOrdensPaginado = view
				.findViewById(R.id.progress_bar_consumir_ordens_paginado);
		mRecyclerOrdens = view.findViewById(R.id.recycler_ordens);

		mRecyclerOrdens.setHasFixedSize(true);
		mAdaptadorOrdens = new AdaptadorRecyclerOrdens(mOrdens);
		mAdaptadorOrdens.setOnItemClickListener(this);
		mAdaptadorOrdens.setOnItemLongClickListener(this);
		mRecyclerOrdens.setAdapter(mAdaptadorOrdens);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerOrdens.setLayoutManager(gerenciador);

		mRecyclerOrdens.addOnScrollListener(new OnOrdemScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mOrdemSelecionada = mOrdens.get(posicao);
		mViewSelecionada = view;
		consumirOrdemGETItensOrdemServico();
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		mOrdemSelecionada = mOrdens.get(posicao);
		mViewSelecionada = view;
		consumirOrdemGETItensOrdemServico();
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
						.append("ordens/")
						.append("search/")
						.append("findByIdOrCnpjCpfCliente")
						.append("?id=").append(query)
						.append("&cnpjCpfCliente=").append(query)
						.append("&page=0&size=20");
		HRef pagina0 = new HRef(url.toString());
		consumirOrdensGETPaginado(pagina0);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.ordens_pesquisa_titulo);
		if (mOrdens.isEmpty()) {
			String url = getString(R.string.requisicao_url_base) + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirOrdensGETPaginado(pagina0);
		}
		alternarLabel0Registros();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(ORDENS, mOrdens);
	}

	private void addOrdem(@NonNull Ordem ordem) {
		if (ordem.getId().compareTo(BigInteger.ZERO) > 0) {
			mOrdens.add(ordem);
			int indice = mOrdens.indexOf(ordem);
			mAdaptadorOrdens.notifyItemInserted(indice);
		}
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mOrdens.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ItemCallback<ServicoDTO> callbackItemOrdemServicoGETServico(@NonNull ItemOrdemServico itemOrdemServico) {
		return new ItemCallback<ServicoDTO>() {

			@Override
			public void onFailure(@NonNull Call<ServicoDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
				Falha.tratar(mProgressBarConsumirOrdensPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<ServicoDTO> chamada,
			                       @NonNull Response<ServicoDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
					Falha.tratar(mProgressBarConsumirOrdensPaginado, resposta);
				} else {
					ServicoDTO dto = resposta.body();
					Servico servico = ServicoMapeamento.paraDominio(dto);
					itemOrdemServico.setServico(servico);
					ocultarProgresso(mProgressBarConsumirOrdensPaginado, true);
				}
			}
		};
	}

	private ItemCallback<PessoaDTO> callbackOrdemGETCliente(@NonNull Ordem ordem) {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
				Falha.tratar(mProgressBarConsumirOrdensPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
					Falha.tratar(mProgressBarConsumirOrdensPaginado, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa cliente = PessoaMapeamento.paraDominio(dto);
					ordem.setCliente(cliente);
					addOrdem(ordem);
					ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<ItemOrdemServicoDTO>> callbackOrdemGETItensOrdemServico() {
		return new ColecaoCallback<Colecao<ItemOrdemServicoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ItemOrdemServicoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
				Falha.tratar(mProgressBarConsumirOrdensPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ItemOrdemServicoDTO>> chamada,
			                       @NonNull Response<Colecao<ItemOrdemServicoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
					Falha.tratar(mProgressBarConsumirOrdensPaginado, resposta);
				} else {
					Set<ItemOrdemServicoDTO> dtos = resposta.body()
							.getEmbedded().getDtos();
					Set<ItemOrdemServico> itensOrdemServico =
							ItemOrdemServicoMapeamento
									.paraDominios(dtos, mOrdemSelecionada);
					mOrdemSelecionada.getItens().addAll(itensOrdemServico);
					for (ItemOrdemServico itemOrdemServico : itensOrdemServico) {
						consumirItemOrdemServicoGETServico(itemOrdemServico);
					}
					ocultarProgresso(mProgressBarConsumirOrdensPaginado, true);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<OrdemDTO>> callbackOrdensGETPaginado() {
		return new ColecaoCallback<Colecao<OrdemDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<OrdemDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
				Falha.tratar(mProgressBarConsumirOrdensPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<OrdemDTO>> chamada,
			                       @NonNull Response<Colecao<OrdemDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirOrdensPaginado, resposta);
				} else {
					Colecao<OrdemDTO> colecao = resposta.body();
					Set<OrdemDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Ordem> ordens = OrdemMapeamento.paraDominios(dtos);
					for (Ordem ordem : ordens) {
						consumirOrdemGETCliente(ordem);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirOrdensPaginado, false);
			}
		};
	}

	private void consumirItemOrdemServicoGETServico(@NonNull ItemOrdemServico itemOrdemServico) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("itensordemservico/%d/servico");
		HRef href = new HRef(String.format(url, itemOrdemServico.getId()));
		++mTarefasPendentes;
		ItemOrdemServicoRequisicao
				.getServico(callbackItemOrdemServicoGETServico(itemOrdemServico), href);
	}

	private void consumirOrdemGETCliente(@NonNull Ordem ordem) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("ordens/%d/cliente");
		HRef href = new HRef(String.format(url, ordem.getId()));
		++mTarefasPendentes;
		OrdemRequisicao.getCliente(callbackOrdemGETCliente(ordem), href);
	}

	private void consumirOrdemGETItensOrdemServico() {
		exibirProgresso(mProgressBarConsumirOrdensPaginado);
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("ordens/%d/itens");
		HRef href = new HRef(String.format(url, mOrdemSelecionada.getId()));
		++mTarefasPendentes;
		OrdemRequisicao.getItens(callbackOrdemGETItensOrdemServico(), href);
	}

	private void consumirOrdensGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirOrdensPaginado);
		exibirProgresso(mProgressBarConsumirOrdensPaginado);
		mAdaptadorOrdens.notifyItemRangeRemoved(0, mOrdens.size());
		++mTarefasPendentes;
		OrdemRequisicao.getPaginado(callbackOrdensGETPaginado(), href);
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
				if (mOnLongoOrdemSelecionadoListener != null) {
					mOnLongoOrdemSelecionadoListener
							.onLongoOrdemSelecionado(mViewSelecionada, mOrdemSelecionada);
				}
				if (mOnOrdemSelecionadoListener != null) {
					mOnOrdemSelecionadoListener
							.onOrdemSelecionado(mViewSelecionada, mOrdemSelecionada);
				}
				getActivity().onBackPressed();
			}
		}
	}

	public void setOnLongoOrdemSelecionadoListener(@Nullable OnLongoOrdemSelecionadoListener ouvinte) {
		mOnLongoOrdemSelecionadoListener = ouvinte;
	}

	public void setOnOrdemSelecionadoListener(@Nullable OnOrdemSelecionadoListener ouvinte) {
		mOnOrdemSelecionadoListener = ouvinte;
	}

	public interface OnLongoOrdemSelecionadoListener {

		void onLongoOrdemSelecionado(@NonNull View view, @NonNull Ordem ordem);
	}

	public interface OnOrdemSelecionadoListener {

		void onOrdemSelecionado(@NonNull View view, @NonNull Ordem ordem);
	}

	private final class OnOrdemScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = 3588439901319351358L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerOrdens.getLayoutManager();
			if (mOrdens.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirOrdensGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
