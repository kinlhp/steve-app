package com.kinlhp.steve.atividade.fragmento;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.PessoaCadastroActivity;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerEnderecos;
import com.kinlhp.steve.dominio.Endereco;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PessoaCadastroEnderecosFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerEnderecos.OnItemClickListener,
		AdaptadorRecyclerEnderecos.OnItemLongClickListener, Serializable {
	private static final long serialVersionUID = -968651976676314401L;
	private static final String ENDERECOS = "enderecos";
	private ArrayList<Endereco> mEnderecos;
	private OnEnderecoSelectedListener mEnderecoSelectedListener;

	private AppCompatImageButton mButtonNovoEndereco;
	private RecyclerView mRecyclerEnderecos;

	/**
	 * Construtor padrão é obrigatório
	 */
	public PessoaCadastroEnderecosFragment() {
	}

	public static PessoaCadastroEnderecosFragment newInstance(@NonNull ArrayList<Endereco> enderecos) {
		PessoaCadastroEnderecosFragment fragmento =
				new PessoaCadastroEnderecosFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(ENDERECOS, enderecos);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnEnderecoSelectedListener) {
			mEnderecoSelectedListener = (OnEnderecoSelectedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnEnderecoSelectedListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_endereco:
				((PessoaCadastroActivity) getActivity())
						.onEnderecoSelected(Endereco.builder().tipo(null).build());
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//noinspection unchecked
		mEnderecos = getArguments() != null
				? (ArrayList<Endereco>) getArguments().getSerializable(ENDERECOS)
				: new ArrayList<Endereco>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoa_cadastro_enderecos, container, false);
		mButtonNovoEndereco = view.findViewById(R.id.button_novo_endereco);
		mRecyclerEnderecos = view.findViewById(R.id.recycler_enderecos);

		mRecyclerEnderecos.setHasFixedSize(true);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerEnderecos.setLayoutManager(gerenciador);
		RecyclerView.Adapter adaptador =
				new AdaptadorRecyclerEnderecos(mEnderecos);
		((AdaptadorRecyclerEnderecos) adaptador).addOnItemClickListener(this);
		((AdaptadorRecyclerEnderecos) adaptador)
				.addOnItemLongClickListener(this);
		mRecyclerEnderecos.setAdapter(adaptador);

		mButtonNovoEndereco.setOnClickListener(this);

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mEnderecoSelectedListener = null;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mEnderecoSelectedListener.onEnderecoSelected(mEnderecos.get(posicao));
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Endereco enderecoARemover = mEnderecos.get(posicao);
		if (mEnderecos.contains(enderecoARemover)
				&& enderecoARemover.getId() == null) {
			enderecoARemover.getPessoa().getEnderecos()
					.remove(enderecoARemover);

			// TODO: 9/9/17 corrigir essa gambiarra [problema com equals e hashCode]
			/*
			essa gambiarra foi necessária pois a ação acima não remove o
			Endereco do Set<Endereco>.
			 */
			List<Endereco> enderecosNaoRemovidos = new ArrayList<>();
			for (Endereco endereco : enderecoARemover.getPessoa().getEnderecos()) {
				if (!enderecoARemover.getTipo().equals(endereco.getTipo())) {
					enderecosNaoRemovidos.add(endereco);
				}
			}
			enderecoARemover.getPessoa().getEnderecos().clear();
			enderecoARemover.getPessoa().getEnderecos()
					.addAll(enderecosNaoRemovidos);

			((AdaptadorRecyclerEnderecos) mRecyclerEnderecos.getAdapter())
					.removerItem(enderecoARemover);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_label_enderecos_hint);
		if (!mEnderecos.isEmpty()) {
			for (Endereco endereco : mEnderecos.get(0).getPessoa().getEnderecos()) {
				if (!mEnderecos.contains(endereco)) {
					mEnderecos.add(endereco);
					((AdaptadorRecyclerEnderecos) mRecyclerEnderecos.getAdapter())
							.adicionarItem(endereco);
				}
			}
		}
		mButtonNovoEndereco
				.setVisibility(mEnderecos.size() < Endereco.Tipo.values().length
						? View.VISIBLE : View.GONE);
	}

	public interface OnEnderecoSelectedListener {

		void onEnderecoSelected(Endereco endereco);
	}
}
