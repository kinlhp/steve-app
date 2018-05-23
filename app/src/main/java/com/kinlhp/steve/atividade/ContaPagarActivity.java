package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.ContaPagarCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.PessoasPesquisaFragment;
import com.kinlhp.steve.dominio.ContaPagar;
import com.kinlhp.steve.dominio.Pessoa;

import java.io.Serializable;

public class ContaPagarActivity extends AppCompatActivity
		implements ContaPagarCadastroFragment.OnCedentesPesquisaListener,
		PessoasPesquisaFragment.OnPessoaSelecionadoListener,
		Serializable {
	private ContaPagar mContaPagar = ContaPagar.builder().numeroParcela(null)
			.build();
	private ContaPagarCadastroFragment mFragmentoContaPagarCadastro;
	private PessoasPesquisaFragment mFragmentoPessoasPesquisa;
	private Bundle mSavedInstanceState;

	@Override
	public void onCedentesPesquisa(@NonNull View view) {
		if (mFragmentoPessoasPesquisa == null) {
			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
		}
		mFragmentoPessoasPesquisa.setOnPessoaSelecionadoListener(this);
		String tag = getString(R.string.pessoas_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_conta_pagar, mFragmentoPessoasPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conta_pagar);

		Toolbar toolbar = findViewById(R.id.toolbar_conta_pagar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarContaPagarCadastro();
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
	public void onPessoaSelecionado(@NonNull View view,
	                                @NonNull Pessoa pessoa) {
		mContaPagar.setCedente(pessoa);
	}

	private void inflarContaPagarCadastro() {
		if (mFragmentoContaPagarCadastro == null) {
			mFragmentoContaPagarCadastro = ContaPagarCadastroFragment
					.newInstance(mContaPagar);
		}
		mFragmentoContaPagarCadastro.setOnCedentesPesquisaListener(this);
		String tag = getString(R.string.conta_pagar_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_conta_pagar, mFragmentoContaPagarCadastro, tag)
					.commit();
		}
	}
}
