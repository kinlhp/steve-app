package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Ordem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/20/17.
 */
public class AdaptadorRecyclerOrdens
		extends RecyclerView.Adapter<AdaptadorRecyclerOrdens.ViewHolderOrdens>
		implements Serializable {
	private static final long serialVersionUID = 3910363705500683552L;
	private List<Ordem> mOrdens;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerOrdens(@NonNull List<Ordem> ordens) {
		mOrdens = ordens;
	}

	@Override
	public int getItemCount() {
		return mOrdens.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderOrdens viewHolder, int position) {
		Ordem ordem = mOrdens.get(position);
		viewHolder.mLabelId.setText(ordem.getId().toString());
		viewHolder.mLabelCliente.setText(ordem.getCliente().toString());
		viewHolder.mLabelTipo.setText(ordem.getTipo().getDescricao());
		viewHolder.mButtonRemover.setVisibility(ordem.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderOrdens onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_ordens, parent, false);
		return new ViewHolderOrdens(view);
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

	class ViewHolderOrdens extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 8680829839914527282L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelCliente;
		private AppCompatTextView mLabelId;
		private AppCompatTextView mLabelTipo;

		ViewHolderOrdens(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_ordem);
			mLabelCliente = itemView.findViewById(R.id.label_cliente);
			mLabelId = itemView.findViewById(R.id.label_id);
			mLabelTipo = itemView.findViewById(R.id.label_tipo);

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
