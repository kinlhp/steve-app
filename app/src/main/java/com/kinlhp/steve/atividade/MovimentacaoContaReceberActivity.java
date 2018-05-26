package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.CondicoesPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.ContasReceberPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.FormasPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.MovimentacaoContaReceberCadastroFragment;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dominio.MovimentacaoContaReceber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class MovimentacaoContaReceberActivity extends AppCompatActivity
		implements
		CondicoesPagamentoPesquisaFragment.OnCondicaoPagamentoSelecionadoListener,
		CondicoesPagamentoPesquisaFragment.OnLongoCondicaoPagamentoSelecionadoListener,
//		MovimentacaoContaReceberCadastroFragment.OnClientesPesquisaListener,
		MovimentacaoContaReceberCadastroFragment.OnCondicoesPagamentoPesquisaListener,
//		MovimentacaoContaReceberCadastroFragment.OnParcelasGeradoListener,
		MovimentacaoContaReceberCadastroFragment.OnContasReceberPesquisaListener,
		MovimentacaoContaReceberCadastroFragment.OnFormasPagamentoPesquisaListener,
//		MovimentacaoContaReceberCadastroFragment.OnParcelasPesquisaListener,
		FormasPagamentoPesquisaFragment.OnFormaPagamentoSelecionadoListener,
		FormasPagamentoPesquisaFragment.OnLongoFormaPagamentoSelecionadoListener,
		ContasReceberPesquisaFragment.OnContaReceberSelecionadoListener,
		ContasReceberPesquisaFragment.OnLongoContaReceberSelecionadoListener,
//		PessoasPesquisaFragment.OnLongoPessoaSelecionadoListener,
//		PessoasPesquisaFragment.OnPessoaSelecionadoListener,
		Serializable {

	private ContaReceber mContaReceber = ContaReceber.builder().numeroParcela(null)
			.build();
	private MovimentacaoContaReceber mMovimentacaoContaReceber = MovimentacaoContaReceber.builder().contaReceber(mContaReceber)
			.build();
	private CondicoesPagamentoPesquisaFragment mFragmentoCondicoesPagamentoPesquisa;
	private FormasPagamentoPesquisaFragment mFragmentoFormasPagamentoPesquisa;
	private MovimentacaoContaReceberCadastroFragment mFragmentoMovimentacaoContaReceberCadastro;
	private ContasReceberPesquisaFragment mFragmentoContasReceberPesquisa;
//	private PessoasPesquisaFragment mFragmentoPessoasPesquisa;
	private Bundle mSavedInstanceState;

	@Override
	public void onCondicaoPagamentoSelecionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setCondicaoPagamento(condicaoPagamento);
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
		setContentView(R.layout.activity_movimentacao_conta_receber);

		Toolbar toolbar = findViewById(R.id.toolbar_movimentacao_conta_receber);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarContaReceberCadastro();
	}

	@Override
	public void onFormaPagamentoSelecionado(@NonNull View view, @NonNull FormaPagamento formaPagamento) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setFormaPagamento(formaPagamento);
		}
	}

	@Override
	public void onFormasPagamentoPesquisa(@NonNull View view) {
		inflarFormasPagamentoPesquisa();
	}

	@Override
	public void onLongoCondicaoPagamentoSelecionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setCondicaoPagamento(condicaoPagamento);
		}
		onBackPressed();
	}

	@Override
	public void onLongoFormaPagamentoSelecionado(@NonNull View view, @NonNull FormaPagamento formaPagamento) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setFormaPagamento(formaPagamento);
		}
	}

	@Override
	public void onLongoContaReceberSelecionado(@NonNull View view, @NonNull ContaReceber contaReceber) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setContaReceber(contaReceber);
		}
	}

//	@Override
//	public void onLongoPessoaSelecionado(@NonNull View view, @NonNull Pessoa pessoa) {
//		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
//			mFragmentoMovimentacaoContaReceberCadastro.setCliente(pessoa);
//		}
//	}

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
	public void onContaReceberSelecionado(@NonNull View view, @NonNull ContaReceber contaReceber) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setContaReceber(contaReceber);
		}
	}

	@Override
	public void onContasReceberPesquisa(@NonNull View view) {
		inflarContasReceberPesquisa();
	}

//	@Override
//	public void onParcelasGerado(@NonNull View view, @NonNull ArrayList<ContaReceber> contasReceber) {
//		finish();
//	}

//	@Override
//	public void onParcelasPesquisa(@NonNull View view) {
//		inflarContasReceberPesquisa();
//	}

//	@Override
//	public void onPessoaSelecionado(@NonNull View view, @NonNull Pessoa pessoa) {
//		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
//			mFragmentoMovimentacaoContaReceberCadastro.setCliente(pessoa);
//		}
//	}

//	@Override
//	public void onClientesPesquisa(@NonNull View view) {
//		inflarPessoasPesquisa();
//	}

	private void inflarCondicoesPagamentoPesquisa(@NonNull Set<CondicaoPagamento> condicoesPagamento) {
		if (mFragmentoCondicoesPagamentoPesquisa == null) {
			mFragmentoCondicoesPagamentoPesquisa = CondicoesPagamentoPesquisaFragment.newInstance(new ArrayList<>(condicoesPagamento));
		} else {
			mFragmentoCondicoesPagamentoPesquisa.setCondicoesPagamento(new ArrayList<>(condicoesPagamento));
		}
		mFragmentoCondicoesPagamentoPesquisa.setOnCondicaoPagamentoSelecionadoListener(this);
		mFragmentoCondicoesPagamentoPesquisa.setOnLongoCondicaoPagamentoSelecionadoListener(this);
		String tag = getString(R.string.condicoes_pagamento_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoCondicoesPagamentoPesquisa, tag).addToBackStack(tag).commit();
	}

	private void inflarContaReceberCadastro() {
		if (mFragmentoMovimentacaoContaReceberCadastro == null) {
			mFragmentoMovimentacaoContaReceberCadastro = MovimentacaoContaReceberCadastroFragment.newInstance(mMovimentacaoContaReceber);
		}
//		mFragmentoMovimentacaoContaReceberCadastro.setOnClientesPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnCondicoesPagamentoPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnFormasPagamentoPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnContasReceberPesquisaListener(this);
//		mFragmentoMovimentacaoContaReceberCadastro.setOnParcelasGeradoListener(this);
//		mFragmentoMovimentacaoContaReceberCadastro.setOnParcelasPesquisaListener(this);
		String tag = getString(R.string.movimentacao_conta_receber_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoMovimentacaoContaReceberCadastro, tag).commit();
		}
	}

//	private void inflarMovimentacoesContaReceberPesquisa(@NonNull ArrayList<MovimentacaoContaReceber> movimentacoesContaReceber) {
//		if (mFragmentoMovimentacoesContaReceberPesquisa == null) {
//			mFragmentoMovimentacoesContaReceberPesquisa = MovimentacoesContaReceberCadastroPreVisualizacaoFragment.newInstance(movimentacoesContaReceber);
//		} else {
//			mFragmentoMovimentacoesContaReceberPesquisa.setContasReceber(movimentacoesContaReceber);
//		}
//		String tag = getString(R.string.movimentacoes_conta_receber_pesquisa_titulo);
//
//		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoMovimentacoesContaReceberPesquisa, tag).addToBackStack(tag).commit();
//	}

	private void inflarFormasPagamentoPesquisa() {
		if (mFragmentoFormasPagamentoPesquisa == null) {
			mFragmentoFormasPagamentoPesquisa = FormasPagamentoPesquisaFragment.newInstance();
		}
		mFragmentoFormasPagamentoPesquisa.setOnLongoFormaPagamentoSelecionadoListener(this);
		mFragmentoFormasPagamentoPesquisa.setOnFormaPagamentoSelecionadoListener(this);
		String tag = getString(R.string.formas_pagamento_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoFormasPagamentoPesquisa, tag).addToBackStack(tag).commit();
	}

	private void inflarContasReceberPesquisa() {
		if (mFragmentoContasReceberPesquisa == null) {
			mFragmentoContasReceberPesquisa = ContasReceberPesquisaFragment.newInstance();
		}
		mFragmentoContasReceberPesquisa.setOnLongoContaReceberSelecionadoListener(this);
		mFragmentoContasReceberPesquisa.setOnContaReceberSelecionadoListener(this);
		String tag = getString(R.string.contas_receber_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoContasReceberPesquisa, tag).addToBackStack(tag).commit();
	}

//	private void inflarPessoasPesquisa() {
//		if (mFragmentoPessoasPesquisa == null) {
//			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
//		}
//		mFragmentoPessoasPesquisa.setOnLongoPessoaSelecionadoListener(this);
//		mFragmentoPessoasPesquisa.setOnPessoaSelecionadoListener(this);
//		String tag = getString(R.string.pessoas_pesquisa_titulo);
//
//		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoPessoasPesquisa, tag).addToBackStack(tag).commit();
//	}
}
