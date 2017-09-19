package com.kinlhp.steve.atividade.adaptador;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Credencial;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kin on 9/19/17.
 */
public class AdaptadorRecyclerCredenciais
		extends RecyclerView.Adapter<AdaptadorRecyclerCredenciais.ViewHolderCredenciais>
		implements Serializable {
	private static final long serialVersionUID = 5487687509022538157L;
	private List<Credencial> mCredenciais;
	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public AdaptadorRecyclerCredenciais(@NonNull List<Credencial> credenciais) {
		mCredenciais = credenciais;
	}

	@Override
	public int getItemCount() {
		return mCredenciais.size();
	}

	@Override
	public void onBindViewHolder(ViewHolderCredenciais viewHolder,
	                             int position) {
		Credencial credencial = mCredenciais.get(position);
		viewHolder.mLabelUsuario.setText(credencial.getUsuario());
		viewHolder.mLabelFuncionario
				.setText(credencial.getFuncionario().toString());
		viewHolder.mLabelPerfis.setText(credencial.perfisToString());
		viewHolder.mButtonRemover.setVisibility(credencial.getId() == null
				? View.VISIBLE : View.GONE);
	}

	@Override
	public ViewHolderCredenciais onCreateViewHolder(ViewGroup parent,
	                                                int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_lista_credenciais, parent, false);
		return new ViewHolderCredenciais(view);
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

	class ViewHolderCredenciais extends RecyclerView.ViewHolder
			implements View.OnClickListener, View.OnLongClickListener,
			Serializable {
		private static final long serialVersionUID = -2485368420370163285L;
		private AppCompatImageButton mButtonRemover;
		private AppCompatTextView mLabelFuncionario;
		private AppCompatTextView mLabelPerfis;
		private AppCompatTextView mLabelUsuario;

		ViewHolderCredenciais(View itemView) {
			super(itemView);
			mButtonRemover = itemView
					.findViewById(R.id.button_remover_credencial);
			mLabelFuncionario = itemView.findViewById(R.id.label_funcionario);
			mLabelPerfis = itemView.findViewById(R.id.label_perfis);
			mLabelUsuario = itemView.findViewById(R.id.label_usuario);

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
