package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerPessoas;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.PessoaRequisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.Links;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class PessoasPesquisaFragment extends Fragment
		implements
		AdaptadorRecyclerPessoas.OnItemClickListener,
		AdaptadorRecyclerPessoas.OnItemLongClickListener,
		MenuItem.OnActionExpandListener,
		SearchView.OnQueryTextListener, Serializable {
	private static final long serialVersionUID = 8596121901536938807L;
	private static final String LINKS = "_links";
	private static final String PAGINA_0 = "pessoas?page=0&size=20";
	private static final String PESSOAS = "pessoas";
	private AdaptadorRecyclerPessoas mAdaptadorPessoas;
	private ArrayList<Pessoa> mPessoas = new ArrayList<>();
	private Links mLinks;
	private OnPessoaSelecionadaListener mOnPessoaSelecionadaListener;
	private OnLongoPessoaSelecionadaListener mOnLongoPessoaSelecionadaListener;
	private int mTarefasPendentes;

	private ProgressBar mProgressBarConsumirPessoasPaginado;
	private RecyclerView mRecyclerPessoas;

	/**
	 * Construtor padrão é obrigatório
	 */
	public PessoasPesquisaFragment() {
	}

	public static PessoasPesquisaFragment newInstance() {
		PessoasPesquisaFragment fragmento = new PessoasPesquisaFragment();
		Bundle argumentos = new Bundle();
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (savedInstanceState != null) {
		//noinspection unchecked
//			mPessoas = (ArrayList<Pessoa>) savedInstanceState
//					.getSerializable(PESSOAS);
//			mLinks = (Links) savedInstanceState.getSerializable(LINKS);
//		} else if (getArguments() != null) {
		//noinspection unchecked
//			mPessoas = (ArrayList<Pessoa>) getArguments()
//					.getSerializable(PESSOAS);
//			mLinks = (Links) getArguments().getSerializable(LINKS);
//		}
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
				.inflate(R.layout.fragment_pessoas_pesquisa, container, false);
		mProgressBarConsumirPessoasPaginado = view
				.findViewById(R.id.progress_bar_consumir_pessoas_paginado);
		mRecyclerPessoas = view.findViewById(R.id.recycler_pessoas);

		mRecyclerPessoas.setHasFixedSize(true);
		mAdaptadorPessoas = new AdaptadorRecyclerPessoas(mPessoas);
		mAdaptadorPessoas.setOnItemClickListener(this);
		mAdaptadorPessoas.setOnItemLongClickListener(this);
		mRecyclerPessoas.setAdapter(mAdaptadorPessoas);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerPessoas.setLayoutManager(gerenciador);

		mRecyclerPessoas
				.addOnScrollListener(recyclerPessoasAddOnScrollListener());

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		Pessoa pessoa = mPessoas.get(posicao);
		if (mOnPessoaSelecionadaListener != null) {
			mOnPessoaSelecionadaListener.onPessoaSelecionada(view, pessoa);
		}
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Pessoa pessoa = mPessoas.get(posicao);
		if (mOnLongoPessoaSelecionadaListener != null) {
			mOnLongoPessoaSelecionadaListener
					.onLongoPessoaSelecionada(view, pessoa);
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
		if (TextUtils.isEmpty(query)) {
			Toast.makeText(getActivity(), "Cancelar/Limpar pesquisa", Toast.LENGTH_SHORT).show();
			return true;
		}
		Toast.makeText(getActivity(), "Consumir pessoas GET", Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoas_pesquisa_titulo);
		if (mPessoas.isEmpty()) {
			String url = getString(R.string.requisicao_url_base) + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirPessoasGETPaginado(pagina0);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(PESSOAS, mPessoas);
	}

	private void addPessoas(@NonNull ArrayList<Pessoa> pessoas,
	                        @Nullable Links links) {
		for (Pessoa pessoa : pessoas) {
			mPessoas.add(pessoa);
			int indice = mPessoas.indexOf(pessoa);
			mAdaptadorPessoas.notifyItemInserted(indice);
		}
		mLinks = links;
		if (getArguments() != null) {
			getArguments().putSerializable(LINKS, mLinks);
		}
	}

	private ColecaoCallback<Colecao<PessoaDTO>> callbackPessoasGETPaginado() {
		return new ColecaoCallback<Colecao<PessoaDTO>>() {
			@Override
			public void onFailure(@NonNull Call<Colecao<PessoaDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, null);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<PessoaDTO>> chamada,
			                       @NonNull Response<Colecao<PessoaDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					Colecao<PessoaDTO> colecao = resposta.body();
					Set<PessoaDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Pessoa> pessoas = PessoaMapeamento.paraDominios(dtos);
					addPessoas(new ArrayList<>(pessoas), colecao.getLinks());
				}
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, null);
			}
		};
	}

	private void consumirPessoasGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirPessoasPaginado);
		exibirProgresso(mProgressBarConsumirPessoasPaginado, null);
		++mTarefasPendentes;
		PessoaRequisicao.getPaginado(callbackPessoasGETPaginado(), href);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
		}
	}

	private RecyclerView.OnScrollListener recyclerPessoasAddOnScrollListener() {
		return new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				LinearLayoutManager gerenciador =
						(LinearLayoutManager) mRecyclerPessoas.getLayoutManager();
				if (mPessoas.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
					if (mLinks != null && mLinks.getNext() != null) {
						consumirPessoasGETPaginado(mLinks.getNext());
					}
				}
			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
			                                 int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}
		};
	}

	public void setOnPessoaSelecionadaListener(@Nullable OnPessoaSelecionadaListener ouvinte) {
		mOnPessoaSelecionadaListener = ouvinte;
	}

	public void setOnLongoPessoaSelecionadaListener(@Nullable OnLongoPessoaSelecionadaListener ouvinte) {
		mOnLongoPessoaSelecionadaListener = ouvinte;
	}

	public interface OnPessoaSelecionadaListener {

		void onPessoaSelecionada(@NonNull View view, @NonNull Pessoa pessoa);
	}

	public interface OnLongoPessoaSelecionadaListener {

		void onLongoPessoaSelecionada(@NonNull View view,
		                              @NonNull Pessoa pessoa);
	}
}
