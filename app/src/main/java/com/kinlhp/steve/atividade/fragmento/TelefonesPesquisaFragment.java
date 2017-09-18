package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerTelefones;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.ArrayList;

public class TelefonesPesquisaFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerTelefones.OnItemClickListener,
		AdaptadorRecyclerTelefones.OnItemLongClickListener, Serializable {
	private static final long serialVersionUID = -3375380125366495441L;
	private static final String TELEFONES = "telefones";
	private AdaptadorRecyclerTelefones mAdaptadorTelefones;
	private ArrayList<Telefone> mTelefones;
	private OnTelefoneSelecionadoListener mOnTelefoneSelecionadoListener;
	private OnLongoTelefoneSelecionadoListener mOnLongoTelefoneSelecionadoListener;

	private AppCompatImageButton mButtonNovoTelefone;
	private AppCompatTextView mLabel0Registros;
	private RecyclerView mRecyclerTelefones;

	/**
	 * Construtor padrão é obrigatório
	 */
	public TelefonesPesquisaFragment() {
	}

	public static TelefonesPesquisaFragment newInstance(@NonNull ArrayList<Telefone> telefones) {
		TelefonesPesquisaFragment fragmento = new TelefonesPesquisaFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(TELEFONES, telefones);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_telefone:
				Telefone telefone = Telefone.builder().tipo(null).build();
				if (mOnTelefoneSelecionadoListener != null) {
					mOnTelefoneSelecionadoListener
							.onTelefoneSelecionado(view, telefone);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//noinspection unchecked
			mTelefones = (ArrayList<Telefone>) savedInstanceState
					.getSerializable(TELEFONES);
		} else if (getArguments() != null) {
			//noinspection unchecked
			mTelefones = (ArrayList<Telefone>) getArguments()
					.getSerializable(TELEFONES);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_telefones_pesquisa, container, false);
		mButtonNovoTelefone = view.findViewById(R.id.button_novo_telefone);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mRecyclerTelefones = view.findViewById(R.id.recycler_telefones);

		mRecyclerTelefones.setHasFixedSize(true);
		mAdaptadorTelefones = new AdaptadorRecyclerTelefones(mTelefones);
		mAdaptadorTelefones.setOnItemClickListener(this);
		mAdaptadorTelefones.setOnItemLongClickListener(this);
		mRecyclerTelefones.setAdapter(mAdaptadorTelefones);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerTelefones.setLayoutManager(gerenciador);

		mButtonNovoTelefone.setOnClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		Telefone telefone = mTelefones.get(posicao);
		if (mOnTelefoneSelecionadoListener != null) {
			mOnTelefoneSelecionadoListener
					.onTelefoneSelecionado(view, telefone);
		}
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Telefone telefone = mTelefones.get(posicao);
		if (mOnLongoTelefoneSelecionadoListener != null) {
			mOnLongoTelefoneSelecionadoListener
					.onLongoTelefoneSelecionado(view, telefone);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_label_telefones_hint);
		alternarButtonNovoTelefone();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(TELEFONES, mTelefones);
	}

	public void addTelefone(@NonNull Telefone telefone) {
		mTelefones.add(telefone);
		int indice = mTelefones.indexOf(telefone);
		mAdaptadorTelefones.notifyItemInserted(indice);
	}

	private void alternarButtonNovoTelefone() {
		mButtonNovoTelefone
				.setVisibility(mTelefones.size() < Telefone.Tipo.values().length
						? View.VISIBLE : View.GONE);
		mLabel0Registros.setVisibility(mTelefones.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	public void removeTelefone(@NonNull Telefone telefone) {
		if (mTelefones.contains(telefone)) {
			int indice = mTelefones.indexOf(telefone);
			mTelefones.remove(indice);
			mAdaptadorTelefones.notifyItemRemoved(indice);
		}
		alternarButtonNovoTelefone();
	}

	public void setTelefones(@NonNull ArrayList<Telefone> telefones) {
		mTelefones = telefones;
		if (getArguments() != null) {
			getArguments().putSerializable(TELEFONES, mTelefones);
		}
	}

	public void setOnLongoTelefoneSelecionadoListener(@Nullable OnLongoTelefoneSelecionadoListener ouvinte) {
		mOnLongoTelefoneSelecionadoListener = ouvinte;
	}

	public void setOnTelefoneSelecionadoListener(@Nullable OnTelefoneSelecionadoListener ouvinte) {
		mOnTelefoneSelecionadoListener = ouvinte;
	}

	public interface OnLongoTelefoneSelecionadoListener {

		void onLongoTelefoneSelecionado(@NonNull View view,
		                                @NonNull Telefone telefone);
	}

	public interface OnTelefoneSelecionadoListener {

		void onTelefoneSelecionado(@NonNull View view,
		                           @NonNull Telefone telefone);
	}
}
