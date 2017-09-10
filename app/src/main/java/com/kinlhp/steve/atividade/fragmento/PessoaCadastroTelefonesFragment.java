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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerTelefones;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PessoaCadastroTelefonesFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerTelefones.OnItemClickListener,
		AdaptadorRecyclerTelefones.OnItemLongClickListener, Serializable {
	private static final long serialVersionUID = 6943800981024946138L;
	private static final String TELEFONES = "telefones";
	private ArrayList<Telefone> mTelefones;
	private OnTelefoneSelectedListener mTelefoneSelectedListener;

	private AppCompatImageButton mButtonNovoTelefone;
	private RecyclerView mRecyclerTelefones;

	/**
	 * Construtor padrão é obrigatório
	 */
	public PessoaCadastroTelefonesFragment() {
	}

	public static PessoaCadastroTelefonesFragment newInstance(@NonNull ArrayList<Telefone> telefones) {
		PessoaCadastroTelefonesFragment fragmento =
				new PessoaCadastroTelefonesFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(TELEFONES, telefones);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnTelefoneSelectedListener) {
			mTelefoneSelectedListener = (OnTelefoneSelectedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnTelefoneSelectedListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_telefone:
				((PessoaCadastroActivity) getActivity())
						.onTelefoneSelected(Telefone.builder().tipo(null).build());
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//noinspection unchecked
		mTelefones = getArguments() != null
				? (ArrayList<Telefone>) getArguments().getSerializable(TELEFONES)
				: new ArrayList<Telefone>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoa_cadastro_telefones, container, false);
		mButtonNovoTelefone = view.findViewById(R.id.button_novo_telefone);
		mRecyclerTelefones = view.findViewById(R.id.recycler_telefones);

		mRecyclerTelefones.setHasFixedSize(true);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerTelefones.setLayoutManager(gerenciador);
		RecyclerView.Adapter adaptador =
				new AdaptadorRecyclerTelefones(mTelefones);
		((AdaptadorRecyclerTelefones) adaptador).addOnItemClickListener(this);
		((AdaptadorRecyclerTelefones) adaptador)
				.addOnItemLongClickListener(this);
		mRecyclerTelefones.setAdapter(adaptador);

		mButtonNovoTelefone.setOnClickListener(this);

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mTelefoneSelectedListener = null;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mTelefoneSelectedListener.onTelefoneSelected(mTelefones.get(posicao));
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Telefone telefoneARemover = mTelefones.get(posicao);
		if (mTelefones.contains(telefoneARemover)
				&& telefoneARemover.getId() == null) {
			telefoneARemover.getPessoa().getTelefones()
					.remove(telefoneARemover);

			// TODO: 9/9/17 corrigir essa gambiarra [problema com equals e hashCode]
			/*
			essa gambiarra foi necessária pois a ação acima não remove o
			Telefone do Set<Telefone>.
			 */
			List<Telefone> telefonesNaoRemovidos = new ArrayList<>();
			for (Telefone telefone : telefoneARemover.getPessoa().getTelefones()) {
				if (!telefoneARemover.getTipo().equals(telefone.getTipo())) {
					telefonesNaoRemovidos.add(telefone);
				}
			}
			telefoneARemover.getPessoa().getTelefones().clear();
			telefoneARemover.getPessoa().getTelefones()
					.addAll(telefonesNaoRemovidos);

			((AdaptadorRecyclerTelefones) mRecyclerTelefones.getAdapter())
					.removerItem(telefoneARemover);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_label_telefones_hint);
		if (!mTelefones.isEmpty()) {
			for (Telefone telefone : mTelefones.get(0).getPessoa().getTelefones()) {
				if (!mTelefones.contains(telefone)) {
					mTelefones.add(telefone);
					((AdaptadorRecyclerTelefones) mRecyclerTelefones.getAdapter())
							.adicionarItem(telefone);
				}
			}
		}
		mButtonNovoTelefone
				.setVisibility(mTelefones.size() < Telefone.Tipo.values().length
						? View.VISIBLE : View.GONE);
	}

	public interface OnTelefoneSelectedListener {

		void onTelefoneSelected(Telefone telefone);
	}
}
