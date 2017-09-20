package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.FormaPagamento;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/19/17.
 */
public class AdaptadorRecyclerFormasPagamento
		extends RecyclerView.Adapter<AdaptadorRecyclerFormasPagamento.ViewHolderFormasPagamento>
		implements Serializable {
	private static final long serialVersionUID = 2805748379878370700L;
	private List<FormaPagamento> mFormasPagamento;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerFormasPagamento(@NonNull List<FormaPagamento> formasPagamento) {
		mFormasPagamento = formasPagamento;
	}

	@Override
	public int getItemCount() {
		return mFormasPagamento.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderFormasPagamento viewHolder,
	                             int position) {
		FormaPagamento formaPagamento = mFormasPagamento.get(position);
		// TODO: 9/19/17 corrigir hard-coded
		viewHolder.mLabelTitulo.setText(" ");
		viewHolder.mLabelDescricao.setText(formaPagamento.getDescricao());
		viewHolder.mLabelObservacao.setText(" ");
		viewHolder.mButtonRemover.setVisibility(formaPagamento.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderFormasPagamento onCreateViewHolder(ViewGroup parent,
	                                                    int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_formas_pagamento, parent, false);
		return new ViewHolderFormasPagamento(view);
	}

	public void setOnItemClickListener(OnItemClickListener ouvinte) {
		mOnItemClickListener = ouvinte;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener ouvinte) {
		mOnItemLongClickListener = ouvinte;
	}

	public interface OnItemClickListener {

		void onItemClick(View view, int posicao);
	}

	public interface OnItemLongClickListener {

		void onItemLongClickListener(View view, int posicao);
	}

	class ViewHolderFormasPagamento extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 553448687113841817L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelDescricao;
		private AppCompatTextView mLabelObservacao;
		private AppCompatTextView mLabelTitulo;

		ViewHolderFormasPagamento(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_forma_pagamento);
			mLabelDescricao = itemView.findViewById(R.id.label_descricao);
			mLabelObservacao = itemView.findViewById(R.id.label_observacao);
			mLabelTitulo = itemView.findViewById(R.id.label_titulo);

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
						.onItemLongClickListener(view, getAdapterPosition());
			}
			return true;
		}
	}
}
