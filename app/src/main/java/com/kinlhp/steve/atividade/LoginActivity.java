package com.kinlhp.steve.atividade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.TokenDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.CredencialMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.requisicao.CredencialRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.TokenRequisicao;
import com.kinlhp.steve.resposta.ItemCallback;
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
	private static final long serialVersionUID = 3914451699131021189L;
	private AppCompatImageButton mButtonAutenticacao;
	private Credencial mCredencial;
	private CredencialDTO mCredencialDTO;
	private TextInputEditText mInputSenha;
	private TextInputEditText mInputUsuario;
	private ProgressBar mProgressBarAutenticacao;
	private String mSenha;
	private String mUsuario;

	@Override
	public void onClick(@NonNull View view) {
		if (view.getId() == mButtonAutenticacao.getId()) {
			Teclado.ocultar(this, view);
			exibirProgresso();
			iterarFormulario();
			consumirTokenPOST();
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

		mInputUsuario.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_configuracao:
				abrirDialogoConfiguracao();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void abrirDialogoConfiguracao() {
		final AppCompatDialog dialogoConfiguracao = new AppCompatDialog(this);
		dialogoConfiguracao.setContentView(R.layout.view_configuracao);
		final AppCompatButton buttonSalvar = dialogoConfiguracao.findViewById(R.id.button_salvar);
		final TextInputEditText inputUrlBase = dialogoConfiguracao.findViewById(R.id.input_url_base);

		buttonSalvar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				// TODO: 6/10/18 remover hard-coded
				final SharedPreferences configuracoes = getSharedPreferences("CONFIGURACOES", MODE_PRIVATE);
				final SharedPreferences.Editor editor = configuracoes.edit();
				String urlBase = inputUrlBase.getText().toString();
				if (TextUtils.isEmpty(urlBase)) {
					urlBase = "http://localhost/";
				}
				editor.putString("URL_BASE", urlBase.endsWith("/") ? urlBase : urlBase + "/");
				editor.apply();
				dialogoConfiguracao.cancel();
			}
		});
		dialogoConfiguracao.show();
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

	private ItemCallback<TokenDTO> callbackTokenPOST() {
		return new ItemCallback<TokenDTO>() {

			@Override
			public void onFailure(@NonNull Call<TokenDTO> chamada,
			                      @NonNull Throwable causa) {
				ocultarProgresso();
				Falha.tratar(mButtonAutenticacao, causa);
			}

			@Override
			public void onResponse(@NonNull Call<TokenDTO> chamada,
			                       @NonNull Response<TokenDTO> resposta) {
				if (!resposta.isSuccessful()) {
					ocultarProgresso();
					mInputSenha.getText().clear();
					Falha.tratar(mButtonAutenticacao, resposta);
				} else {
					TokenDTO dto = resposta.body();
					Parametro.put(Parametro.Chave.TOKEN, dto);
					consumirCredencialGETPorUsuario();
				}
			}
		};
	}

	private void consumirCredencialGETPorUsuario() {
		CredencialRequisicao
				.getPorUsuario(callbackCredencialGETPorUsuario(), mUsuario);
	}

	private void consumirCredencialGETFuncionario() {
		HRef funcionario = mCredencialDTO.getLinks().getFuncionario();
		CredencialRequisicao
				.getFuncionario(callbackCredencialGETFuncionario(), funcionario);
	}

	private void consumirTokenPOST() {
		try {
			mSenha = Criptografia.sha512(mSenha);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			String mensagem =
					getString(R.string.suporte_mensagem_criptografia_senha);
			Snackbar.make(mButtonAutenticacao, mensagem, Snackbar.LENGTH_LONG)
					.show();
			return;
		}
		String nomeCliente = getString(R.string.requisicao_cliente_api_nome);
		String senhaCliente = getString(R.string.requisicao_cliente_api_senha);
		// TODO: 6/10/18 remover hard-coded
		String urlBase = getSharedPreferences("CONFIGURACOES", MODE_PRIVATE)
				.getString("URL_BASE", "http://localhost/");
		Parametro.put(Parametro.Chave.NOME_CLIENTE, nomeCliente);
		Parametro.put(Parametro.Chave.SENHA_CLIENTE, senhaCliente);
		Parametro.put(Parametro.Chave.URL_BASE, urlBase);
		TokenRequisicao.post(callbackTokenPOST(), mSenha, mUsuario);
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

	private void iterarFormulario() {
		mSenha = mInputSenha.getText().toString();
		mUsuario = mInputUsuario.getText().toString();
	}

	private void ocultarProgresso() {
		mButtonAutenticacao.setVisibility(View.VISIBLE);
		mProgressBarAutenticacao.setVisibility(View.GONE);
	}
}
