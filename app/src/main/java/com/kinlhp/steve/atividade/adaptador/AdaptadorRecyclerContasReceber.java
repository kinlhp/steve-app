package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.util.Data;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by kin on 9/5/17.
 */
public class AdaptadorRecyclerContasReceber
		extends RecyclerView.Adapter<AdaptadorRecyclerContasReceber.ViewHolderContasReceber>
		implements Serializable {
	private static final long serialVersionUID = 4663410846505346534L;
	private List<ContaReceber> mContasReceber;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerContasReceber(@NonNull List<ContaReceber> contasReceber) {
		mContasReceber = contasReceber;
	}

	@Override
	public int getItemCount() {
		return mContasReceber.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderContasReceber viewHolder, int position) {
		ContaReceber contaReceber = mContasReceber.get(position);
		viewHolder.mLabelSacado.setText(contaReceber.getSacado() == null ? "" : contaReceber.getSacado().toString());
		viewHolder.mLabelValor.setText(contaReceber.getValor() == null ? "" : contaReceber.getValor().setScale(2, RoundingMode.HALF_EVEN).toString());
		viewHolder.mLabelFormaPagamento.setText(contaReceber.getCondicaoPagamento() != null && contaReceber.getCondicaoPagamento().getFormaPagamento() != null ? contaReceber.getCondicaoPagamento().getFormaPagamento().toString() : "");
		viewHolder.mLabelCondicaoPagamento.setText(contaReceber.getCondicaoPagamento() != null ? contaReceber.getCondicaoPagamento().toString() : "");
		viewHolder.mLabelNumeroParcela.setText(contaReceber.getNumeroParcela() == null ? "" : contaReceber.getNumeroParcela().toString());
		viewHolder.mLabelDataVencimento.setText(contaReceber.getDataVencimento() == null ? "" : Data.paraStringData(contaReceber.getDataVencimento()));
		viewHolder.mLabelMontantePago.setText(contaReceber.getMontantePago() == null ? "" : contaReceber.getMontantePago().setScale(2, RoundingMode.HALF_EVEN).toString());
		viewHolder.mLabelSaldoDevedor.setText(contaReceber.getSaldoDevedor() == null ? "" : contaReceber.getSaldoDevedor().setScale(2, RoundingMode.HALF_EVEN).toString());
		viewHolder.mButtonRemover.setVisibility(contaReceber.getId() == null ? View.GONE : View.GONE);
	}

	@Override
	public ViewHolderContasReceber onCreateViewHolder(ViewGroup parent,
	                                                  int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_contas_receber, parent, false);
		return new ViewHolderContasReceber(view);
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

	class ViewHolderContasReceber extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 4335851568331325066L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelCondicaoPagamento;
		private AppCompatTextView mLabelDataVencimento;
		private AppCompatTextView mLabelFormaPagamento;
		private AppCompatTextView mLabelMontantePago;
		private AppCompatTextView mLabelNumeroParcela;
		private AppCompatTextView mLabelSacado;
		private AppCompatTextView mLabelSaldoDevedor;
		private AppCompatTextView mLabelValor;

		ViewHolderContasReceber(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_conta_receber);
			mLabelCondicaoPagamento = itemView.findViewById(R.id.label_condicao_pagamento);
			mLabelDataVencimento = itemView.findViewById(R.id.label_data_vencimento);
			mLabelFormaPagamento = itemView.findViewById(R.id.label_forma_pagamento);
			mLabelMontantePago = itemView.findViewById(R.id.label_montante_pago);
			mLabelNumeroParcela = itemView.findViewById(R.id.label_numero_parcela);
			mLabelSacado = itemView.findViewById(R.id.label_sacado);
			mLabelSaldoDevedor = itemView.findViewById(R.id.label_saldo_devedor);
			mLabelValor = itemView.findViewById(R.id.label_valor);

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
