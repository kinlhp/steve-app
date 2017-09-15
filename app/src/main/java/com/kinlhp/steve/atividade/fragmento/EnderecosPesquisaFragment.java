package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerEnderecos;
import com.kinlhp.steve.dominio.Endereco;

import java.io.Serializable;
import java.util.ArrayList;

public class EnderecosPesquisaFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerEnderecos.OnItemClickListener,
		AdaptadorRecyclerEnderecos.OnItemLongClickListener, Serializable {
	private static final long serialVersionUID = 7493108215483289366L;
	private static final String ENDERECOS = "enderecos";
	private AdaptadorRecyclerEnderecos mAdaptadorEnderecos;
	private ArrayList<Endereco> mEnderecos;
	private OnEnderecoSelecionadoListener mOnEnderecoSelecionadoListener;
	private OnLongoEnderecoSelecionadoListener mOnLongoEnderecoSelecionadoListener;

	private AppCompatImageButton mButtonNovoEndereco;
	private RecyclerView mRecyclerEnderecos;

	/**
	 * Construtor padrão é obrigatório
	 */
	public EnderecosPesquisaFragment() {
	}

	public static EnderecosPesquisaFragment newInstance(@NonNull ArrayList<Endereco> enderecos) {
		EnderecosPesquisaFragment fragmento = new EnderecosPesquisaFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(ENDERECOS, enderecos);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_endereco:
				Endereco endereco = Endereco.builder().tipo(null).build();
				if (mOnEnderecoSelecionadoListener != null) {
					mOnEnderecoSelecionadoListener
							.onEnderecoSelecionado(view, endereco);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//noinspection unchecked
			mEnderecos = (ArrayList<Endereco>) savedInstanceState
					.getSerializable(ENDERECOS);
		} else if (getArguments() != null) {
			//noinspection unchecked
			mEnderecos = (ArrayList<Endereco>) getArguments()
					.getSerializable(ENDERECOS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_enderecos_pesquisa, container, false);
		mButtonNovoEndereco = view.findViewById(R.id.button_novo_endereco);
		mRecyclerEnderecos = view.findViewById(R.id.recycler_enderecos);

		mRecyclerEnderecos.setHasFixedSize(true);
		mAdaptadorEnderecos = new AdaptadorRecyclerEnderecos(mEnderecos);
		mAdaptadorEnderecos.setOnItemClickListener(this);
		mAdaptadorEnderecos.setOnItemLongClickListener(this);
		mRecyclerEnderecos.setAdapter(mAdaptadorEnderecos);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerEnderecos.setLayoutManager(gerenciador);

		mButtonNovoEndereco.setOnClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		Endereco endereco = mEnderecos.get(posicao);
		if (mOnEnderecoSelecionadoListener != null) {
			mOnEnderecoSelecionadoListener
					.onEnderecoSelecionado(view, endereco);
		}
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Endereco endereco = mEnderecos.get(posicao);
		if (mOnLongoEnderecoSelecionadoListener != null) {
			mOnLongoEnderecoSelecionadoListener
					.onLongoEnderecoSelecionado(view, endereco);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_label_enderecos_hint);
		alternarButtonNovoEndereco();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ENDERECOS, mEnderecos);
	}

	public void addEndereco(@NonNull Endereco endereco) {
		mEnderecos.add(endereco);
		int indice = mEnderecos.indexOf(endereco);
		mAdaptadorEnderecos.notifyItemInserted(indice);
	}

	private void alternarButtonNovoEndereco() {
		mButtonNovoEndereco
				.setVisibility(mEnderecos.size() < Endereco.Tipo.values().length
						? View.VISIBLE : View.GONE);
	}

	public void removeEndereco(@NonNull Endereco endereco) {
		if (mEnderecos.contains(endereco)) {
			int indice = mEnderecos.indexOf(endereco);
			mEnderecos.remove(indice);
			mAdaptadorEnderecos.notifyItemRemoved(indice);
		}
		alternarButtonNovoEndereco();
	}

	public void setEnderecos(@NonNull ArrayList<Endereco> enderecos) {
		mEnderecos = enderecos;
		if (getArguments() != null) {
			getArguments().putSerializable(ENDERECOS, mEnderecos);
		}
	}

	public void setOnEnderecoSelecionadoListener(@Nullable OnEnderecoSelecionadoListener ouvinte) {
		mOnEnderecoSelecionadoListener = ouvinte;
	}

	public void setOnLongoEnderecoSelecionadoListener(@Nullable OnLongoEnderecoSelecionadoListener ouvinte) {
		mOnLongoEnderecoSelecionadoListener = ouvinte;
	}

	public interface OnEnderecoSelecionadoListener {

		void onEnderecoSelecionado(@NonNull View view, @NonNull Endereco endereco);
	}

	public interface OnLongoEnderecoSelecionadoListener {

		void onLongoEnderecoSelecionado(@NonNull View view,
		                                @NonNull Endereco endereco);
	}
}
