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
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerContasReceber;
import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.resposta.Links;

import java.io.Serializable;
import java.util.ArrayList;

public class ContasReceberPesquisaFragment extends Fragment implements Serializable {
	private static final String CONTAS_RECEBER = "contasReceber";
	private AdaptadorRecyclerContasReceber mAdaptadorContasReceber;
	private ArrayList<ContaReceber> mContasReceber = new ArrayList<>();
	private Links mLinks;
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

	public static ContasReceberPesquisaFragment newInstance(@NonNull ArrayList<ContaReceber> contasReceber) {
		ContasReceberPesquisaFragment fragmento = new ContasReceberPesquisaFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(CONTAS_RECEBER, contasReceber);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//noinspection unchecked
			mContasReceber = (ArrayList<ContaReceber>) savedInstanceState.getSerializable(CONTAS_RECEBER);
		} else if (getArguments() != null) {
			//noinspection unchecked
			mContasReceber = (ArrayList<ContaReceber>) getArguments().getSerializable(CONTAS_RECEBER);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
//		searchView.setOnQueryTextListener(this);
		searchView.setQueryHint(getString(R.string.app_search));
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contas_receber_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirContasReceberPaginado = view.findViewById(R.id.progress_bar_consumir_contas_receber_paginado);
		mRecyclerContasReceber = view.findViewById(R.id.recycler_contas_receber);

		mRecyclerContasReceber.setHasFixedSize(true);
		mAdaptadorContasReceber = new AdaptadorRecyclerContasReceber(mContasReceber);
//		mAdaptadorContasReceber.setOnItemClickListener(this);
//		mAdaptadorContasReceber.setOnItemLongClickListener(this);
		mRecyclerContasReceber.setAdapter(mAdaptadorContasReceber);
		RecyclerView.LayoutManager gerenciador = new LinearLayoutManager(getActivity());
		mRecyclerContasReceber.setLayoutManager(gerenciador);

		mRecyclerContasReceber.addOnScrollListener(new OnContaReceberScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.contas_receber_pesquisa_titulo);
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mContasReceber.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso) {
		progresso.setVisibility(View.VISIBLE);
	}

	public void setContasReceber(@NonNull ArrayList<ContaReceber> contasReceber) {
		mContasReceber = contasReceber;
		if (getArguments() != null) {
			getArguments().putSerializable(CONTAS_RECEBER, mContasReceber);
		}
	}

	private final class OnContaReceberScrollListener extends RecyclerView.OnScrollListener implements Serializable {
		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador = (LinearLayoutManager) mRecyclerContasReceber.getLayoutManager();
			if (mContasReceber.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				Toast.makeText(getActivity(), "Consumir próxima página", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
