package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.util.Data;
import com.kinlhp.steve.util.Moeda;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/5/17.
 */
public class AdaptadorRecyclerContasPagar
		extends RecyclerView.Adapter<AdaptadorRecyclerContasPagar.ViewHolderContasPagar>
		implements Serializable {
	private static final long serialVersionUID = -6290771401825361834L;
	private List<ContaPagar> mContasPagar;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerContasPagar(@NonNull List<ContaPagar> contasPagar) {
		mContasPagar = contasPagar;
	}

	@Override
	public int getItemCount() {
		return mContasPagar.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderContasPagar viewHolder, int position) {
		ContaPagar contaPagar = mContasPagar.get(position);
		viewHolder.mLabelCedente.setText(contaPagar.getCedente() == null ? "" : contaPagar.getCedente().toString());
		viewHolder.mLabelValor.setText(contaPagar.getValor() == null ? "" : "Valor ".concat(Moeda.comSifra(contaPagar.getValor())));
		viewHolder.mLabelNumeroParcela.setText(contaPagar.getNumeroParcela() == null ? "" : "Parcela " + contaPagar.getNumeroParcela().toString());
		viewHolder.mLabelDataVencimento.setText(contaPagar.getDataVencimento() == null ? "" : "Vencimento " + Data.paraStringData(contaPagar.getDataVencimento()));
		viewHolder.mLabelMontantePago.setText(contaPagar.getMontantePago() == null ? "" : "Montante pago ".concat(Moeda.comSifra(contaPagar.getMontantePago())));
		viewHolder.mLabelSaldoDevedor.setText(contaPagar.getSaldoDevedor() == null ? "" : "Saldo devedor ".concat(Moeda.comSifra(contaPagar.getSaldoDevedor())));
		viewHolder.mButtonRemover.setVisibility(contaPagar.getId() == null ? View.GONE : View.GONE);
	}

	@Override
	public ViewHolderContasPagar onCreateViewHolder(ViewGroup parent,
	                                                int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_contas_pagar, parent, false);
		return new ViewHolderContasPagar(view);
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

	class ViewHolderContasPagar extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = 1437983559541438529L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelCedente;
		private AppCompatTextView mLabelDataVencimento;
		private AppCompatTextView mLabelMontantePago;
		private AppCompatTextView mLabelNumeroParcela;
		private AppCompatTextView mLabelSaldoDevedor;
		private AppCompatTextView mLabelValor;

		ViewHolderContasPagar(View itemView) {
			super(itemView);
			mButtonRemover = itemView.findViewById(R.id.button_remover_conta_pagar);
			mLabelCedente = itemView.findViewById(R.id.label_cedente);
			mLabelDataVencimento = itemView.findViewById(R.id.label_data_vencimento);
			mLabelMontantePago = itemView.findViewById(R.id.label_montante_pago);
			mLabelNumeroParcela = itemView.findViewById(R.id.label_numero_parcela);
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
