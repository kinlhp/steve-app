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
import com.kinlhp.steve.dominio.Endereco;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/5/17.
 */
public class AdaptadorRecyclerEnderecos
		extends RecyclerView.Adapter<AdaptadorRecyclerEnderecos.ViewHolderEnderecos>
		implements Serializable {
	private static final long serialVersionUID = 1075524588439191761L;
	private List<Endereco> mEnderecos;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerEnderecos(@NonNull List<Endereco> enderecos) {
		mEnderecos = enderecos;
	}

	@Override
	public int getItemCount() {
		return mEnderecos.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderEnderecos viewHolder, int position) {
		Endereco endereco = mEnderecos.get(position);
		viewHolder.mLabelTipo.setText(endereco.getTipo().getDescricao());
		viewHolder.mLabelLogradouro.setText(endereco.toString());
		viewHolder.mLabelNomeContato
				.setText(!TextUtils.isEmpty(endereco.getNomeContato())
						? endereco.getNomeContato() : "");
		viewHolder.mButtonRemover.setVisibility(endereco.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderEnderecos onCreateViewHolder(ViewGroup parent,
	                                              int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_enderecos, parent, false);
		return new ViewHolderEnderecos(view);
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

	class ViewHolderEnderecos extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 6503784243903583380L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelLogradouro;
		private AppCompatTextView mLabelNomeContato;
		private AppCompatTextView mLabelTipo;

		ViewHolderEnderecos(View itemView) {
			super(itemView);
			mButtonRemover = itemView
					.findViewById(R.id.button_remover_endereco);
			mLabelLogradouro = itemView.findViewById(R.id.label_numero);
			mLabelNomeContato = itemView.findViewById(R.id.label_nome_contato);
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
