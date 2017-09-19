package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerCredenciais;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CredencialMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.CredencialRequisicao;
import com.kinlhp.steve.requisicao.Falha;
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

public class CredenciaisPesquisaFragment extends Fragment
		implements AdaptadorRecyclerCredenciais.OnItemClickListener,
		AdaptadorRecyclerCredenciais.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final long serialVersionUID = 2480043693410326387L;
	private static final String LINKS = "_links";
	private static final String PAGINA_0 = "credenciais?page=0&size=20";
	private static final String CREDENCIAIS = "credenciais";
	private AdaptadorRecyclerCredenciais mAdaptadorCredenciais;
	private ArrayList<Credencial> mCredenciais = new ArrayList<>();
	private Links mLinks;
	private OnCredencialSelecionadoListener mOnCredencialSelecionadoListener;
	private OnLongoCredencialSelecionadoListener mOnLongoCredencialSelecionadoListener;
	private int mTarefasPendentes;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirCredenciaisPaginado;
	private RecyclerView mRecyclerCredenciais;

	/**
	 * Construtor padrão é obrigatório
	 */
	public CredenciaisPesquisaFragment() {
	}

	public static CredenciaisPesquisaFragment newInstance() {
		CredenciaisPesquisaFragment fragmento =
				new CredenciaisPesquisaFragment();
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
				.inflate(R.layout.fragment_credenciais_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirCredenciaisPaginado = view
				.findViewById(R.id.progress_bar_consumir_credenciais_paginado);
		mRecyclerCredenciais = view.findViewById(R.id.recycler_credenciais);

		mRecyclerCredenciais.setHasFixedSize(true);
		mAdaptadorCredenciais = new AdaptadorRecyclerCredenciais(mCredenciais);
		mAdaptadorCredenciais.setOnItemClickListener(this);
		mAdaptadorCredenciais.setOnItemLongClickListener(this);
		mRecyclerCredenciais.setAdapter(mAdaptadorCredenciais);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerCredenciais.setLayoutManager(gerenciador);

		mRecyclerCredenciais
				.addOnScrollListener(new OnCredencialScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		Credencial credencial = mCredenciais.get(posicao);
		if (mOnCredencialSelecionadoListener != null) {
			mOnCredencialSelecionadoListener
					.onCredencialSelecionado(view, credencial);
		}
		getActivity().onBackPressed();
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Credencial credencial = mCredenciais.get(posicao);
		if (mOnLongoCredencialSelecionadoListener != null) {
			mOnLongoCredencialSelecionadoListener
					.onLongoCredencialSelecionado(view, credencial);
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
		if (TextUtils.isEmpty(query)) {
			Toast.makeText(getActivity(), "Cancelar/Limpar pesquisa", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Toast.makeText(getActivity(), "Consumir credenciais GET", Toast.LENGTH_SHORT)
				.show();
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.credenciais_pesquisa_titulo);
		if (mCredenciais.isEmpty()) {
			String url = getString(R.string.requisicao_url_base) + PAGINA_0;
			HRef pagina0 = new HRef(url);
			consumirCredenciaisGETPaginado(pagina0);
		}
		alternarLabel0Registros();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(CREDENCIAIS, mCredenciais);
	}

	private void addCredencial(@NonNull Credencial credencial) {
		if (credencial.getId().compareTo(BigInteger.ZERO) > 0) {
			mCredenciais.add(credencial);
			int indice = mCredenciais.indexOf(credencial);
			mAdaptadorCredenciais.notifyItemInserted(indice);
		}
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mCredenciais.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ItemCallback<PessoaDTO> callbackCredencialGETFuncionario(@NonNull Credencial credencial) {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirCredenciaisPaginado);
				Falha.tratar(mProgressBarConsumirCredenciaisPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirCredenciaisPaginado);
					Falha.tratar(mProgressBarConsumirCredenciaisPaginado, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa funcionario = PessoaMapeamento.paraDominio(dto);
					credencial.setFuncionario(funcionario);
					addCredencial(credencial);
					ocultarProgresso(mProgressBarConsumirCredenciaisPaginado);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<CredencialDTO>> callbackCredenciaisGETPaginado() {
		return new ColecaoCallback<Colecao<CredencialDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<CredencialDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirCredenciaisPaginado);
				Falha.tratar(mProgressBarConsumirCredenciaisPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<CredencialDTO>> chamada,
			                       @NonNull Response<Colecao<CredencialDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirCredenciaisPaginado, resposta);
				} else {
					Colecao<CredencialDTO> colecao = resposta.body();
					Set<CredencialDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Credencial> credenciais = CredencialMapeamento
							.paraDominios(dtos);
					for (Credencial credencial : credenciais) {
						consumirCredencialGETFuncionario(credencial);
					}
					alternarLabel0Registros();
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirCredenciaisPaginado);
			}
		};
	}

	private void consumirCredencialGETFuncionario(@NonNull Credencial credencial) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("credenciais/%d/funcionario");
		HRef href = new HRef(String.format(url, credencial.getId()));
		++mTarefasPendentes;
		CredencialRequisicao
				.getFuncionario(callbackCredencialGETFuncionario(credencial), href);
	}

	private void consumirCredenciaisGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirCredenciaisPaginado);
		exibirProgresso(mProgressBarConsumirCredenciaisPaginado);
		++mTarefasPendentes;
		CredencialRequisicao
				.getPaginado(callbackCredenciaisGETPaginado(), href);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso) {
		progresso.setVisibility(View.VISIBLE);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso) {
		if (mTarefasPendentes <= 0) {
			progresso.setVisibility(View.GONE);
		}
	}

	public void setOnLongoCredencialSelecionadoListener(@Nullable OnLongoCredencialSelecionadoListener ouvinte) {
		mOnLongoCredencialSelecionadoListener = ouvinte;
	}

	public void setOnCredencialSelecionadoListener(@Nullable OnCredencialSelecionadoListener ouvinte) {
		mOnCredencialSelecionadoListener = ouvinte;
	}

	public interface OnLongoCredencialSelecionadoListener {

		void onLongoCredencialSelecionado(@NonNull View view,
		                                  @NonNull Credencial credencial);
	}

	public interface OnCredencialSelecionadoListener {

		void onCredencialSelecionado(@NonNull View view,
		                             @NonNull Credencial credencial);
	}

	private final class OnCredencialScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = 8156013106029011148L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerCredenciais.getLayoutManager();
			if (mCredenciais.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirCredenciaisGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
