package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerContasReceber;
import com.kinlhp.steve.dominio.ContaReceber;

import java.io.Serializable;
import java.util.ArrayList;

public class ContasReceberCadastroPreVisualizacaoFragment extends Fragment implements Serializable {
	private static final long serialVersionUID = -5267748102421917397L;
	private static final String CONTAS_RECEBER = "contasReceber";
	private AdaptadorRecyclerContasReceber mAdaptadorContasReceber;
	private ArrayList<ContaReceber> mContasReceber = new ArrayList<>();

	private AppCompatTextView mLabel0Registros;
	private RecyclerView mRecyclerContasReceber;

	/**
	 * Construtor padrão é obrigatório
	 */
	public ContasReceberCadastroPreVisualizacaoFragment() {
	}

	public static ContasReceberCadastroPreVisualizacaoFragment newInstance(@NonNull ArrayList<ContaReceber> contasReceber) {
		ContasReceberCadastroPreVisualizacaoFragment fragmento =
				new ContasReceberCadastroPreVisualizacaoFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(CONTAS_RECEBER, contasReceber);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//noinspection unchecked
			mContasReceber = (ArrayList<ContaReceber>) savedInstanceState
					.getSerializable(CONTAS_RECEBER);
		} else if (getArguments() != null) {
			//noinspection unchecked
			mContasReceber = (ArrayList<ContaReceber>) getArguments()
					.getSerializable(CONTAS_RECEBER);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_contas_receber_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mRecyclerContasReceber = view
				.findViewById(R.id.recycler_contas_receber);

		mRecyclerContasReceber.setHasFixedSize(true);
		mAdaptadorContasReceber =
				new AdaptadorRecyclerContasReceber(mContasReceber);
		mRecyclerContasReceber.setAdapter(mAdaptadorContasReceber);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerContasReceber.setLayoutManager(gerenciador);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.conta_receber_cadastro_label_parcelas_hint);
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mContasReceber.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	public void setContasReceber(@NonNull ArrayList<ContaReceber> contasReceber) {
		mContasReceber = contasReceber;
		if (getArguments() != null) {
			getArguments().putSerializable(CONTAS_RECEBER, mContasReceber);
		}
	}
}
