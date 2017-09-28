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
import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.util.Moeda;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/21/17.
 */
public class AdaptadorRecyclerItensOrdemServico
		extends RecyclerView.Adapter<AdaptadorRecyclerItensOrdemServico.ViewHolderItensOrdemServico>
		implements Serializable {
	private static final long serialVersionUID = 4132156367202217999L;
	private List<ItemOrdemServico> mItensOrdemServico;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerItensOrdemServico(@NonNull List<ItemOrdemServico> itensOrdemServico) {
		mItensOrdemServico = itensOrdemServico;
	}

	@Override
	public int getItemCount() {
		return mItensOrdemServico.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderItensOrdemServico viewHolder,
	                             int position) {
		ItemOrdemServico itemOrdemServico = mItensOrdemServico.get(position);
		viewHolder.mLabelSituacao
				.setText(itemOrdemServico.getSituacao().getDescricao());
		viewHolder.mLabelServico
				.setText(itemOrdemServico.getServico().toString());
		// TODO: 9/24/17 corrigir hard-coded
		viewHolder.mLabelValorOrcamento
				.setText("Valor do orçamento ".concat(Moeda.comSifra(itemOrdemServico.getValorOrcamento())));
		viewHolder.mLabelValorServico
				.setText("Valor aplicado ".concat(Moeda.comSifra(itemOrdemServico.getValorServico())));
		viewHolder.mLabelDescricao
				.setText(TextUtils.isEmpty(itemOrdemServico.getDescricao())
						? " " : itemOrdemServico.getDescricao());
		viewHolder.mButtonRemover.setVisibility(itemOrdemServico.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderItensOrdemServico onCreateViewHolder(ViewGroup parent,
	                                                      int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_itens_ordem_servico, parent, false);
		return new ViewHolderItensOrdemServico(view);
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

	class ViewHolderItensOrdemServico extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = -9211154205011930123L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelDescricao;
		private AppCompatTextView mLabelServico;
		private AppCompatTextView mLabelSituacao;
		private AppCompatTextView mLabelValorOrcamento;
		private AppCompatTextView mLabelValorServico;

		ViewHolderItensOrdemServico(View itemView) {
			super(itemView);
			mButtonRemover = itemView
					.findViewById(R.id.button_remover_item_ordem_servico);
			mLabelDescricao = itemView.findViewById(R.id.label_descricao);
			mLabelServico = itemView.findViewById(R.id.label_servico);
			mLabelSituacao = itemView.findViewById(R.id.label_situacao);
			mLabelValorOrcamento = itemView
					.findViewById(R.id.label_valor_orcamento);
			mLabelValorServico = itemView
					.findViewById(R.id.label_valor_servico);

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
