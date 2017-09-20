package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.FormaPagamentoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.FormasPagamentoPesquisaFragment;
import com.kinlhp.steve.dominio.FormaPagamento;

import java.io.Serializable;

public class FormaPagamentoActivity extends AppCompatActivity
		implements FormaPagamentoCadastroFragment.OnFormaPagamentoAdicionadoListener,
		FormaPagamentoCadastroFragment.OnFormasPagamentoPesquisaListener,
		FormaPagamentoCadastroFragment.OnReferenciaFormaPagamentoAlteradoListener,
		FormasPagamentoPesquisaFragment.OnFormaPagamentoSelecionadoListener,
		FormasPagamentoPesquisaFragment.OnLongoFormaPagamentoSelecionadoListener,
		Serializable {
	private static final long serialVersionUID = 6059313423075729603L;
	private FormaPagamentoCadastroFragment mFragmentoFormaPagamentoCadastro;
	private FormasPagamentoPesquisaFragment mFragmentoFormasPagamentoPesquisa;
	private FormaPagamento mFormaPagamento = FormaPagamento.builder().build();
	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forma_pagamento);

		Toolbar toolbar = findViewById(R.id.toolbar_forma_pagamento);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarFormaPagamentoCadastro();
	}

	@Override
	public void onFormaPagamentoAdicionado(@NonNull View view,
	                                       @NonNull FormaPagamento formaPagamento) {
		mFormaPagamento = formaPagamento;
		finish();
	}

	@Override
	public void onFormaPagamentoSelecionado(@NonNull View view,
	                                        @NonNull FormaPagamento formaPagamento) {
		// TODO: 9/18/17 definir implementações diferentes para clique curto e longo
		mFormaPagamento = formaPagamento;
		if (mFragmentoFormaPagamentoCadastro != null) {
			mFragmentoFormaPagamentoCadastro.setFormaPagamento(mFormaPagamento);
		}
	}

	@Override
	public void onFormasPagamentoPesquisa(@NonNull View view) {
		inflarFormasPagamentoPesquisa();
	}

	@Override
	public void onLongoFormaPagamentoSelecionado(@NonNull View view,
	                                             @NonNull FormaPagamento formaPagamento) {
		// TODO: 9/18/17 definir implementações diferentes para clique curto e longo
		mFormaPagamento = formaPagamento;
		if (mFragmentoFormaPagamentoCadastro != null) {
			mFragmentoFormaPagamentoCadastro.setFormaPagamento(mFormaPagamento);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onReferenciaFormaPagamentoAlterado(@NonNull FormaPagamento novaReferencia) {
		mFormaPagamento = novaReferencia;
	}

	private void inflarFormaPagamentoCadastro() {
		if (mFragmentoFormaPagamentoCadastro == null) {
			mFragmentoFormaPagamentoCadastro = FormaPagamentoCadastroFragment
					.newInstance(mFormaPagamento);
		} else {
			mFragmentoFormaPagamentoCadastro.setFormaPagamento(mFormaPagamento);
		}
		mFragmentoFormaPagamentoCadastro
				.setOnFormaPagamentoAdicionadoListener(this);
		mFragmentoFormaPagamentoCadastro
				.setOnFormasPagamentoPesquisaListener(this);
		mFragmentoFormaPagamentoCadastro
				.setOnReferenciaFormaPagamentoAlteradoListener(this);
		String tag = getString(R.string.forma_pagamento_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_forma_pagamento, mFragmentoFormaPagamentoCadastro, tag)
					.commit();
		}
	}

	private void inflarFormasPagamentoPesquisa() {
		if (mFragmentoFormasPagamentoPesquisa == null) {
			mFragmentoFormasPagamentoPesquisa = FormasPagamentoPesquisaFragment
					.newInstance();
		}
		mFragmentoFormasPagamentoPesquisa
				.setOnLongoFormaPagamentoSelecionadoListener(this);
		mFragmentoFormasPagamentoPesquisa
				.setOnFormaPagamentoSelecionadoListener(this);
		String tag = getString(R.string.formas_pagamento_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_forma_pagamento, mFragmentoFormasPagamentoPesquisa, tag)
				.addToBackStack(tag).commit();
	}
}
