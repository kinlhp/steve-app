package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.EmailCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.EmailsPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.EnderecoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.EnderecosPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.PessoaCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.PessoasPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.TelefoneCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.TelefonesPesquisaFragment;
import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Telefone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PessoaCadastroActivity extends AppCompatActivity
		implements EmailCadastroFragment.OnEmailAdicionadoListener,
		EmailsPesquisaFragment.OnEmailSelecionadoListener,
		EmailsPesquisaFragment.OnLongoEmailSelecionadoListener,
		EnderecoCadastroFragment.OnEnderecoAdicionadoListener,
		EnderecosPesquisaFragment.OnEnderecoSelecionadoListener,
		EnderecosPesquisaFragment.OnLongoEnderecoSelecionadoListener,
		PessoaCadastroFragment.OnEmailsSelecionadosListener,
		PessoaCadastroFragment.OnEnderecosSelecionadosListener,
		PessoaCadastroFragment.OnPessoasPesquisaListener,
		PessoaCadastroFragment.OnReferenciaPessoaAlteradaListener,
		PessoaCadastroFragment.OnTelefonesSelecionadosListener,
		PessoasPesquisaFragment.OnLongoPessoaSelecionadaListener,
		PessoasPesquisaFragment.OnPessoaSelecionadaListener,
		TelefoneCadastroFragment.OnTelefoneAdicionadoListener,
		TelefonesPesquisaFragment.OnTelefoneSelecionadoListener,
		TelefonesPesquisaFragment.OnLongoTelefoneSelecionadoListener,
		Serializable {
	private static final long serialVersionUID = 8444981203026770685L;
	private EmailCadastroFragment mFragmentoEmailCadastro;
	private EmailsPesquisaFragment mFragmentoEmailsPesquisa;
	private EnderecoCadastroFragment mFragmentoEnderecoCadastro;
	private EnderecosPesquisaFragment mFragmentoEnderecosPesquisa;
	private PessoaCadastroFragment mFragmentoPessoaCadastro;
	private PessoasPesquisaFragment mFragmentoPessoasPesquisa;
	private TelefoneCadastroFragment mFragmentoTelefoneCadastro;
	private TelefonesPesquisaFragment mFragmentoTelefonesPesquisa;
	private Pessoa mPessoa = Pessoa.builder().build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pessoa_cadastro);

		Toolbar toolbar = findViewById(R.id.toolbar_pessoa_cadastro);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		inflarPessoaCadastro(savedInstanceState);
	}

	@Override
	public void onEmailAdicionado(@NonNull View view, @NonNull Email email) {
		/*
		Método contains não se comparta corretamente
		 */
//		if (!mPessoa.getEmails().contains(email)) {
//			mPessoa.getEmails().add(email);
//		}
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Email> emails = new ArrayList<>(email.getPessoa().getEmails());
		if (!emails.contains(email)) {
			emails.add(email);
			if (mFragmentoEmailsPesquisa != null) {
				mFragmentoEmailsPesquisa.addEmail(email);
			}
		}
		mPessoa.getEmails().clear();
		mPessoa.getEmails().addAll(emails);
	}

	@Override
	public void onEmailSelecionado(@NonNull View view, @NonNull Email email) {
		inflarEmailCadastro(email);
	}

	@Override
	public void onEmailsSelecionados(@NonNull View view,
	                                 @NonNull Set<Email> emails) {
		if (emails.isEmpty()) {
			Email email = Email.builder().pessoa(mPessoa).tipo(null).build();
			inflarEmailCadastro(email);
		} else {
			inflarEmailsPesquisa(emails);
		}
	}

	@Override
	public void onEnderecoAdicionado(@NonNull View view,
	                                 @NonNull Endereco endereco) {
		/*
		Método contains não se comparta corretamente
		 */
//		if (!mPessoa.getEnderecos().contains(endereco)) {
//			mPessoa.getEnderecos().add(endereco);
//		}
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Endereco> enderecos =
				new ArrayList<>(endereco.getPessoa().getEnderecos());
		if (!enderecos.contains(endereco)) {
			enderecos.add(endereco);
			if (mFragmentoEnderecosPesquisa != null) {
				mFragmentoEnderecosPesquisa.addEndereco(endereco);
			}
		}
		mPessoa.getEnderecos().clear();
		mPessoa.getEnderecos().addAll(enderecos);
	}

	@Override
	public void onEnderecoSelecionado(@NonNull View view,
	                                  @NonNull Endereco endereco) {
		inflarEnderecoCadastro(endereco);
	}

	@Override
	public void onEnderecosSelecionados(@NonNull View view,
	                                    @NonNull Set<Endereco> enderecos) {
		if (enderecos.isEmpty()) {
			Endereco endereco = Endereco.builder().pessoa(mPessoa).tipo(null)
					.build();
			inflarEnderecoCadastro(endereco);
		} else {
			inflarEnderecosPesquisa(enderecos);
		}
	}

	@Override
	public void onLongoEmailSelecionado(@NonNull View view,
	                                    @NonNull Email email) {
		/*
		Método contains não se comparta corretamente
		 */
//		if (mPessoa.getEmails().contains(email) && email.getId() == null) {
//			mPessoa.getEmails().remove(email);
//		}
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Email> emails = new ArrayList<>(email.getPessoa().getEmails());
		if (emails.contains(email) && email.getId() == null) {
			emails.remove(email);
			if (mFragmentoEmailsPesquisa != null) {
				mFragmentoEmailsPesquisa.removeEmail(email);
			}
		}
		mPessoa.getEmails().clear();
		mPessoa.getEmails().addAll(emails);
	}

	@Override
	public void onLongoEnderecoSelecionado(@NonNull View view,
	                                       @NonNull Endereco endereco) {
		/*
		Método contains não se comparta corretamente
		 */
//		if (mPessoa.getEnderecos().contains(endereco) && endereco.getId() == null) {
//			mPessoa.getEnderecos().remove(endereco);
//		}
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Endereco> enderecos = new ArrayList<>(endereco.getPessoa()
				.getEnderecos());
		if (enderecos.contains(endereco) && endereco.getId() == null) {
			enderecos.remove(endereco);
			if (mFragmentoEnderecosPesquisa != null) {
				mFragmentoEnderecosPesquisa.removeEndereco(endereco);
			}
		}
		mPessoa.getEnderecos().clear();
		mPessoa.getEnderecos().addAll(enderecos);
	}

	@Override
	public void onLongoPessoaSelecionada(@NonNull View view,
	                                     @NonNull Pessoa pessoa) {
		mPessoa = pessoa;
		// TODO: 9/14/17 inflar cadastro de pessoa
		Toast.makeText(this, "Inflar cadastro de pessoa com a pessoa selecionada", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLongoTelefoneSelecionado(@NonNull View view,
	                                       @NonNull Telefone telefone) {
		/*
		Método contains não se comparta corretamente
		 */
//		if (mPessoa.getTelefones().contains(telefone) && telefone.getId() == null) {
//			mPessoa.getTelefones().remove(telefone);
//		}
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Telefone> telefones = new ArrayList<>(telefone.getPessoa()
				.getTelefones());
		if (telefones.contains(telefone) && telefone.getId() == null) {
			telefones.remove(telefone);
			if (mFragmentoTelefonesPesquisa != null) {
				mFragmentoTelefonesPesquisa.removeTelefone(telefone);
			}
		}
		mPessoa.getTelefones().clear();
		mPessoa.getTelefones().addAll(telefones);
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
	public void onPessoaSelecionada(@NonNull View view,
	                                @NonNull Pessoa pessoa) {
		mPessoa = pessoa;
		// TODO: 9/14/17 inflar cadastro de pessoa
		Toast.makeText(this, "Inflar cadastro de pessoa com a pessoa selecionada", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onReferenciaPessoaAlterada(@NonNull Pessoa novaReferencia) {
		mPessoa = novaReferencia;
	}

	@Override
	public void onPessoasPesquisa() {
		inflarPessoasPesquisa();
	}

	@Override
	public void onTelefoneAdicionado(@NonNull View view,
	                                 @NonNull Telefone telefone) {
		/*
		Método contains não se comparta corretamente
		 */
//		if (!mPessoa.getTelefones().contains(telefone)) {
//			mPessoa.getTelefones().add(telefone);
//		}
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Telefone> telefones = new ArrayList<>(telefone.getPessoa()
				.getTelefones());
		if (!telefones.contains(telefone)) {
			telefones.add(telefone);
			if (mFragmentoTelefonesPesquisa != null) {
				mFragmentoTelefonesPesquisa.addTelefone(telefone);
			}
		}
		mPessoa.getTelefones().clear();
		mPessoa.getTelefones().addAll(telefones);
	}

	@Override
	public void onTelefoneSelecionado(@NonNull View view,
	                                  @NonNull Telefone telefone) {
		inflarTelefoneCadastro(telefone);
	}

	@Override
	public void onTelefonesSelecionados(@NonNull View view,
	                                    @NonNull Set<Telefone> telefones) {
		if (telefones.isEmpty()) {
			Telefone telefone = Telefone.builder().pessoa(mPessoa).tipo(null)
					.build();
			inflarTelefoneCadastro(telefone);
		} else {
			inflarTelefonesPesquisa(telefones);
		}
	}

	private void inflarEmailCadastro(@NonNull Email email) {
		if (email.getPessoa() == null) {
			email.setPessoa(mPessoa);
		}
		if (mFragmentoEmailCadastro == null) {
			mFragmentoEmailCadastro = EmailCadastroFragment
					.newInstance(email);
		} else {
			mFragmentoEmailCadastro.setEmail(email);
		}
		mFragmentoEmailCadastro.setOnEmailAdicionadoListener(this);
		String tag = getString(R.string.email_cadastro_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoEmailCadastro, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarEmailsPesquisa(@NonNull Set<Email> emails) {
		if (mFragmentoEmailsPesquisa == null) {
			mFragmentoEmailsPesquisa = EmailsPesquisaFragment
					.newInstance(new ArrayList<>(emails));
		} else {
			mFragmentoEmailsPesquisa.setEmails(new ArrayList<>(emails));
		}
		mFragmentoEmailsPesquisa.setOnEmailSelecionadoListener(this);
		mFragmentoEmailsPesquisa.setOnLongoEmailSelecionadoListener(this);
		String tag = getString(R.string.emails_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoEmailsPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarEnderecoCadastro(@NonNull Endereco endereco) {
		if (endereco.getPessoa() == null) {
			endereco.setPessoa(mPessoa);
		}
		if (mFragmentoEnderecoCadastro == null) {
			mFragmentoEnderecoCadastro = EnderecoCadastroFragment
					.newInstance(endereco);
		} else {
			mFragmentoEnderecoCadastro.setEndereco(endereco);
		}
		mFragmentoEnderecoCadastro.setOnEnderecoAdicionadoListener(this);
		String tag = getString(R.string.endereco_cadastro_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoEnderecoCadastro, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarEnderecosPesquisa(@NonNull Set<Endereco> enderecos) {
		if (mFragmentoEnderecosPesquisa == null) {
			mFragmentoEnderecosPesquisa = EnderecosPesquisaFragment
					.newInstance(new ArrayList<>(enderecos));
		} else {
			mFragmentoEnderecosPesquisa
					.setEnderecos(new ArrayList<>(enderecos));
		}
		mFragmentoEnderecosPesquisa.setOnEnderecoSelecionadoListener(this);
		mFragmentoEnderecosPesquisa.setOnLongoEnderecoSelecionadoListener(this);
		String tag = getString(R.string.enderecos_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoEnderecosPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarPessoaCadastro(@Nullable Bundle savedInstanceState) {
		if (mFragmentoPessoaCadastro == null) {
			mFragmentoPessoaCadastro = PessoaCadastroFragment
					.newInstance(mPessoa);
		} else {
			mFragmentoPessoaCadastro.setPessoa(mPessoa);
		}
		mFragmentoPessoaCadastro.setOnEmailsSelecionadosListener(this);
		mFragmentoPessoaCadastro.setOnEnderecosSelecionadosListener(this);
		mFragmentoPessoaCadastro.setOnPessoasPesquisaListener(this);
		mFragmentoPessoaCadastro.setOnReferenciaPessoaAlteradaListener(this);
		mFragmentoPessoaCadastro.setOnTelefonesSelecionadosListener(this);
		String tag = getString(R.string.pessoa_cadastro_titulo);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_pessoa_cadastro, mFragmentoPessoaCadastro, tag)
					.commit();
		}
	}

	private void inflarPessoasPesquisa() {
		if (mFragmentoPessoasPesquisa == null) {
			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
		}
		mFragmentoPessoasPesquisa.setOnPessoaSelecionadaListener(this);
		mFragmentoPessoasPesquisa.setOnLongoPessoaSelecionadaListener(this);

		String tag = getString(R.string.pessoas_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoPessoasPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarTelefoneCadastro(@NonNull Telefone telefone) {
		if (telefone.getPessoa() == null) {
			telefone.setPessoa(mPessoa);
		}
		if (mFragmentoTelefoneCadastro == null) {
			mFragmentoTelefoneCadastro = TelefoneCadastroFragment
					.newInstance(telefone);
		} else {
			mFragmentoTelefoneCadastro.setTelefone(telefone);
		}
		mFragmentoTelefoneCadastro.setOnTelefoneAdicionadoListener(this);
		String tag = getString(R.string.telefone_cadastro_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoTelefoneCadastro, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarTelefonesPesquisa(@NonNull Set<Telefone> telefones) {
		if (mFragmentoTelefonesPesquisa == null) {
			mFragmentoTelefonesPesquisa = TelefonesPesquisaFragment
					.newInstance(new ArrayList<>(telefones));
		} else {
			mFragmentoTelefonesPesquisa
					.setTelefones(new ArrayList<>(telefones));
		}
		mFragmentoTelefonesPesquisa.setOnTelefoneSelecionadoListener(this);
		mFragmentoTelefonesPesquisa.setOnLongoTelefoneSelecionadoListener(this);
		String tag = getString(R.string.telefones_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_pessoa_cadastro, mFragmentoTelefonesPesquisa, tag)
				.addToBackStack(tag).commit();
	}
}
