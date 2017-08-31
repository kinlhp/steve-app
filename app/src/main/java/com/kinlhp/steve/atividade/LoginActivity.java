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
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Criptografia;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
		implements View.OnClickListener, TextView.OnEditorActionListener,
		Serializable {
	private static final long serialVersionUID = -3424404939614981647L;
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
			exibirProgresso();
			mLoginDTO = iterarFormulario();
			consumirLoginPOST(mLoginDTO);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Toolbar toolbar = findViewById(R.id.toolbar_login);
		mButtonAutenticacao = findViewById(R.id.button_autenticacao);
		mProgressBarAutenticacao = findViewById(R.id.progress_bar_autenticacao);
		mInputSenha = findViewById(R.id.input_senha);
		mInputUsuario = findViewById(R.id.input_usuario);

		setSupportActionBar(toolbar);
		setTitle(getString(R.string.login_titulo));

		mButtonAutenticacao.setOnClickListener(this);
		mInputSenha.setOnEditorActionListener(this);
	}

	@Override
	public boolean onEditorAction(TextView view, int id, KeyEvent evento) {
		switch (id) {
			case EditorInfo.IME_ACTION_GO:
				mButtonAutenticacao.performClick();
				break;
		}
		return false;
	}

	private ItemCallback<CredencialDTO> callbackCredencialGETPorUsuario() {
		return new ItemCallback<CredencialDTO>() {

			@Override
			public void onFailure(@NonNull Call<CredencialDTO> chamada,
			                      @NonNull Throwable causa) {
				ocultarProgresso();
				Falha.tratar(mButtonAutenticacao, causa);
			}

			@Override
			public void onResponse(@NonNull Call<CredencialDTO> chamada,
			                       @NonNull Response<CredencialDTO> resposta) {
				if (!resposta.isSuccessful()) {
					ocultarProgresso();
					Falha.tratar(mButtonAutenticacao, resposta);
				} else {
					mCredencialDTO = resposta.body();
					mCredencial = CredencialMapeamento
							.paraDominio(mCredencialDTO);
					Parametro.put(Parametro.Chave.CREDENCIAL, mCredencial);
					consumirCredencialGETFuncionario();
				}
			}
		};
	}

	private ItemCallback<PessoaDTO> callbackCredencialGETFuncionario() {
		return new ItemCallback<PessoaDTO>() {

			@Override
			public void onFailure(@NonNull Call<PessoaDTO> chamada,
			                      @NonNull Throwable causa) {
				ocultarProgresso();
				Falha.tratar(mButtonAutenticacao, causa);
			}

			@Override
			public void onResponse(@NonNull Call<PessoaDTO> chamada,
			                       @NonNull Response<PessoaDTO> resposta) {
				if (!resposta.isSuccessful()) {
					ocultarProgresso();
					Falha.tratar(mButtonAutenticacao, resposta);
				} else {
					PessoaDTO dto = resposta.body();
					Pessoa pessoa = PessoaMapeamento.paraDominio(dto);
					mCredencial.setFuncionario(pessoa);
					iniciarDashboard();
				}
			}
		};
	}

	private VazioCallback callbackLoginPOST() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				ocultarProgresso();
				Falha.tratar(mButtonAutenticacao, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				if (!resposta.isSuccessful()) {
					ocultarProgresso();
					mInputSenha.getText().clear();
					Falha.tratar(mButtonAutenticacao, resposta);
				} else {
					String token = resposta.headers()
							.get(Requisicao.AUTHORIZATION_HEADER);
					Parametro.put(Parametro.Chave.TOKEN, token);
					consumirCredencialGETPorUsuario();
				}
			}
		};
	}

	private void consumirCredencialGETPorUsuario() {
		String usuario = mLoginDTO.getUsuario();
		CredencialRequisicao
				.getPorUsuario(callbackCredencialGETPorUsuario(), usuario);
	}

	private void consumirCredencialGETFuncionario() {
		HRef funcionario = mCredencialDTO.getLinks().getFuncionario();
		CredencialRequisicao
				.getFuncionario(callbackCredencialGETFuncionario(), funcionario);
	}

	private void consumirLoginPOST(@NonNull LoginDTO dto) {
		try {
			dto.setSenha(Criptografia.sha512(dto.getSenha()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			String mensagem =
					getString(R.string.suporte_mensagem_criptografia_senha);
			Snackbar.make(mButtonAutenticacao, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		String urlBase = getString(R.string.requisicao_url_base);
		Parametro.put(Parametro.Chave.URL_BASE, urlBase);
		LoginRequisicao.post(callbackLoginPOST(), dto);
	}

	private void exibirProgresso() {
		mProgressBarAutenticacao.setVisibility(View.VISIBLE);
		mButtonAutenticacao.setVisibility(View.GONE);
	}

	private void iniciarDashboard() {
		if (!mCredencial.getSituacao().equals(Credencial.Situacao.ATIVO)) {
			String mensagem =
					getString(R.string.login_mensagem_usuario_inativo);
			mensagem = String
					.format(mensagem, mCredencial.getFuncionario().getNomeRazao());
			ocultarProgresso();
			Snackbar.make(mButtonAutenticacao, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		Intent intentDashboard = new Intent(this, DashboardActivity.class);
		startActivity(intentDashboard);
		finish();
	}

	private LoginDTO iterarFormulario() {
		String usuario = mInputUsuario.getText().toString();
		String senha = mInputSenha.getText().toString();
		return LoginDTO.builder().usuario(usuario).senha(senha).build();
	}

	private void ocultarProgresso() {
		mButtonAutenticacao.setVisibility(View.VISIBLE);
		mProgressBarAutenticacao.setVisibility(View.GONE);
	}
}
