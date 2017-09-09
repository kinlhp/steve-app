package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luis on 9/5/17.
 */
public class AdaptadorRecyclerTelefones extends RecyclerView.Adapter
		implements Serializable {
	private static final long serialVersionUID = 7679612353238026049L;
	private List<Telefone> mTelefones;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerTelefones(@NonNull List<Telefone> telefones) {
		mTelefones = telefones;
	}

	@Override
	public int getItemCount() {
		return mTelefones.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		ViewHolderTelefones viewHolder = (ViewHolderTelefones) holder;
		Telefone telefone = mTelefones.get(position);
		viewHolder.mLabelTipo.setText(telefone.getTipo().getDescricao());
		viewHolder.mLabelNumero.setText(telefone.getNumero());
		viewHolder.mLabelNomeContato
				.setText(!TextUtils.isEmpty(telefone.getNomeContato())
						? telefone.getNomeContato() : " ");
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                  int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_telefones, parent, false);
		return new ViewHolderTelefones(view);
	}

	/**
	 * Delega um {@link OnItemClickListener} para lidar com o clique no item
	 *
	 * @param ouvinte
	 */
	public void addOnItemClickListener(OnItemClickListener ouvinte) {
		mOnItemClickListener = ouvinte;
	}

	/**
	 * Delega um {@link OnItemLongClickListener} para lidar com o clique longo
	 * no item
	 *
	 * @param ouvinte
	 */
	public void addOnItemLongClickListener(OnItemLongClickListener ouvinte) {
		mOnItemLongClickListener = ouvinte;
	}

	public void adicionarItem(@NonNull Telefone telefone) {
		if (!mTelefones.contains(telefone)) {
			mTelefones.add(telefone);
			notifyItemInserted(getItemCount());
		}
	}

	public void removerItem(@NonNull Telefone telefone) {
		if (mTelefones.contains(telefone)) {
			int posicao = mTelefones.indexOf(telefone);
			mTelefones.remove(telefone);
			notifyItemRemoved(posicao);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int posicao);
	}

	public interface OnItemLongClickListener {
		void onItemLongClickListener(View view, int posicao);
	}

	private class ViewHolderTelefones extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener {
		private AppCompatTextView mLabelNomeContato;
		private AppCompatTextView mLabelNumero;
		private AppCompatTextView mLabelTipo;

		ViewHolderTelefones(View itemView) {
			super(itemView);
			mLabelNomeContato = itemView.findViewById(R.id.label_nome_contato);
			mLabelNumero = itemView.findViewById(R.id.label_numero);
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
