package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.EmailCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.PessoaCadastroEmailsFragment;
import com.kinlhp.steve.atividade.fragmento.PessoaCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.PessoaCadastroTelefonesFragment;
import com.kinlhp.steve.atividade.fragmento.TelefoneCadastroFragment;
import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PessoaCadastroActivity extends AppCompatActivity
		implements View.OnClickListener,
		EmailCadastroFragment.OnEmailAddedListener,
		PessoaCadastroEmailsFragment.OnEmailSelectedListener,
		PessoaCadastroFragment.OnListClickListener,
		TelefoneCadastroFragment.OnTelefoneAddedListener,
		PessoaCadastroTelefonesFragment.OnTelefoneSelectedListener,
		Serializable {
	private static final long serialVersionUID = -2399747618761776509L;
	private ArrayList<Email> mEmails = new ArrayList<>();
	private Pessoa mPessoa = Pessoa.builder().build();
	private ArrayList<Telefone> mTelefones = new ArrayList<>();

	private FloatingActionButton mButtonPessoaPesquisa;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_pessoa_pesquisa:
				Toast.makeText(this, "Inflar pesquisa de pessoas", Toast.LENGTH_SHORT)
						.show();
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pessoa_cadastro);

		Toolbar toolbar = findViewById(R.id.toolbar_pessoa_cadastro);
		mButtonPessoaPesquisa = findViewById(R.id.button_pessoa_pesquisa);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mButtonPessoaPesquisa.setOnClickListener(this);

		PessoaCadastroFragment fragmento = PessoaCadastroFragment
				.newInstance(mPessoa);
		String tag = getString(R.string.pessoa_cadastro_titulo);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_pessoa_cadastro, fragmento, tag).commit();
	}

	@Override
	public void onEmailAdded(Email email) {
		if (!mPessoa.getEmails().contains(email)) {
			// TODO: 9/8/17 corrigir essa gambiarra
			/*
			essa gambiarra foi necessária pois a validação acima não funciona
			quando o Tipo é alterado, gerando assim duplicidade no Set<Email>.
			 */
			List<Email> emails = new ArrayList<>(mPessoa.getEmails());
			int indice = emails.indexOf(email);
			if (indice < 0) {
				mPessoa.getEmails().add(email);
			}
		}
		onBackPressed();
	}

	@Override
	public void onEmailsClick(Set<Email> emails) {
		mButtonPessoaPesquisa.setVisibility(View.GONE);
		if (mPessoa.getEmails().isEmpty()) {
			onEmailSelected(Email.builder().build());
		} else {
			mEmails.clear();
			mEmails.addAll(mPessoa.getEmails());
			Fragment fragmento = PessoaCadastroEmailsFragment
					.newInstance(mEmails);
			String tag = getString(R.string.pessoa_cadastro_label_emails_hint);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_pessoa_cadastro, fragmento, tag)
					.addToBackStack(tag).commit();
		}
	}

	@Override
	public void onEmailSelected(Email email) {
		if (email.getPessoa() == null) {
			email.setPessoa(mPessoa);
		}
		Fragment fragmento = EmailCadastroFragment.newInstance(email);
		String tag = getString(R.string.email_cadastro_titulo);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, fragmento, tag)
				.addToBackStack(tag).commit();
	}

	@Override
	public void onEnderecosClick(Set<Endereco> enderecos) {
		mPessoa.setEnderecos(enderecos);
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
	public void onTelefoneAdded(Telefone telefone) {
		if (!mPessoa.getTelefones().contains(telefone)) {
			// TODO: 9/8/17 corrigir essa gambiarra
			/*
			essa gambiarra foi necessária pois a validação acima não funciona
			quando o Tipo é alterado, gerando assim duplicidade no Set<Telefone>.
			 */
			List<Telefone> telefones = new ArrayList<>(mPessoa.getTelefones());
			int indice = telefones.indexOf(telefone);
			if (indice < 0) {
				mPessoa.getTelefones().add(telefone);
			}
		}
		onBackPressed();
	}

	@Override
	public void onTelefonesClick(Set<Telefone> telefones) {
		mButtonPessoaPesquisa.setVisibility(View.GONE);
		if (mPessoa.getTelefones().isEmpty()) {
			onTelefoneSelected(Telefone.builder().build());
		} else {
			mTelefones.clear();
			mTelefones.addAll(mPessoa.getTelefones());
			Fragment fragmento = PessoaCadastroTelefonesFragment
					.newInstance(mTelefones);
			String tag =
					getString(R.string.pessoa_cadastro_label_telefones_hint);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_pessoa_cadastro, fragmento, tag)
					.addToBackStack(tag).commit();
		}
	}

	@Override
	public void onTelefoneSelected(Telefone telefone) {
		if (telefone.getPessoa() == null) {
			telefone.setPessoa(mPessoa);
		}
		Fragment fragmento = TelefoneCadastroFragment.newInstance(telefone);
		String tag = getString(R.string.telefone_cadastro_titulo);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, fragmento, tag)
				.addToBackStack(tag).commit();
	}
}
