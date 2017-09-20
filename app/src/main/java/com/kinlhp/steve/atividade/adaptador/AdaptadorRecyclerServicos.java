package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Servico;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/20/17.
 */
public class AdaptadorRecyclerServicos
		extends RecyclerView.Adapter<AdaptadorRecyclerServicos.ViewHolderServicos>
		implements Serializable {
	private static final long serialVersionUID = -1146159059277979071L;
	private List<Servico> mServicos;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerServicos(@NonNull List<Servico> servicos) {
		mServicos = servicos;
	}

	@Override
	public int getItemCount() {
		return mServicos.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderServicos viewHolder,
	                             int position) {
		Servico servico = mServicos.get(position);
		// TODO: 9/20/17 corrigir hard-coded
		viewHolder.mLabelTitulo.setText(" ");
		viewHolder.mLabelDescricao.setText(servico.getDescricao());
		// TODO: 9/20/17 corrigir hard-coded
		viewHolder.mLabelObservacao.setText(" ");
		viewHolder.mButtonRemover.setVisibility(servico.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderServicos onCreateViewHolder(ViewGroup parent,
	                                             int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_servicos, parent, false);
		return new ViewHolderServicos(view);
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

	class ViewHolderServicos extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 5996494775297853838L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelDescricao;
		private AppCompatTextView mLabelObservacao;
		private AppCompatTextView mLabelTitulo;

		ViewHolderServicos(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_servico);
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
