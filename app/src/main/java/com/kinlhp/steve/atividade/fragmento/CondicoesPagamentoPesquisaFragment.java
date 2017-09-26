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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerCondicoesPagamento;
import com.kinlhp.steve.dominio.CondicaoPagamento;

import java.io.Serializable;
import java.util.ArrayList;

public class CondicoesPagamentoPesquisaFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerCondicoesPagamento.OnItemClickListener,
		AdaptadorRecyclerCondicoesPagamento.OnItemLongClickListener,
		Serializable {
	private static final long serialVersionUID = -7150964993650524693L;
	private static final String CONDICOES_PAGAMENTO = "condicoesPagamento";
	private AdaptadorRecyclerCondicoesPagamento mAdaptadorCondicoesPagamento;
	private ArrayList<CondicaoPagamento> mCondicoesPagamento;
	private OnCondicaoPagamentoSelecionadoListener mOnCondicaoPagamentoSelecionadoListener;
	private OnLongoCondicaoPagamentoSelecionadoListener mOnLongoCondicaoPagamentoSelecionadoListener;

	private AppCompatImageButton mButtonNovoCondicaoPagamento;
	private AppCompatTextView mLabel0Registros;
	private RecyclerView mRecyclerCondicoesPagamento;

	/**
	 * Construtor padrão é obrigatório
	 */
	public CondicoesPagamentoPesquisaFragment() {
	}

	public static CondicoesPagamentoPesquisaFragment newInstance(@NonNull ArrayList<CondicaoPagamento> condicoesPagamento) {
		CondicoesPagamentoPesquisaFragment fragmento =
				new CondicoesPagamentoPesquisaFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(CONDICOES_PAGAMENTO, condicoesPagamento);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_condicao_pagamento:
				CondicaoPagamento condicaoPagamento = CondicaoPagamento
						.builder().periodoEntreParcelas(null)
						.quantidadeParcelas(null).build();
				if (mOnCondicaoPagamentoSelecionadoListener != null) {
					mOnCondicaoPagamentoSelecionadoListener
							.onCondicaoPagamentoSelecionado(view, condicaoPagamento);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//noinspection unchecked
			mCondicoesPagamento =
					(ArrayList<CondicaoPagamento>) savedInstanceState
							.getSerializable(CONDICOES_PAGAMENTO);
		} else if (getArguments() != null) {
			//noinspection unchecked
			mCondicoesPagamento = (ArrayList<CondicaoPagamento>) getArguments()
					.getSerializable(CONDICOES_PAGAMENTO);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_condicoes_pagamento_pesquisa, container, false);
		mButtonNovoCondicaoPagamento = view
				.findViewById(R.id.button_novo_condicao_pagamento);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mRecyclerCondicoesPagamento = view
				.findViewById(R.id.recycler_condicoes_pagamento);

		mRecyclerCondicoesPagamento.setHasFixedSize(true);
		mAdaptadorCondicoesPagamento =
				new AdaptadorRecyclerCondicoesPagamento(mCondicoesPagamento);
		mAdaptadorCondicoesPagamento.setOnItemClickListener(this);
		mAdaptadorCondicoesPagamento.setOnItemLongClickListener(this);
		mRecyclerCondicoesPagamento.setAdapter(mAdaptadorCondicoesPagamento);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerCondicoesPagamento.setLayoutManager(gerenciador);

		mButtonNovoCondicaoPagamento.setOnClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		CondicaoPagamento condicaoPagamento = mCondicoesPagamento.get(posicao);
		if (mOnCondicaoPagamentoSelecionadoListener != null) {
			mOnCondicaoPagamentoSelecionadoListener
					.onCondicaoPagamentoSelecionado(view, condicaoPagamento);
		}
	}

	@Override
	public void onItemLongClick(View view, int posicao) {
		CondicaoPagamento condicaoPagamento = mCondicoesPagamento.get(posicao);
		if (mOnLongoCondicaoPagamentoSelecionadoListener != null) {
			mOnLongoCondicaoPagamentoSelecionadoListener
					.onLongoCondicaoPagamentoSelecionado(view, condicaoPagamento);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity()
				.setTitle(R.string.forma_pagamento_cadastro_label_condicoes_pagamento_hint);
		alternarButtonNovoCondicaoPagamento();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CONDICOES_PAGAMENTO, mCondicoesPagamento);
	}

	public void addCondicaoPagamento(@NonNull CondicaoPagamento condicaoPagamento) {
		mCondicoesPagamento.add(condicaoPagamento);
		int indice = mCondicoesPagamento.indexOf(condicaoPagamento);
		mAdaptadorCondicoesPagamento.notifyItemInserted(indice);
	}

	private void alternarButtonNovoCondicaoPagamento() {
		mLabel0Registros.setVisibility(mCondicoesPagamento.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	public void removeCondicaoPagamento(@NonNull CondicaoPagamento condicaoPagamento) {
		if (mCondicoesPagamento.contains(condicaoPagamento)) {
			int indice = mCondicoesPagamento.indexOf(condicaoPagamento);
			mCondicoesPagamento.remove(indice);
			mAdaptadorCondicoesPagamento.notifyItemRemoved(indice);
		}
		alternarButtonNovoCondicaoPagamento();
	}

	public void setCondicoesPagamento(@NonNull ArrayList<CondicaoPagamento> condicoesPagamento) {
		mCondicoesPagamento = condicoesPagamento;
		if (getArguments() != null) {
			getArguments()
					.putSerializable(CONDICOES_PAGAMENTO, mCondicoesPagamento);
		}
	}

	public void setOnCondicaoPagamentoSelecionadoListener(@Nullable OnCondicaoPagamentoSelecionadoListener ouvinte) {
		mOnCondicaoPagamentoSelecionadoListener = ouvinte;
	}

	public void setOnLongoCondicaoPagamentoSelecionadoListener(@Nullable OnLongoCondicaoPagamentoSelecionadoListener ouvinte) {
		mOnLongoCondicaoPagamentoSelecionadoListener = ouvinte;
	}

	public interface OnCondicaoPagamentoSelecionadoListener {

		void onCondicaoPagamentoSelecionado(@NonNull View view,
		                                    @NonNull CondicaoPagamento condicaoPagamento);
	}

	public interface OnLongoCondicaoPagamentoSelecionadoListener {

		void onLongoCondicaoPagamentoSelecionado(@NonNull View view,
		                                         @NonNull CondicaoPagamento condicaoPagamento);
	}
}
