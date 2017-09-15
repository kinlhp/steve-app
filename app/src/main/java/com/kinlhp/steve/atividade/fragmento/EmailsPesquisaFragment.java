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
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerEmails;
import com.kinlhp.steve.dominio.Email;

import java.io.Serializable;
import java.util.ArrayList;

public class EmailsPesquisaFragment extends Fragment
		implements View.OnClickListener,
		AdaptadorRecyclerEmails.OnItemClickListener,
		AdaptadorRecyclerEmails.OnItemLongClickListener, Serializable {
	private static final long serialVersionUID = 2587003421554623755L;
	private static final String EMAILS = "emails";
	private AdaptadorRecyclerEmails mAdaptadorEmails;
	private ArrayList<Email> mEmails;
	private OnEmailSelecionadoListener mOnEmailSelecionadoListener;
	private OnLongoEmailSelecionadoListener mOnLongoEmailSelecionadoListener;

	private AppCompatImageButton mButtonNovoEmail;
	private RecyclerView mRecyclerEmails;

	/**
	 * Construtor padrão é obrigatório
	 */
	public EmailsPesquisaFragment() {
	}

	public static EmailsPesquisaFragment newInstance(@NonNull ArrayList<Email> emails) {
		EmailsPesquisaFragment fragmento = new EmailsPesquisaFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(EMAILS, emails);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_novo_email:
				Email email = Email.builder().tipo(null).build();
				if (mOnEmailSelecionadoListener != null) {
					mOnEmailSelecionadoListener.onEmailSelecionado(view, email);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//noinspection unchecked
			mEmails = (ArrayList<Email>) savedInstanceState
					.getSerializable(EMAILS);
		} else if (getArguments() != null) {
			//noinspection unchecked
			mEmails = (ArrayList<Email>) getArguments().getSerializable(EMAILS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_emails_pesquisa, container, false);
		mButtonNovoEmail = view.findViewById(R.id.button_novo_email);
		mRecyclerEmails = view.findViewById(R.id.recycler_emails);

		mRecyclerEmails.setHasFixedSize(true);
		mAdaptadorEmails = new AdaptadorRecyclerEmails(mEmails);
		mAdaptadorEmails.setOnItemClickListener(this);
		mAdaptadorEmails.setOnItemLongClickListener(this);
		mRecyclerEmails.setAdapter(mAdaptadorEmails);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerEmails.setLayoutManager(gerenciador);

		mButtonNovoEmail.setOnClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		Email email = mEmails.get(posicao);
		if (mOnEmailSelecionadoListener != null) {
			mOnEmailSelecionadoListener.onEmailSelecionado(view, email);
		}
	}

	@Override
	public void onItemLongClick(View view, int posicao) {
		Email email = mEmails.get(posicao);
		if (mOnLongoEmailSelecionadoListener != null) {
			mOnLongoEmailSelecionadoListener
					.onLongoEmailSelecionado(view, email);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoa_cadastro_label_emails_hint);
		alternarButtonNovoEmail();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EMAILS, mEmails);
	}

	public void addEmail(@NonNull Email email) {
		mEmails.add(email);
		int indice = mEmails.indexOf(email);
		mAdaptadorEmails.notifyItemInserted(indice);
	}

	private void alternarButtonNovoEmail() {
		mButtonNovoEmail
				.setVisibility(mEmails.size() < Email.Tipo.values().length
						? View.VISIBLE : View.GONE);
	}

	public void removeEmail(@NonNull Email email) {
		if (mEmails.contains(email)) {
			int indice = mEmails.indexOf(email);
			mEmails.remove(indice);
			mAdaptadorEmails.notifyItemRemoved(indice);
		}
		alternarButtonNovoEmail();
	}

	public void setEmails(@NonNull ArrayList<Email> emails) {
		mEmails = emails;
		if (getArguments() != null) {
			getArguments().putSerializable(EMAILS, mEmails);
		}
	}

	public void setOnEmailSelecionadoListener(@Nullable OnEmailSelecionadoListener ouvinte) {
		mOnEmailSelecionadoListener = ouvinte;
	}

	public void setOnLongoEmailSelecionadoListener(@Nullable OnLongoEmailSelecionadoListener ouvinte) {
		mOnLongoEmailSelecionadoListener = ouvinte;
	}

	public interface OnEmailSelecionadoListener {

		void onEmailSelecionado(@NonNull View view, @NonNull Email email);
	}

	public interface OnLongoEmailSelecionadoListener {

		void onLongoEmailSelecionado(@NonNull View view, @NonNull Email email);
	}
}
