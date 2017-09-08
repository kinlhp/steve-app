package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Email;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luis on 9/5/17.
 */
public class AdaptadorRecyclerEmails extends RecyclerView.Adapter
		implements Serializable {
	private static final long serialVersionUID = -3388936833199806112L;
	private List<Email> mEmails;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerEmails(@NonNull List<Email> emails) {
		mEmails = emails;
	}

	@Override
	public int getItemCount() {
		return mEmails.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		ViewHolderEmails viewHolder = (ViewHolderEmails) holder;
		Email email = mEmails.get(position);
		viewHolder.mLabelTitulo.setText(email.getTipo().getDescricao());
		viewHolder.mLabelConteudo.setText(email.getEnderecoEletronico());
		viewHolder.mLabelDescricao
				.setText(!TextUtils.isEmpty(email.getNomeContato())
						? email.getNomeContato() : " ");
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                  int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista, parent, false);
		return new ViewHolderEmails(view);
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

	public void adicionarItem(@NonNull Email email) {
		if (!mEmails.contains(email)) {
			mEmails.add(email);
			notifyItemInserted(getItemCount());
		}
	}

	public void removerItem(@NonNull Email email) {
		if (mEmails.contains(email)) {
			int posicao = mEmails.indexOf(email);
			mEmails.remove(email);
			notifyItemRemoved(posicao);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int posicao);
	}

	public interface OnItemLongClickListener {
		void onItemLongClickListener(View view, int posicao);
	}

	private class ViewHolderEmails extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener {
		private AppCompatTextView mLabelConteudo;
		private AppCompatTextView mLabelDescricao;
		private AppCompatTextView mLabelTitulo;

		ViewHolderEmails(View itemView) {
			super(itemView);
			mLabelConteudo = itemView.findViewById(R.id.label_conteudo);
			mLabelDescricao = itemView.findViewById(R.id.label_descricao);
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
