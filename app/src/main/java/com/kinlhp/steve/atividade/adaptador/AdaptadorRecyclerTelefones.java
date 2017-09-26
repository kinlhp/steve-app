package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
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
 * Created by kin on 9/5/17.
 */
public class AdaptadorRecyclerTelefones
		extends RecyclerView.Adapter<AdaptadorRecyclerTelefones.ViewHolderTelefones>
		implements Serializable {
	private static final long serialVersionUID = -8689088205451865016L;
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
	public void onBindViewHolder(ViewHolderTelefones viewHolder, int position) {
		Telefone telefone = mTelefones.get(position);
		viewHolder.mLabelTipo.setText(telefone.getTipo().getDescricao());
		viewHolder.mLabelNumero.setText(telefone.getNumero());
		viewHolder.mLabelNomeContato
				.setText(!TextUtils.isEmpty(telefone.getNomeContato())
						? telefone.getNomeContato() : "");
		viewHolder.mButtonRemover.setVisibility(telefone.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderTelefones onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_telefones, parent, false);
		return new ViewHolderTelefones(view);
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
		void onItemLongClickListener(View view, int posicao);
	}

	class ViewHolderTelefones extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = -9017008007191733771L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelNomeContato;
		private AppCompatTextView mLabelNumero;
		private AppCompatTextView mLabelTipo;

		ViewHolderTelefones(View itemView) {
			super(itemView);
			mButtonRemover = itemView
					.findViewById(R.id.button_remover_telefone);
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
