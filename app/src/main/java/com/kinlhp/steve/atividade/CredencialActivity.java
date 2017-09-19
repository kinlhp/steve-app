package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.CredencialCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.PessoasPesquisaFragment;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Pessoa;

import java.io.Serializable;

public class CredencialActivity extends AppCompatActivity
		implements CredencialCadastroFragment.OnCredenciaisPesquisaListener,
		CredencialCadastroFragment.OnCredencialAdicionadoListener,
		CredencialCadastroFragment.OnFuncionariosPesquisaListener,
		CredencialCadastroFragment.OnReferenciaCredencialAlteradoListener,
		PessoasPesquisaFragment.OnLongoPessoaSelecionadoListener,
		PessoasPesquisaFragment.OnPessoaSelecionadoListener, Serializable {
	private static final long serialVersionUID = -7758224979963289230L;
	private Credencial mCredencial = Credencial.builder().build();
	private CredencialCadastroFragment mFragmentoCredencialCadastro;
	private PessoasPesquisaFragment mFragmentoPessoasPesquisa;
	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credencial);

		Toolbar toolbar = findViewById(R.id.toolbar_credencial);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarCredencialCadastro();
	}

	@Override
	public void onCredenciaisPesquisa(@NonNull View view) {
		inflarCredenciaisPesquisa();
	}

	@Override
	public void onCredencialAdicionado(@NonNull View view,
	                                   @NonNull Credencial credencial) {
		mCredencial = credencial;
		finish();
	}

	@Override
	public void onFuncionariosPesquisa(@NonNull View view) {
		inflarPessoasPesquisa();
	}

	@Override
	public void onLongoPessoaSelecionado(@NonNull View view,
	                                     @NonNull Pessoa pessoa) {
		mCredencial.setFuncionario(pessoa);
		getSupportFragmentManager().beginTransaction()
				.remove(mFragmentoPessoasPesquisa);
		mFragmentoPessoasPesquisa = null;
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
		mCredencial.setFuncionario(pessoa);
		getSupportFragmentManager().beginTransaction()
				.remove(mFragmentoPessoasPesquisa);
		mFragmentoPessoasPesquisa = null;
	}

	@Override
	public void onReferenciaCredencialAlterado(@NonNull Credencial novaReferencia) {
		mCredencial = novaReferencia;
	}

	private void inflarCredenciaisPesquisa() {
//		if (mFragmentoPessoasPesquisa == null) {
//			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
//		}
//		mFragmentoPessoasPesquisa.setOnLongoPessoaSelecionadoListener(this);
//		mFragmentoPessoasPesquisa.setOnPessoaSelecionadoListener(this);
//		String tag = getString(R.string.pessoas_pesquisa_titulo);
//
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.content_pessoa, mFragmentoPessoasPesquisa, tag)
//				.addToBackStack(tag).commit();
		Toast.makeText(this, "Inflar pesquisa de credenciais", Toast.LENGTH_SHORT)
				.show();
	}

	private void inflarCredencialCadastro() {
		if (mFragmentoCredencialCadastro == null) {
			mFragmentoCredencialCadastro = CredencialCadastroFragment
					.newInstance(mCredencial);
		} else {
			mFragmentoCredencialCadastro.setCredencial(mCredencial);
		}
		mFragmentoCredencialCadastro.setOnCredencialAdicionadoListener(this);
		mFragmentoCredencialCadastro.setOnFuncionariosPesquisaListener(this);
		mFragmentoCredencialCadastro
				.setOnReferenciaCredencialAlteradoListener(this);
		mFragmentoCredencialCadastro.setOnCredenciaisPesquisaListener(this);
		String tag = getString(R.string.credencial_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_credencial, mFragmentoCredencialCadastro, tag)
					.commit();
		}
	}

	private void inflarPessoasPesquisa() {
		if (mFragmentoPessoasPesquisa == null) {
			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
		}
		mFragmentoPessoasPesquisa.setOnLongoPessoaSelecionadoListener(this);
		mFragmentoPessoasPesquisa.setOnPessoaSelecionadoListener(this);
		String tag = getString(R.string.pessoas_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_credencial, mFragmentoPessoasPesquisa, tag)
				.addToBackStack(tag).commit();
	}
}
