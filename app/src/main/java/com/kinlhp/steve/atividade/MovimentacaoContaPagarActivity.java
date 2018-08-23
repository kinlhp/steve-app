package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.CondicoesPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.ContasPagarPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.FormasPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.MovimentacaoContaPagarCadastroFragment;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dominio.MovimentacaoContaPagar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class MovimentacaoContaPagarActivity extends AppCompatActivity
		implements
		CondicoesPagamentoPesquisaFragment.OnCondicaoPagamentoSelecionadoListener,
		CondicoesPagamentoPesquisaFragment.OnLongoCondicaoPagamentoSelecionadoListener,
		MovimentacaoContaPagarCadastroFragment.OnCondicoesPagamentoPesquisaListener,
		MovimentacaoContaPagarCadastroFragment.OnContasPagarPesquisaListener,
		MovimentacaoContaPagarCadastroFragment.OnFormasPagamentoPesquisaListener,
		FormasPagamentoPesquisaFragment.OnFormaPagamentoSelecionadoListener,
		FormasPagamentoPesquisaFragment.OnLongoFormaPagamentoSelecionadoListener,
		ContasPagarPesquisaFragment.OnContaPagarSelecionadoListener,
		ContasPagarPesquisaFragment.OnLongoContaPagarSelecionadoListener,
		Serializable {

	private ContaPagar mContaPagar = ContaPagar.builder().numeroParcela(null)
			.build();
	private MovimentacaoContaPagar mMovimentacaoContaPagar = MovimentacaoContaPagar.builder().contaPagar(mContaPagar)
			.build();
	private CondicoesPagamentoPesquisaFragment mFragmentoCondicoesPagamentoPesquisa;
	private FormasPagamentoPesquisaFragment mFragmentoFormasPagamentoPesquisa;
	private MovimentacaoContaPagarCadastroFragment mFragmentoMovimentacaoContaPagarCadastro;
	private ContasPagarPesquisaFragment mFragmentoContasPagarPesquisa;
	private Bundle mSavedInstanceState;

	@Override
	public void onCondicaoPagamentoSelecionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		if (mFragmentoMovimentacaoContaPagarCadastro != null) {
			mFragmentoMovimentacaoContaPagarCadastro.setCondicaoPagamento(condicaoPagamento);
		}
		onBackPressed();
	}

	@Override
	public void onCondicoesPagamentoPesquisa(@NonNull View view, @NonNull Set<CondicaoPagamento> condicoesPagamento) {
		inflarCondicoesPagamentoPesquisa(condicoesPagamento);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movimentacao_conta_pagar);

		Toolbar toolbar = findViewById(R.id.toolbar_movimentacao_conta_pagar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarContaPagarCadastro();
	}

	@Override
	public void onFormaPagamentoSelecionado(@NonNull View view, @NonNull FormaPagamento formaPagamento) {
		if (mFragmentoMovimentacaoContaPagarCadastro != null) {
			mFragmentoMovimentacaoContaPagarCadastro.setFormaPagamento(formaPagamento);
		}
	}

	@Override
	public void onFormasPagamentoPesquisa(@NonNull View view) {
		inflarFormasPagamentoPesquisa();
	}

	@Override
	public void onLongoCondicaoPagamentoSelecionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		if (mFragmentoMovimentacaoContaPagarCadastro != null) {
			mFragmentoMovimentacaoContaPagarCadastro.setCondicaoPagamento(condicaoPagamento);
		}
		onBackPressed();
	}

	@Override
	public void onLongoFormaPagamentoSelecionado(@NonNull View view, @NonNull FormaPagamento formaPagamento) {
		if (mFragmentoMovimentacaoContaPagarCadastro != null) {
			mFragmentoMovimentacaoContaPagarCadastro.setFormaPagamento(formaPagamento);
		}
	}

	@Override
	public void onLongoContaPagarSelecionado(@NonNull View view, @NonNull ContaPagar contaPagar) {
		if (mFragmentoMovimentacaoContaPagarCadastro != null) {
			mFragmentoMovimentacaoContaPagarCadastro.setContaPagar(contaPagar);
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
	public void onContaPagarSelecionado(@NonNull View view, @NonNull ContaPagar contaPagar) {
		if (mFragmentoMovimentacaoContaPagarCadastro != null) {
			mFragmentoMovimentacaoContaPagarCadastro.setContaPagar(contaPagar);
		}
	}

	@Override
	public void onContasPagarPesquisa(@NonNull View view) {
		inflarContasPagarPesquisa();
	}

	private void inflarCondicoesPagamentoPesquisa(@NonNull Set<CondicaoPagamento> condicoesPagamento) {
		if (mFragmentoCondicoesPagamentoPesquisa == null) {
			mFragmentoCondicoesPagamentoPesquisa = CondicoesPagamentoPesquisaFragment.newInstance(new ArrayList<>(condicoesPagamento));
		} else {
			mFragmentoCondicoesPagamentoPesquisa.setCondicoesPagamento(new ArrayList<>(condicoesPagamento));
		}
		mFragmentoCondicoesPagamentoPesquisa.setOnCondicaoPagamentoSelecionadoListener(this);
		mFragmentoCondicoesPagamentoPesquisa.setOnLongoCondicaoPagamentoSelecionadoListener(this);
		String tag = getString(R.string.condicoes_pagamento_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_pagar, mFragmentoCondicoesPagamentoPesquisa, tag).addToBackStack(tag).commit();
	}

	private void inflarContaPagarCadastro() {
		if (mFragmentoMovimentacaoContaPagarCadastro == null) {
			mFragmentoMovimentacaoContaPagarCadastro = MovimentacaoContaPagarCadastroFragment.newInstance(mMovimentacaoContaPagar);
		}
		mFragmentoMovimentacaoContaPagarCadastro.setOnCondicoesPagamentoPesquisaListener(this);
		mFragmentoMovimentacaoContaPagarCadastro.setOnFormasPagamentoPesquisaListener(this);
		mFragmentoMovimentacaoContaPagarCadastro.setOnContasPagarPesquisaListener(this);
		String tag = getString(R.string.movimentacao_conta_pagar_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_pagar, mFragmentoMovimentacaoContaPagarCadastro, tag).commit();
		}
	}

	private void inflarFormasPagamentoPesquisa() {
		if (mFragmentoFormasPagamentoPesquisa == null) {
			mFragmentoFormasPagamentoPesquisa = FormasPagamentoPesquisaFragment.newInstance();
		}
		mFragmentoFormasPagamentoPesquisa.setOnLongoFormaPagamentoSelecionadoListener(this);
		mFragmentoFormasPagamentoPesquisa.setOnFormaPagamentoSelecionadoListener(this);
		String tag = getString(R.string.formas_pagamento_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_pagar, mFragmentoFormasPagamentoPesquisa, tag).addToBackStack(tag).commit();
	}

	private void inflarContasPagarPesquisa() {
		if (mFragmentoContasPagarPesquisa == null) {
			mFragmentoContasPagarPesquisa = ContasPagarPesquisaFragment.newInstance();
		}
		mFragmentoContasPagarPesquisa.setOnLongoContaPagarSelecionadoListener(this);
		mFragmentoContasPagarPesquisa.setOnContaPagarSelecionadoListener(this);
		String tag = getString(R.string.contas_pagar_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_pagar, mFragmentoContasPagarPesquisa, tag).addToBackStack(tag).commit();
	}
}
