package com.kinlhp.steve.atividade;

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

		NavigationView navigationView = findViewById(R.id.navigation_dashboard);
		Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
		mDrawerLayout = findViewById(R.id.activity_dashboard);

		setSupportActionBar(toolbar);

		ActionBarDrawerToggle toggle =
				new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.dashboard_abrir, R.string.dashboard_fechar);
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
			case R.id.subitem_pessoa:
				iniciarPessoa();
				break;
			case R.id.subitem_credencial:
				iniciarCredencial();
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
				Toast.makeText(this, "Implementar", Toast.LENGTH_SHORT)
						.show();
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

	private void iniciarCredencial() {
		Intent intentCredencial =
				new Intent(this, CredencialActivity.class);
		startActivityForResult(intentCredencial, 0);
	}

	private void iniciarPessoa() {
		Intent intentPessoa =
				new Intent(this, PessoaActivity.class);
		startActivityForResult(intentPessoa, 0);
	}

	private void ocultarDashboard() {
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}
}
