package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.CondicaoPagamento;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/25/17.
 */
public class AdaptadorRecyclerCondicoesPagamento
		extends RecyclerView.Adapter<AdaptadorRecyclerCondicoesPagamento.ViewHolderCondicoesPagamento>
		implements Serializable {
	private static final long serialVersionUID = -1056584825266490592L;
	private List<CondicaoPagamento> mCondicoesPagamento;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerCondicoesPagamento(@NonNull List<CondicaoPagamento> condicoesPagamento) {
		mCondicoesPagamento = condicoesPagamento;
	}

	@Override
	public int getItemCount() {
		return mCondicoesPagamento.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderCondicoesPagamento viewHolder,
	                             int position) {
		CondicaoPagamento condicaoPagamento = mCondicoesPagamento.get(position);
		viewHolder.mLabelQuantidadeParcelas
				.setText(condicaoPagamento.getQuantidadeParcelas().toString() + "x");
		viewHolder.mLabelDescricao.setText(condicaoPagamento.getDescricao());
		viewHolder.mLabelPeriodoEntreParcelas
				.setText("A cada " + condicaoPagamento.getPeriodoEntreParcelas() + " dia(s)");
		viewHolder.mButtonRemover
				.setVisibility(condicaoPagamento.getId() == null
						? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderCondicoesPagamento onCreateViewHolder(ViewGroup parent,
	                                                       int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_condicoes_pagamento, parent, false);
		return new ViewHolderCondicoesPagamento(view);
	}

	public void setOnItemClickListener(@Nullable OnItemClickListener ouvinte) {
		mOnItemClickListener = ouvinte;
	}

	public void setOnItemLongClickListener(@Nullable OnItemLongClickListener ouvinte) {
		mOnItemLongClickListener = ouvinte;
	}

	public interface OnItemClickListener {

		void onItemClick(View view, int posicao);
	}

	public interface OnItemLongClickListener {

		void onItemLongClick(View view, int posicao);
	}

	class ViewHolderCondicoesPagamento extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 542116939422801788L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelDescricao;
		private AppCompatTextView mLabelPeriodoEntreParcelas;
		private AppCompatTextView mLabelQuantidadeParcelas;

		ViewHolderCondicoesPagamento(View itemView) {
			super(itemView);
			mButtonRemover = itemView
					.findViewById(R.id.button_remover_condicao_pagamento);
			mLabelDescricao = itemView.findViewById(R.id.label_descricao);
			mLabelPeriodoEntreParcelas = itemView
					.findViewById(R.id.label_periodo_entre_parcelas);
			mLabelQuantidadeParcelas = itemView
					.findViewById(R.id.label_quantidade_parcelas);

			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(view, getAdapterPosition());
			}
		}

		@Override
		public boolean onLongClick(View view) {
			if (mOnItemLongClickListener != null) {
				mOnItemLongClickListener
						.onItemLongClick(view, getAdapterPosition());
			}
			return true;
		}
	}
}
