package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.CondicaoPagamentoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.CondicoesPagamentoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.FormaPagamentoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.FormasPagamentoPesquisaFragment;
import com.kinlhp.steve.dominio.CondicaoPagamento;
import com.kinlhp.steve.dominio.FormaPagamento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FormaPagamentoActivity extends AppCompatActivity
		implements CondicaoPagamentoCadastroFragment.OnCondicaoPagamentoAdicionadoListener,
		CondicoesPagamentoPesquisaFragment.OnCondicaoPagamentoSelecionadoListener,
		CondicoesPagamentoPesquisaFragment.OnLongoCondicaoPagamentoSelecionadoListener,
		FormaPagamentoCadastroFragment.OnCondicoesPagamentoPesquisaListener,
		FormaPagamentoCadastroFragment.OnFormaPagamentoAdicionadoListener,
		FormaPagamentoCadastroFragment.OnFormasPagamentoPesquisaListener,
		FormaPagamentoCadastroFragment.OnReferenciaFormaPagamentoAlteradoListener,
		FormasPagamentoPesquisaFragment.OnFormaPagamentoSelecionadoListener,
		FormasPagamentoPesquisaFragment.OnLongoFormaPagamentoSelecionadoListener,
		Serializable {
	private static final long serialVersionUID = 6059313423075729603L;
	private CondicaoPagamentoCadastroFragment mFragmentoCondicaoPagamentoCadastro;
	private CondicoesPagamentoPesquisaFragment mFragmentoCondicoesPagamentoPesquisa;
	private FormaPagamentoCadastroFragment mFragmentoFormaPagamentoCadastro;
	private FormasPagamentoPesquisaFragment mFragmentoFormasPagamentoPesquisa;
	private FormaPagamento mFormaPagamento = FormaPagamento.builder().build();
	private Bundle mSavedInstanceState;

	@Override
	public void onCondicaoPagamentoAdicionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<CondicaoPagamento> condicoesPagamento = new ArrayList<>(condicaoPagamento.getFormaPagamento().getCondicoesPagamento());
		if (!condicoesPagamento.contains(condicaoPagamento)) {
			condicoesPagamento.add(condicaoPagamento);
			if (mFragmentoCondicoesPagamentoPesquisa != null) {
				mFragmentoCondicoesPagamentoPesquisa.addCondicaoPagamento(condicaoPagamento);
			}
		}
		mFormaPagamento.getCondicoesPagamento().clear();
		mFormaPagamento.getCondicoesPagamento().addAll(condicoesPagamento);
	}

	@Override
	public void onCondicaoPagamentoSelecionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		inflarCondicaoPagamentoCadastro(condicaoPagamento);
	}

	@Override
	public void onCondicoesPagamentoPesquisa(@NonNull View view) {
		if (mFormaPagamento.getCondicoesPagamento().isEmpty()) {
			CondicaoPagamento condicaoPagamento = CondicaoPagamento.builder().formaPagamento(mFormaPagamento).periodoEntreParcelas(null).quantidadeParcelas(null).build();
			inflarCondicaoPagamentoCadastro(condicaoPagamento);
		} else {
			inflarCondicoesPagamentoPesquisa(mFormaPagamento.getCondicoesPagamento());
		}
	}

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
	public void onLongoCondicaoPagamentoSelecionado(@NonNull View view, @NonNull CondicaoPagamento condicaoPagamento) {
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<CondicaoPagamento> condicoesPagamento = new ArrayList<>(condicaoPagamento.getFormaPagamento().getCondicoesPagamento());
		if (condicoesPagamento.contains(condicaoPagamento) && condicaoPagamento.getId() == null) {
			condicoesPagamento.remove(condicaoPagamento);
			if (mFragmentoCondicoesPagamentoPesquisa != null) {
				mFragmentoCondicoesPagamentoPesquisa.removeCondicaoPagamento(condicaoPagamento);
			}
		}
		mFormaPagamento.getCondicoesPagamento().clear();
		mFormaPagamento.getCondicoesPagamento().addAll(condicoesPagamento);
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

	private void inflarCondicaoPagamentoCadastro(@NonNull CondicaoPagamento condicaoPagamento) {
		if (condicaoPagamento.getFormaPagamento() == null) {
			condicaoPagamento.setFormaPagamento(mFormaPagamento);
		}
		if (mFragmentoCondicaoPagamentoCadastro == null) {
			mFragmentoCondicaoPagamentoCadastro = CondicaoPagamentoCadastroFragment.newInstance(condicaoPagamento);
		} else {
			mFragmentoCondicaoPagamentoCadastro.setCondicaoPagamento(condicaoPagamento);
		}
		mFragmentoCondicaoPagamentoCadastro.setOnCondicaoPagamentoAdicionadoListener(this);
		String tag = getString(R.string.condicao_pagamento_cadastro_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_forma_pagamento, mFragmentoCondicaoPagamentoCadastro, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarCondicoesPagamentoPesquisa(@NonNull Set<CondicaoPagamento> condicoesPagamento) {
		if (mFragmentoCondicoesPagamentoPesquisa == null) {
			mFragmentoCondicoesPagamentoPesquisa = CondicoesPagamentoPesquisaFragment
					.newInstance(new ArrayList<>(condicoesPagamento));
		} else {
			mFragmentoCondicoesPagamentoPesquisa.setCondicoesPagamento(new ArrayList<>(condicoesPagamento));
		}
		mFragmentoCondicoesPagamentoPesquisa.setOnCondicaoPagamentoSelecionadoListener(this);
		mFragmentoCondicoesPagamentoPesquisa.setOnLongoCondicaoPagamentoSelecionadoListener(this);
		String tag = getString(R.string.condicoes_pagamento_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_forma_pagamento, mFragmentoCondicoesPagamentoPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarFormaPagamentoCadastro() {
		if (mFragmentoFormaPagamentoCadastro == null) {
			mFragmentoFormaPagamentoCadastro = FormaPagamentoCadastroFragment
					.newInstance(mFormaPagamento);
		} else {
			mFragmentoFormaPagamentoCadastro.setFormaPagamento(mFormaPagamento);
		}
		mFragmentoFormaPagamentoCadastro
				.setOnCondicoesPagamentoPesquisaListener(this);
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
