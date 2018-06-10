package com.kinlhp.steve.atividade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;

public class DashboardActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		Serializable {
	private static final long serialVersionUID = -9134543732313010320L;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationDashboard;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (Activity.RESULT_OK == resultCode) {
			final Ordem ordem = (Ordem) data.getSerializableExtra("ordem");
			if (ordem != null) {
				iniciarContaReceber(ordem);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			ocultarDashboard();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		mNavigationDashboard = findViewById(R.id.navigation_dashboard);
		Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
		mDrawerLayout = findViewById(R.id.activity_dashboard);

		setSupportActionBar(toolbar);

		ActionBarDrawerToggle toggle =
				new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.dashboard_abrir, R.string.dashboard_fechar);
		mDrawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		mNavigationDashboard.setNavigationItemSelectedListener(this);

		limitarMenusDisponíveis();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard_menu, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.subitem_conta_pagar_lancamento:
				iniciarContaPagar();
				break;
			case R.id.subitem_conta_pagar_pagamento:
				iniciarMovimentacaoContaPagar();
				break;
			case R.id.subitem_conta_receber_lancamento:
				iniciarContaReceber();
				break;
			case R.id.subitem_conta_receber_recebimento:
				iniciarMovimentacaoContaReceber();
				break;
			case R.id.subitem_credencial:
				iniciarCredencial();
				break;
			case R.id.subitem_forma_pagamento:
				iniciarFormaPagamento();
				break;
			case R.id.subitem_ordem_servico:
				iniciarOrdem();
				break;
			case R.id.subitem_pessoa:
				iniciarPessoa();
				break;
			case R.id.subitem_servico:
				iniciarServico();
				break;
		}
		ocultarDashboard();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_configuracao:
				Toast.makeText(this, "Implementar", Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		exibirDashboard();
	}

	private void exibirDashboard() {
		mDrawerLayout.openDrawer(GravityCompat.START);
	}

	private void iniciarContaPagar() {
		Intent intentContaPagar = new Intent(this, ContaPagarActivity.class);
		startActivityForResult(intentContaPagar, 0);
	}

	private void iniciarContaReceber() {
		Intent intentContaReceber = new Intent(this,
				ContaReceberActivity.class);
		startActivityForResult(intentContaReceber, 0);
	}

	private void iniciarContaReceber(@NonNull Ordem ordem) {
		Intent intentContaReceber = new Intent(this,
				ContaReceberActivity.class);
		intentContaReceber.putExtra("ordem", ordem);
		startActivityForResult(intentContaReceber, 0);
	}

	private void iniciarCredencial() {
		Intent intentCredencial = new Intent(this, CredencialActivity.class);
		startActivityForResult(intentCredencial, 0);
	}

	private void iniciarFormaPagamento() {
		Intent intentFormaPagamento =
				new Intent(this, FormaPagamentoActivity.class);
		startActivityForResult(intentFormaPagamento, 0);
	}

	private void iniciarMovimentacaoContaPagar() {
//		Intent intentMovimentacaoContaPagar = new Intent(this,
//				MovimentacaoContaPagarActivity.class);
//		startActivityForResult(intentMovimentacaoContaPagar, 0);
	}

	private void iniciarMovimentacaoContaReceber() {
		Intent intentMovimentacaoContaReceber = new Intent(this,
				MovimentacaoContaReceberActivity.class);
		startActivityForResult(intentMovimentacaoContaReceber, 0);
	}

	private void iniciarOrdem() {
		Intent intentOrdem = new Intent(this, OrdemActivity.class);
		startActivityForResult(intentOrdem, 0);
	}

	private void iniciarPessoa() {
		Intent intentPessoa = new Intent(this, PessoaActivity.class);
		startActivityForResult(intentPessoa, 0);
	}

	private void iniciarServico() {
		Intent intentServico = new Intent(this, ServicoActivity.class);
		startActivityForResult(intentServico, 0);
	}

	private void limitarMenusDisponíveis() {
		Credencial credencialLogado = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		if (!credencialLogado.isPerfilAdministrador()) {
			mNavigationDashboard.getMenu()
					.findItem(R.id.subitem_forma_pagamento).setEnabled(false);
			mNavigationDashboard.getMenu().findItem(R.id.subitem_servico)
					.setEnabled(false);
			mNavigationDashboard.getMenu().findItem(R.id.subitem_relatorio)
					.setEnabled(false);
			mNavigationDashboard.getMenu().findItem(R.id.subitem_credencial)
					.setEnabled(false);
		}
	}

	private void ocultarDashboard() {
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}
}
