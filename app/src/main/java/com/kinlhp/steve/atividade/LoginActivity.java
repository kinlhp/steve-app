package com.kinlhp.steve.atividade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.LoginDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CredencialMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.CredencialRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.LoginRequisicao;
import com.kinlhp.steve.requisicao.Requisicao;
import com.kinlhp.steve.util.Criptografia;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
		implements View.OnClickListener, TextView.OnEditorActionListener,
		Serializable {
	private static final long serialVersionUID = 4914198791424849025L;
	private AppCompatImageButton mButtonAutenticacao;
	private Credencial mCredencial;
	private CredencialDTO mCredencialDTO;
	private TextInputEditText mInputSenha;
	private TextInputEditText mInputUsuario;
	private LoginDTO mLoginDTO;
	private ProgressBar mProgressBarAutenticacao;

	@Override
	public void onClick(@NonNull View view) {
		if (view.getId() == mButtonAutenticacao.getId()) {
			Teclado.ocultar(this, view);
			exibirBarraDeProgresso();
			mLoginDTO = iterarFormulario();
			consumirToken(mLoginDTO);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mButtonAutenticacao = findViewById(R.id.button_autenticacao);
		mProgressBarAutenticacao = findViewById(R.id.progress_bar_autenticacao);
		mInputSenha = findViewById(R.id.input_senha);
		mInputUsuario = findViewById(R.id.input_usuario);
		Toolbar toolbar = findViewById(R.id.toolbar_login);

		setSupportActionBar(toolbar);
		setTitle(getString(R.string.login_title));

		mButtonAutenticacao.setOnClickListener(this);
		mInputSenha.setOnEditorActionListener(this);
	}

	@Override
	public boolean onEditorAction(TextView view, int id, KeyEvent event) {
		if (id == EditorInfo.IME_ACTION_DONE) {
			mButtonAutenticacao.performClick();
		}
		return false;
	}

	private void consumirCredencialPorUsuario() {
		String usuario = mLoginDTO.getUsuario();
		CredencialRequisicao.getPorUsuario(new CredencialCallback(), usuario);
	}

	private void consumirFuncionario() {
		HRef funcionario = mCredencialDTO.getLinks().getFuncionario();
		CredencialRequisicao.getFuncionario(new PessoaCallback(), funcionario);
	}

	private void consumirToken(@NonNull LoginDTO dto) {
		try {
			dto.setSenha(Criptografia.sha512(dto.getSenha()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			String mensagem = getString(R.string.suporte_criptografia_senha);
			Snackbar.make(mButtonAutenticacao, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		LoginRequisicao.post(new LoginCallback(), dto);
	}

	private void exibirBarraDeProgresso() {
		mButtonAutenticacao.setVisibility(View.GONE);
		mProgressBarAutenticacao.setVisibility(View.VISIBLE);
	}

	private void iniciarDashboard() {
		if (!mCredencial.getSituacao().equals(Credencial.Situacao.ATIVO)) {
			String mensagem = getString(R.string.login_message_usuario_inativo);
			mensagem = String
					.format(mensagem, mCredencial.getFuncionario().getNomeRazao());
			ocultarBarraDeProgresso();
			Snackbar.make(mButtonAutenticacao, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		Intent intentDashboard =
				new Intent(this, DashboardActivity.class);
		startActivity(intentDashboard);
		finish();
	}

	private LoginDTO iterarFormulario() {
		String usuario = mInputUsuario.getText().toString();
		String senha = mInputSenha.getText().toString();
		return LoginDTO.builder().usuario(usuario).senha(senha).build();
	}

	private void ocultarBarraDeProgresso() {
		mProgressBarAutenticacao.setVisibility(View.GONE);
		mButtonAutenticacao.setVisibility(View.VISIBLE);
	}

	private class CredencialCallback implements Callback<CredencialDTO> {

		@Override
		public void onResponse(@NonNull Call<CredencialDTO> chamada,
		                       @NonNull Response<CredencialDTO> resposta) {
			if (!resposta.isSuccessful()) {
				ocultarBarraDeProgresso();
				Falha.tratar(mButtonAutenticacao, resposta);
			} else {
				mCredencialDTO = resposta.body();
				mCredencial = CredencialMapeamento.deDTO(mCredencialDTO);
				Parametro.put(Parametro.Chave.CREDENCIAL, mCredencial);
				consumirFuncionario();
			}
		}

		@Override
		public void onFailure(@NonNull Call<CredencialDTO> chamada,
		                      @NonNull Throwable causa) {
			ocultarBarraDeProgresso();
			Falha.tratar(mButtonAutenticacao, causa);
		}
	}

	private class LoginCallback implements Callback<Void> {

		@Override
		public void onResponse(@NonNull Call<Void> chamada,
		                       @NonNull Response<Void> resposta) {
			if (!resposta.isSuccessful()) {
				ocultarBarraDeProgresso();
				mInputSenha.setText("");
				Falha.tratar(mButtonAutenticacao, resposta);
			} else {
				String token = resposta.headers()
						.get(Requisicao.AUTHORIZATION_HEADER);
				Parametro.put(Parametro.Chave.TOKEN, token);
				consumirCredencialPorUsuario();
			}
		}

		@Override
		public void onFailure(@NonNull Call<Void> chamada,
		                      @NonNull Throwable causa) {
			ocultarBarraDeProgresso();
			Falha.tratar(mButtonAutenticacao, causa);
		}
	}

	private class PessoaCallback implements Callback<PessoaDTO> {

		@Override
		public void onResponse(@NonNull Call<PessoaDTO> chamada,
		                       @NonNull Response<PessoaDTO> resposta) {
			if (!resposta.isSuccessful()) {
				ocultarBarraDeProgresso();
				Falha.tratar(mButtonAutenticacao, resposta);
			} else {
				PessoaDTO dto = resposta.body();
				Pessoa pessoa = PessoaMapeamento.deDTO(dto);
				mCredencial.setFuncionario(pessoa);
				iniciarDashboard();
			}
		}

		@Override
		public void onFailure(@NonNull Call<PessoaDTO> chamada,
		                      @NonNull Throwable causa) {
			ocultarBarraDeProgresso();
			Falha.tratar(mButtonAutenticacao, causa);
		}
	}
}
