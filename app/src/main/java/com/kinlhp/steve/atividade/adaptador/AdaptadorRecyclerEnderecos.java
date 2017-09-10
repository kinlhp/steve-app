package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
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
import java.util.Locale;

/**
 * Created by luis on 9/5/17.
 */
public class AdaptadorRecyclerEnderecos extends RecyclerView.Adapter
		implements Serializable {
	private static final long serialVersionUID = -815188722971721467L;
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
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		ViewHolderEnderecos viewHolder = (ViewHolderEnderecos) holder;
		Endereco endereco = mEnderecos.get(position);
		viewHolder.mLabelTipo.setText(endereco.getTipo().getDescricao());
		String logradouro = "%1s, %2s - %3s/%4s";
		viewHolder.mLabelLogradouro
				.setText(String.format(Locale.getDefault(), logradouro, endereco.getLogradouro(), endereco.getNumero(), endereco.getCidade(), endereco.getUf().getSigla()));
		viewHolder.mLabelNomeContato
				.setText(!TextUtils.isEmpty(endereco.getNomeContato())
						? endereco.getNomeContato() : " ");
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                  int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_enderecos, parent, false);
		return new ViewHolderEnderecos(view);
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

	public void adicionarItem(@NonNull Endereco endereco) {
		if (!mEnderecos.contains(endereco)) {
			mEnderecos.add(endereco);
			notifyItemInserted(getItemCount());
		}
	}

	public void removerItem(@NonNull Endereco endereco) {
		if (mEnderecos.contains(endereco)) {
			int posicao = mEnderecos.indexOf(endereco);
			mEnderecos.remove(endereco);
			notifyItemRemoved(posicao);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int posicao);
	}

	public interface OnItemLongClickListener {
		void onItemLongClickListener(View view, int posicao);
	}

	private class ViewHolderEnderecos extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener {
		private AppCompatTextView mLabelLogradouro;
		private AppCompatTextView mLabelNomeContato;
		private AppCompatTextView mLabelTipo;

		ViewHolderEnderecos(View itemView) {
			super(itemView);
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
