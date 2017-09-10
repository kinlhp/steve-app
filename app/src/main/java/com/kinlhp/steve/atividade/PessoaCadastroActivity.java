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
import com.kinlhp.steve.atividade.fragmento.EnderecoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.PessoaCadastroEmailsFragment;
import com.kinlhp.steve.atividade.fragmento.PessoaCadastroEnderecosFragment;
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
		EnderecoCadastroFragment.OnEnderecoAddedListener,
		PessoaCadastroEnderecosFragment.OnEnderecoSelectedListener,
		PessoaCadastroFragment.OnListClickListener,
		PessoaCadastroFragment.OnPessoaChangedListener,
		TelefoneCadastroFragment.OnTelefoneAddedListener,
		PessoaCadastroTelefonesFragment.OnTelefoneSelectedListener,
		Serializable {
	private static final long serialVersionUID = 461722905967624588L;
	private ArrayList<Email> mEmails = new ArrayList<>();
	private ArrayList<Endereco> mEnderecos = new ArrayList<>();
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
			// TODO: 9/8/17 corrigir essa gambiarra [problema com equals e hashCode]
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
//		mPessoa.getEmails().clear();
//		mPessoa.getEmails().addAll(emails);
		if (mPessoa.getEmails().isEmpty()) {
			onEmailSelected(Email.builder().tipo(null).build());
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
	public void onEnderecoAdded(Endereco endereco) {
		if (!mPessoa.getEnderecos().contains(endereco)) {
			// TODO: 9/8/17 corrigir essa gambiarra [problema com equals e hashCode]
			/*
			essa gambiarra foi necessária pois a validação acima não funciona
			quando o Tipo é alterado, gerando assim duplicidade no Set<Endereco>.
			 */
			List<Endereco> enderecos = new ArrayList<>(mPessoa.getEnderecos());
			int indice = enderecos.indexOf(endereco);
			if (indice < 0) {
				mPessoa.getEnderecos().add(endereco);
			}
		}
		onBackPressed();
	}

	@Override
	public void onEnderecosClick(Set<Endereco> enderecos) {
		mButtonPessoaPesquisa.setVisibility(View.GONE);
//		mPessoa.getEnderecos().clear();
//		mPessoa.getEnderecos().addAll(enderecos);
		if (mPessoa.getEnderecos().isEmpty()) {
			onEnderecoSelected(Endereco.builder().tipo(null).build());
		} else {
			mEnderecos.clear();
			mEnderecos.addAll(mPessoa.getEnderecos());
			Fragment fragmento = PessoaCadastroEnderecosFragment
					.newInstance(mEnderecos);
			String tag = getString(R.string.pessoa_cadastro_label_enderecos_hint);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_pessoa_cadastro, fragmento, tag)
					.addToBackStack(tag).commit();
		}
	}

	@Override
	public void onEnderecoSelected(Endereco endereco) {
		if (endereco.getPessoa() == null) {
			endereco.setPessoa(mPessoa);
		}
		Fragment fragmento = EnderecoCadastroFragment.newInstance(endereco);
		String tag = getString(R.string.endereco_cadastro_titulo);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, fragmento, tag)
				.addToBackStack(tag).commit();
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
	public void onPessoaChanged(Pessoa pessoa) {
		mPessoa = pessoa;
	}

	@Override
	public void onTelefoneAdded(Telefone telefone) {
		if (!mPessoa.getTelefones().contains(telefone)) {
			// TODO: 9/8/17 corrigir essa gambiarra [problema com equals e hashCode]
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
//		mPessoa.getTelefones().clear();
//		mPessoa.getTelefones().addAll(telefones);
		if (mPessoa.getTelefones().isEmpty()) {
			onTelefoneSelected(Telefone.builder().tipo(null).build());
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
