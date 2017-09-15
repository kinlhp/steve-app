package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.util.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luis on 9/5/17.
 */
public class AdaptadorRecyclerPessoas
		extends RecyclerView.Adapter<AdaptadorRecyclerPessoas.ViewHolderPessoas>
		implements Serializable {
	private static final long serialVersionUID = -8253634021536597822L;
	private List<Pessoa> mPessoas;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerPessoas(@NonNull List<Pessoa> pessoas) {
		mPessoas = pessoas;
	}

	@Override
	public int getItemCount() {
		return mPessoas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderPessoas viewHolder, int position) {
		Pessoa pessoa = mPessoas.get(position);
		viewHolder.mLabelCnpjCpf.setText(pessoa.getCnpjCpf());
		viewHolder.mLabelNomeRazao.setText(pessoa.toString());
		viewHolder.mLabelAberturaNascimento
				.setText(pessoa.getAberturaNascimento() != null
						? Data.paraStringData(pessoa.getAberturaNascimento())
						: "");
		viewHolder.mButtonRemover.setVisibility(pessoa.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderPessoas onCreateViewHolder(ViewGroup parent,
	                                            int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_pessoas, parent, false);
		return new ViewHolderPessoas(view);
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

	class ViewHolderPessoas extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 766015882614344696L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelAberturaNascimento;
		private AppCompatTextView mLabelCnpjCpf;
		private AppCompatTextView mLabelNomeRazao;

		ViewHolderPessoas(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_pessoa);
			mLabelAberturaNascimento = itemView
					.findViewById(R.id.label_abertura_nascimento);
			mLabelCnpjCpf = itemView.findViewById(R.id.label_cnpj_cpf);
			mLabelNomeRazao = itemView.findViewById(R.id.label_nome_razao);

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
