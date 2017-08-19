package com.kinlhp.steve.atividade;

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
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;

public class DashboardActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		Serializable {
	private static final long serialVersionUID = 5293247457273355103L;
	private DrawerLayout mDrawerLayout;

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

		mDrawerLayout = findViewById(R.id.activity_dashboard);
		NavigationView navigationView = findViewById(R.id.navigation_dashboard);
		Toolbar toolbar = findViewById(R.id.toolbar_dashboard);

		setSupportActionBar(toolbar);

		ActionBarDrawerToggle toggle =
				new ActionBarDrawerToggle(DashboardActivity.this, mDrawerLayout, toolbar, R.string.dashboard_abrir, R.string.dashboard_fechar);
		mDrawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		navigationView.setNavigationItemSelectedListener(this);
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
			default:
				Toast.makeText(DashboardActivity.this, "Implementar", Toast.LENGTH_SHORT)
						.show();
		}
		ocultarDashboard();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item.getItemId() == R.id.action_configuracao) {
			Toast.makeText(DashboardActivity.this, "Implementar", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		exibirDashboard();
		Credencial credencial = (Credencial) Parametro
				.get(Parametro.Chave.CREDENCIAL);
		String mensagem = "Olá " + credencial.getFuncionario().getNomeRazao();
		Toast.makeText(DashboardActivity.this, mensagem, Toast.LENGTH_SHORT)
				.show();
	}

	private void exibirDashboard() {
		mDrawerLayout.openDrawer(GravityCompat.START);
	}

	private void ocultarDashboard() {
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}
}
