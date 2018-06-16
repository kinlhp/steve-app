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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerServicos;
import com.kinlhp.steve.dominio.Servico;
import com.kinlhp.steve.dto.ServicoDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.ServicoMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.ServicoRequisicao;
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

public class ServicosPesquisaFragment extends Fragment
		implements AdaptadorRecyclerServicos.OnItemClickListener,
		AdaptadorRecyclerServicos.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final long serialVersionUID = 3615146513082561142L;
	private static final String LINKS = "_links";
	private static final String PAGINA_0 = "servicos?sort=descricao,asc&page=0&size=20";
	private static final String SERVICOS = "servicos";
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();
	private AdaptadorRecyclerServicos mAdaptadorServicos;
	private ArrayList<Servico> mServicos = new ArrayList<>();
	private Links mLinks;
	private OnServicoSelecionadoListener mOnServicoSelecionadoListener;
	private OnLongoServicoSelecionadoListener mOnLongoServicoSelecionadoListener;
	private int mTarefasPendentes;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirServicosPaginado;
	private RecyclerView mRecyclerServicos;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ServicosPesquisaFragment() {
	}

	public static ServicosPesquisaFragment newInstance() {
		ServicosPesquisaFragment fragmento = new ServicosPesquisaFragment();
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
				.inflate(R.layout.fragment_servicos_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirServicosPaginado = view
				.findViewById(R.id.progress_bar_consumir_servicos_paginado);
		mRecyclerServicos = view.findViewById(R.id.recycler_servicos);

		mRecyclerServicos.setHasFixedSize(true);
		mAdaptadorServicos = new AdaptadorRecyclerServicos(mServicos);
		mAdaptadorServicos.setOnItemClickListener(this);
		mAdaptadorServicos.setOnItemLongClickListener(this);
		mRecyclerServicos.setAdapter(mAdaptadorServicos);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerServicos.setLayoutManager(gerenciador);

		mRecyclerServicos.addOnScrollListener(new OnServicoScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		Servico servico = mServicos.get(posicao);
		if (mOnServicoSelecionadoListener != null) {
			mOnServicoSelecionadoListener.onServicoSelecionado(view, servico);
		}
		getActivity().onBackPressed();
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Servico servico = mServicos.get(posicao);
		if (mOnLongoServicoSelecionadoListener != null) {
			mOnLongoServicoSelecionadoListener
					.onLongoServicoSelecionado(view, servico);
		}
		getActivity().onBackPressed();
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
						.append("servicos/")
						.append("search/")
						.append("descricao")
						.append("?descricao=").append(query)
						.append("&sort=descricao,asc")
						.append("&page=0&size=20");
		HRef pagina0 = new HRef(url.toString());
		consumirServicosGETPesquisaPaginado(pagina0);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.servicos_pesquisa_titulo);
		if (mServicos.isEmpty()) {
			String url = URL_BASE + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirServicosGETPaginado(pagina0);
		}
		alternarLabel0Registros();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(SERVICOS, mServicos);
	}

	private void addServico(@NonNull Servico servico) {
		if (servico.getId().compareTo(BigInteger.ZERO) > 0) {
			mServicos.add(servico);
			int indice = mServicos.indexOf(servico);
			mAdaptadorServicos.notifyItemInserted(indice);
		}
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mServicos.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ColecaoCallback<Colecao<ServicoDTO>> callbackServicosGETPaginado() {
		return new ColecaoCallback<Colecao<ServicoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ServicoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirServicosPaginado);
				Falha.tratar(mProgressBarConsumirServicosPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ServicoDTO>> chamada,
			                       @NonNull Response<Colecao<ServicoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirServicosPaginado, resposta);
				} else {
					Colecao<ServicoDTO> colecao = resposta.body();
					Set<ServicoDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Servico> servicos = ServicoMapeamento
							.paraDominios(dtos);
					for (Servico servico : servicos) {
						addServico(servico);
					}
					alternarLabel0Registros();
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirServicosPaginado);
			}
		};
	}

	private ColecaoCallback<Colecao<ServicoDTO>> callbackServicosGETPesquisaPaginado() {
		return new ColecaoCallback<Colecao<ServicoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<ServicoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirServicosPaginado);
				Falha.tratar(mProgressBarConsumirServicosPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<ServicoDTO>> chamada,
			                       @NonNull Response<Colecao<ServicoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirServicosPaginado, resposta);
				} else {
					Colecao<ServicoDTO> colecao = resposta.body();
					Set<ServicoDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Servico> servicos = ServicoMapeamento
							.paraDominios(dtos);
					for (Servico servico : servicos) {
						addServico(servico);
					}
					alternarLabel0Registros();
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirServicosPaginado);
			}
		};
	}

	private void consumirServicosGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirServicosPaginado);
		exibirProgresso(mProgressBarConsumirServicosPaginado);
		mAdaptadorServicos.notifyItemRangeRemoved(0, mServicos.size());
		++mTarefasPendentes;
		ServicoRequisicao.getPaginado(callbackServicosGETPaginado(), href);
	}

	private void consumirServicosGETPesquisaPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirServicosPaginado);
		exibirProgresso(mProgressBarConsumirServicosPaginado);
		mAdaptadorServicos.notifyItemRangeRemoved(0, mServicos.size());
		mServicos.clear();
		mLinks = null;
		++mTarefasPendentes;
		ServicoRequisicao.getPaginado(callbackServicosGETPesquisaPaginado(), href);
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

	public void setOnServicoSelecionadoListener(@Nullable OnServicoSelecionadoListener ouvinte) {
		mOnServicoSelecionadoListener = ouvinte;
	}

	public void setOnLongoServicoSelecionadoListener(@Nullable OnLongoServicoSelecionadoListener ouvinte) {
		mOnLongoServicoSelecionadoListener = ouvinte;
	}

	public interface OnServicoSelecionadoListener {

		void onServicoSelecionado(@NonNull View view, @NonNull Servico servico);
	}

	public interface OnLongoServicoSelecionadoListener {

		void onLongoServicoSelecionado(@NonNull View view,
		                               @NonNull Servico servico);
	}

	private final class OnServicoScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = -5722773078434489794L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerServicos.getLayoutManager();
			if (mServicos.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirServicosGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
