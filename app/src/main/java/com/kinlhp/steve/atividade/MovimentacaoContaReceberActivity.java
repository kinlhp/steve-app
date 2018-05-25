package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.CondicoesPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.FormasPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.MovimentacaoContaReceberCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.OrdensPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.PessoasPesquisaFragment;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.ContaReceber;
import com.kinlhp.steve.dominio.FormaPagamento;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dominio.Pessoa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class MovimentacaoContaReceberActivity extends AppCompatActivity
		implements
		CondicoesPagamentoPesquisaFragment.OnCondicaoPagamentoSelecionadoListener,
		CondicoesPagamentoPesquisaFragment.OnLongoCondicaoPagamentoSelecionadoListener,
		MovimentacaoContaReceberCadastroFragment.OnClientesPesquisaListener,
		MovimentacaoContaReceberCadastroFragment.OnCondicoesPagamentoPesquisaListener,
		MovimentacaoContaReceberCadastroFragment.OnParcelasGeradoListener,
		MovimentacaoContaReceberCadastroFragment.OnFormasPagamentoPesquisaListener,
		MovimentacaoContaReceberCadastroFragment.OnOrdensPesquisaListener,
		MovimentacaoContaReceberCadastroFragment.OnParcelasPesquisaListener,
		FormasPagamentoPesquisaFragment.OnFormaPagamentoSelecionadoListener,
		FormasPagamentoPesquisaFragment.OnLongoFormaPagamentoSelecionadoListener,
		OrdensPesquisaFragment.OnLongoOrdemSelecionadoListener,
		OrdensPesquisaFragment.OnOrdemSelecionadoListener,
		PessoasPesquisaFragment.OnLongoPessoaSelecionadoListener,
		PessoasPesquisaFragment.OnPessoaSelecionadoListener,
		Serializable {

	private CondicoesPagamentoPesquisaFragment mFragmentoCondicoesPagamentoPesquisa;
	private FormasPagamentoPesquisaFragment mFragmentoFormasPagamentoPesquisa;
	private MovimentacaoContaReceberCadastroFragment mFragmentoMovimentacaoContaReceberCadastro;
	private OrdensPesquisaFragment mFragmentoOrdensPesquisa;
	private PessoasPesquisaFragment mFragmentoPessoasPesquisa;
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
	public void onLongoOrdemSelecionado(@NonNull View view, @NonNull Ordem ordem) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setOrdem(ordem);
		}
	}

	@Override
	public void onLongoPessoaSelecionado(@NonNull View view, @NonNull Pessoa pessoa) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setCliente(pessoa);
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
	public void onOrdemSelecionado(@NonNull View view, @NonNull Ordem ordem) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setOrdem(ordem);
		}
	}

	@Override
	public void onOrdensPesquisa(@NonNull View view) {
		inflarOrdensPesquisa();
	}

	@Override
	public void onParcelasGerado(@NonNull View view, @NonNull ArrayList<ContaReceber> contasReceber) {
		finish();
	}

	@Override
	public void onParcelasPesquisa(@NonNull View view, @NonNull ArrayList<ContaReceber> contasReceber) {
//		inflarContasReceberPesquisa(contasReceber);
	}

	@Override
	public void onPessoaSelecionado(@NonNull View view, @NonNull Pessoa pessoa) {
		if (mFragmentoMovimentacaoContaReceberCadastro != null) {
			mFragmentoMovimentacaoContaReceberCadastro.setCliente(pessoa);
		}
	}

	@Override
	public void onClientesPesquisa(@NonNull View view) {
		inflarPessoasPesquisa();
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

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoCondicoesPagamentoPesquisa, tag).addToBackStack(tag).commit();
	}

	private void inflarContaReceberCadastro() {
		if (mFragmentoMovimentacaoContaReceberCadastro == null) {
			mFragmentoMovimentacaoContaReceberCadastro = MovimentacaoContaReceberCadastroFragment.newInstance();
		}
		mFragmentoMovimentacaoContaReceberCadastro.setOnClientesPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnCondicoesPagamentoPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnFormasPagamentoPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnOrdensPesquisaListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnParcelasGeradoListener(this);
		mFragmentoMovimentacaoContaReceberCadastro.setOnParcelasPesquisaListener(this);
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

	private void inflarOrdensPesquisa() {
		if (mFragmentoOrdensPesquisa == null) {
			mFragmentoOrdensPesquisa = OrdensPesquisaFragment.newInstance();
		}
		mFragmentoOrdensPesquisa.setOnLongoOrdemSelecionadoListener(this);
		mFragmentoOrdensPesquisa.setOnOrdemSelecionadoListener(this);
		String tag = getString(R.string.ordens_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoOrdensPesquisa, tag).addToBackStack(tag).commit();
	}

	private void inflarPessoasPesquisa() {
		if (mFragmentoPessoasPesquisa == null) {
			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
		}
		mFragmentoPessoasPesquisa.setOnLongoPessoaSelecionadoListener(this);
		mFragmentoPessoasPesquisa.setOnPessoaSelecionadoListener(this);
		String tag = getString(R.string.pessoas_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_movimentacao_conta_receber, mFragmentoPessoasPesquisa, tag).addToBackStack(tag).commit();
	}
}
