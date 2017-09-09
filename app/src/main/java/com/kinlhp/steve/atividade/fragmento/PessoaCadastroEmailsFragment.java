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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerEmails;
import com.kinlhp.steve.dominio.Email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PessoaCadastroEmailsFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerEmails.OnItemClickListener,
		AdaptadorRecyclerEmails.OnItemLongClickListener, Serializable {
	private static final long serialVersionUID = -7433581251396015578L;
	private static final String EMAILS = "emails";
	private ArrayList<Email> mEmails;
	private OnEmailSelectedListener mEmailSelectedListener;

	private AppCompatImageButton mButtonNovoEmail;
	private RecyclerView mRecyclerEmails;

	/**
	 * Construtor padrão é obrigatório
	 */
	public PessoaCadastroEmailsFragment() {
	}

	public static PessoaCadastroEmailsFragment newInstance(@NonNull ArrayList<Email> emails) {
		PessoaCadastroEmailsFragment fragmento =
				new PessoaCadastroEmailsFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(EMAILS, emails);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnEmailSelectedListener) {
			mEmailSelectedListener = (OnEmailSelectedListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnEmailSelectedListener");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_email:
				((PessoaCadastroActivity) getActivity())
						.onEmailSelected(Email.builder().build());
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//noinspection unchecked
		mEmails = getArguments() != null
				? (ArrayList<Email>) getArguments().getSerializable(EMAILS)
				: new ArrayList<Email>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoa_cadastro_emails, container, false);
		mButtonNovoEmail = view.findViewById(R.id.button_novo_email);
		mRecyclerEmails = view.findViewById(R.id.recycler_emails);

		mRecyclerEmails.setHasFixedSize(true);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerEmails.setLayoutManager(gerenciador);
		RecyclerView.Adapter adaptador = new AdaptadorRecyclerEmails(mEmails);
		((AdaptadorRecyclerEmails) adaptador).addOnItemClickListener(this);
		((AdaptadorRecyclerEmails) adaptador).addOnItemLongClickListener(this);
		mRecyclerEmails.setAdapter(adaptador);

		mButtonNovoEmail.setOnClickListener(this);

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mEmailSelectedListener = null;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mEmailSelectedListener.onEmailSelected(mEmails.get(posicao));
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		Email emailARemover = mEmails.get(posicao);
		if (mEmails.contains(emailARemover) && emailARemover.getId() == null) {
			emailARemover.getPessoa().getEmails().remove(emailARemover);

			// TODO: 9/9/17 corrigir essa gambiarra
			/*
			essa gambiarra foi necessária pois a ação acima não remove o Email
			do Set<Email>.
			 */
			List<Email> emailsNaoRemovidos = new ArrayList<>();
			for (Email email : emailARemover.getPessoa().getEmails()) {
				if (!emailARemover.getTipo().equals(email.getTipo())) {
					emailsNaoRemovidos.add(email);
				}
			}
			emailARemover.getPessoa().getEmails().clear();
			emailARemover.getPessoa().getEmails().addAll(emailsNaoRemovidos);

			((AdaptadorRecyclerEmails) mRecyclerEmails.getAdapter())
					.removerItem(emailARemover);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_label_emails_hint);
		if (!mEmails.isEmpty()) {
			for (Email email : mEmails.get(0).getPessoa().getEmails()) {
				if (!mEmails.contains(email)) {
					mEmails.add(email);
					((AdaptadorRecyclerEmails) mRecyclerEmails.getAdapter())
							.adicionarItem(email);
				}
			}
		}
		mButtonNovoEmail
				.setVisibility(mEmails.size() < Email.Tipo.values().length
						? View.VISIBLE : View.GONE);
	}

	public interface OnEmailSelectedListener {

		void onEmailSelected(Email email);
	}
}
