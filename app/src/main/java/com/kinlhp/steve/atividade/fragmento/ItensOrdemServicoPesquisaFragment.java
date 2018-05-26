package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerItensOrdemServico;
import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.dominio.Ordem;

import java.io.Serializable;
import java.util.ArrayList;

public class ItensOrdemServicoPesquisaFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerItensOrdemServico.OnItemClickListener,
		AdaptadorRecyclerItensOrdemServico.OnItemLongClickListener,
		Serializable {
	private static final long serialVersionUID = 1072431081988394796L;
	private static final String ITENS_ORDEM_SERVICO = "itensOrdemServico";
	private static final String ORDEM = "ordem";
	private AdaptadorRecyclerItensOrdemServico mAdaptadorItensOrdemServico;
	private ArrayList<ItemOrdemServico> mItensOrdemServico;
	private OnItemOrdemServicoSelecionadoListener mOnItemOrdemServicoSelecionadoListener;
	private OnLongoItemOrdemServicoSelecionadoListener mOnLongoItemOrdemServicoSelecionadoListener;
	private Ordem mOrdem;

	private AppCompatImageButton mButtonNovoItemOrdemServico;
	private AppCompatTextView mLabel0Registros;
	private RecyclerView mRecyclerItensOrdemServico;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ItensOrdemServicoPesquisaFragment() {
	}

	public static ItensOrdemServicoPesquisaFragment newInstance(@NonNull ArrayList<ItemOrdemServico> itensOrdemServico) {
		ItensOrdemServicoPesquisaFragment fragmento =
				new ItensOrdemServicoPesquisaFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(ITENS_ORDEM_SERVICO, itensOrdemServico);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_item_ordem_servico:
				ItemOrdemServico itemOrdemServico = ItemOrdemServico.builder()
						.valorOrcamento(null).valorServico(null).build();
				if (mOnItemOrdemServicoSelecionadoListener != null) {
					mOnItemOrdemServicoSelecionadoListener
							.onItemOrdemServicoSelecionado(view, itemOrdemServico);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mOrdem = (Ordem) savedInstanceState.getSerializable(ORDEM);
			//noinspection unchecked
			mItensOrdemServico =
					(ArrayList<ItemOrdemServico>) savedInstanceState
							.getSerializable(ITENS_ORDEM_SERVICO);
		} else if (getArguments() != null) {
			mOrdem = (Ordem) getArguments().getSerializable(ORDEM);
			//noinspection unchecked
			mItensOrdemServico = (ArrayList<ItemOrdemServico>) getArguments()
					.getSerializable(ITENS_ORDEM_SERVICO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_itens_ordem_servico_pesquisa, container, false);
		mButtonNovoItemOrdemServico = view
				.findViewById(R.id.button_novo_item_ordem_servico);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mRecyclerItensOrdemServico = view
				.findViewById(R.id.recycler_itens_ordem_servico);

		mRecyclerItensOrdemServico.setHasFixedSize(true);
		mAdaptadorItensOrdemServico =
				new AdaptadorRecyclerItensOrdemServico(mItensOrdemServico);
		mAdaptadorItensOrdemServico.setOnItemClickListener(this);
		mAdaptadorItensOrdemServico.setOnItemLongClickListener(this);
		mRecyclerItensOrdemServico.setAdapter(mAdaptadorItensOrdemServico);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerItensOrdemServico.setLayoutManager(gerenciador);

		mButtonNovoItemOrdemServico.setOnClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		ItemOrdemServico itemOrdemServico = mItensOrdemServico.get(posicao);
		if (mOnItemOrdemServicoSelecionadoListener != null) {
			mOnItemOrdemServicoSelecionadoListener
					.onItemOrdemServicoSelecionado(view, itemOrdemServico);
		}
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		ItemOrdemServico itemOrdemServico = mItensOrdemServico.get(posicao);
		if (mOnLongoItemOrdemServicoSelecionadoListener != null) {
			mOnLongoItemOrdemServicoSelecionadoListener
					.onLongoItemOrdemServicoSelecionado(view, itemOrdemServico);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.ordem_cadastro_label_itens_ordem_servico_hint);
		alternarButtonNovoItemOrdemServico();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ORDEM, mOrdem);
		outState.putSerializable(ITENS_ORDEM_SERVICO, mItensOrdemServico);
	}

	public void addItemOrdemServico(@NonNull ItemOrdemServico itemOrdemServico) {
		mItensOrdemServico.add(itemOrdemServico);
		int indice = mItensOrdemServico.indexOf(itemOrdemServico);
		mAdaptadorItensOrdemServico.notifyItemInserted(indice);
	}

	private void alternarButtonNovoItemOrdemServico() {
		mButtonNovoItemOrdemServico
				.setVisibility(mOrdem.getSituacao().equals(Ordem.Situacao.ABERTO)
						? View.VISIBLE : View.GONE);
		mLabel0Registros.setVisibility(mItensOrdemServico.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	public void removeItemOrdemServico(@NonNull ItemOrdemServico itemOrdemServico) {
		if (mItensOrdemServico.contains(itemOrdemServico)) {
			int indice = mItensOrdemServico.indexOf(itemOrdemServico);
			mItensOrdemServico.remove(indice);
			mAdaptadorItensOrdemServico.notifyItemRemoved(indice);
		}
		alternarButtonNovoItemOrdemServico();
	}

	public void setItensOrdemServico(@NonNull ArrayList<ItemOrdemServico> itensOrdemServico) {
		mItensOrdemServico = itensOrdemServico;
		if (getArguments() != null) {
			getArguments().putSerializable(ORDEM, mOrdem);
			getArguments()
					.putSerializable(ITENS_ORDEM_SERVICO, mItensOrdemServico);
		}
	}

	public void setOnItemOrdemServicoSelecionadoListener(@Nullable OnItemOrdemServicoSelecionadoListener ouvinte) {
		mOnItemOrdemServicoSelecionadoListener = ouvinte;
	}

	public void setOnLongoItemOrdemServicoSelecionadoListener(@Nullable OnLongoItemOrdemServicoSelecionadoListener ouvinte) {
		mOnLongoItemOrdemServicoSelecionadoListener = ouvinte;
	}

	public void setOrdem(@NonNull Ordem ordem) {
		mOrdem = ordem;
		if (getArguments() != null) {
			getArguments().putSerializable(ORDEM, mOrdem);
		}
	}

	public interface OnItemOrdemServicoSelecionadoListener {

		void onItemOrdemServicoSelecionado(@NonNull View view,
		                                   @NonNull ItemOrdemServico itemOrdemServico);
	}

	public interface OnLongoItemOrdemServicoSelecionadoListener {

		void onLongoItemOrdemServicoSelecionado(@NonNull View view,
		                                        @NonNull ItemOrdemServico itemOrdemServico);
	}
}
