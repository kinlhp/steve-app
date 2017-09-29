package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.kinlhp.steve.R;

public class ContaPagarActivity extends AppCompatActivity {
	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conta_pagar);

		Toolbar toolbar = findViewById(R.id.toolbar_conta_pagar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarContaReceberCadastro();
	}

	private void inflarContaReceberCadastro() {
		Toast.makeText(this, "Inflar cadastro de contas a pagar", Toast.LENGTH_SHORT).show();
	}
}
