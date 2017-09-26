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
import com.kinlhp.steve.dominio.Email;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/5/17.
 */
public class AdaptadorRecyclerEmails
		extends RecyclerView.Adapter<AdaptadorRecyclerEmails.ViewHolderEmails>
		implements Serializable {
	private static final long serialVersionUID = -3662047948661698133L;
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
	public void onBindViewHolder(ViewHolderEmails viewHolder, int position) {
		Email email = mEmails.get(position);
		viewHolder.mLabelTipo.setText(email.getTipo().getDescricao());
		viewHolder.mLabelEnderecoEletronico
				.setText(email.getEnderecoEletronico());
		viewHolder.mLabelNomeContato
				.setText(!TextUtils.isEmpty(email.getNomeContato())
						? email.getNomeContato() : "");
		viewHolder.mButtonRemover.setVisibility(email.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderEmails onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_emails, parent, false);
		return new ViewHolderEmails(view);
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

	class ViewHolderEmails extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 7821525572226704938L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelEnderecoEletronico;
		private AppCompatTextView mLabelNomeContato;
		private AppCompatTextView mLabelTipo;

		ViewHolderEmails(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_email);
			mLabelEnderecoEletronico = itemView
					.findViewById(R.id.label_endereco_eletronico);
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
						.onItemLongClick(view, getAdapterPosition());
			}
			return true;
		}
	}
}
