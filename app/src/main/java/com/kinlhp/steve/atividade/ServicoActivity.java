package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.ServicoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.ServicosPesquisaFragment;
import com.kinlhp.steve.dominio.Servico;

import java.io.Serializable;

public class ServicoActivity extends AppCompatActivity
		implements ServicoCadastroFragment.OnServicoAdicionadoListener,
		ServicoCadastroFragment.OnServicosPesquisaListener,
		ServicoCadastroFragment.OnReferenciaServicoAlteradoListener,
		ServicosPesquisaFragment.OnServicoSelecionadoListener,
		ServicosPesquisaFragment.OnLongoServicoSelecionadoListener,
		Serializable {
	private static final long serialVersionUID = 8262299817252972206L;
	private ServicoCadastroFragment mFragmentoServicoCadastro;
	private ServicosPesquisaFragment mFragmentoServicosPesquisa;
	private Servico mServico = Servico.builder().build();
	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servico);

		Toolbar toolbar = findViewById(R.id.toolbar_servico);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarServicoCadastro();
	}

	@Override
	public void onServicoAdicionado(@NonNull View view,
	                                @NonNull Servico servico) {
		mServico = servico;
		finish();
	}

	@Override
	public void onServicoSelecionado(@NonNull View view,
	                                 @NonNull Servico servico) {
		// TODO: 9/18/17 definir implementações diferentes para clique curto e longo
		mServico = servico;
		if (mFragmentoServicoCadastro != null) {
			mFragmentoServicoCadastro.setServico(mServico);
		}
	}

	@Override
	public void onServicosPesquisa(@NonNull View view) {
		inflarServicosPesquisa();
	}

	@Override
	public void onLongoServicoSelecionado(@NonNull View view,
	                                      @NonNull Servico servico) {
		// TODO: 9/18/17 definir implementações diferentes para clique curto e longo
		mServico = servico;
		if (mFragmentoServicoCadastro != null) {
			mFragmentoServicoCadastro.setServico(mServico);
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
	public void onReferenciaServicoAlterado(@NonNull Servico novaReferencia) {
		mServico = novaReferencia;
	}

	private void inflarServicoCadastro() {
		if (mFragmentoServicoCadastro == null) {
			mFragmentoServicoCadastro = ServicoCadastroFragment
					.newInstance(mServico);
		} else {
			mFragmentoServicoCadastro.setServico(mServico);
		}
		mFragmentoServicoCadastro.setOnServicoAdicionadoListener(this);
		mFragmentoServicoCadastro.setOnServicosPesquisaListener(this);
		mFragmentoServicoCadastro.setOnReferenciaServicoAlteradoListener(this);
		String tag = getString(R.string.servico_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_servico, mFragmentoServicoCadastro, tag)
					.commit();
		}
	}

	private void inflarServicosPesquisa() {
		if (mFragmentoServicosPesquisa == null) {
			mFragmentoServicosPesquisa = ServicosPesquisaFragment.newInstance();
		}
		mFragmentoServicosPesquisa.setOnLongoServicoSelecionadoListener(this);
		mFragmentoServicosPesquisa.setOnServicoSelecionadoListener(this);
		String tag = getString(R.string.servicos_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_servico, mFragmentoServicosPesquisa, tag)
				.addToBackStack(tag).commit();
	}
}
