package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorSpinner;
import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dominio.Telefone;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.mapeamento.TelefoneMapeamento;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.TelefoneRequisicao;
import com.kinlhp.steve.resposta.VazioCallback;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TelefoneCadastroFragment extends Fragment
		implements View.OnClickListener, View.OnFocusChangeListener,
		Serializable {
	private static final long serialVersionUID = -5340911769514830158L;
	private static final String TELEFONE = "telefone";
	private static final String TELEFONE_AUXILIAR = "telefoneAuxiliar";
	private AdaptadorSpinner<Telefone.Tipo> mAdaptadorTipos;
	private OnTelefoneAdicionadoListener mOnTelefoneAdicionadoListener;
	private int mTarefasPendentes;
	private Telefone mTelefone;
	private Telefone mTelefoneAuxiliar;
	private ArrayList<Telefone.Tipo> mTipos;

	private AppCompatButton mButtonAdicionar;
	private TextInputEditText mInputNomeContato;
	private TextInputEditText mInputNumero;
	private TextInputLayout mLabelNumero;
	private ProgressBar mProgressBarAdicionar;
	private ScrollView mScrollTelefoneCadastro;
	private AppCompatSpinner mSpinnerTipo;

	/**
	 * Construtor padrão é obrigatório
	 */
	public TelefoneCadastroFragment() {
	}

	public static TelefoneCadastroFragment newInstance(@NonNull Telefone telefone) {
		TelefoneCadastroFragment fragmento = new TelefoneCadastroFragment();
		Bundle argumentos = new Bundle();
		argumentos.putSerializable(TELEFONE, telefone);
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_adicionar:
				if (isFormularioValido()) {
					submeterFormulario();
				} else {
					mScrollTelefoneCadastro.fullScroll(View.FOCUS_UP);
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mTelefoneAuxiliar = (Telefone) savedInstanceState
					.getSerializable(TELEFONE_AUXILIAR);
		}
		if (getArguments() != null) {
			mTelefone = (Telefone) getArguments().getSerializable(TELEFONE);
		}
		if (mTelefoneAuxiliar == null) {
			mTelefoneAuxiliar = Telefone.builder().tipo(null).build();
			transcreverTelefone();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_telefone_cadastro, container, false);
		mScrollTelefoneCadastro = (ScrollView) view;
		mButtonAdicionar = view.findViewById(R.id.button_adicionar);
		mInputNomeContato = view.findViewById(R.id.input_nome_contato);
		mInputNumero = view.findViewById(R.id.input_numero);
		mLabelNumero = view.findViewById(R.id.label_numero);
		mProgressBarAdicionar = view.findViewById(R.id.progress_bar_adicionar);
		mSpinnerTipo = view.findViewById(R.id.spinner_tipo);

		mTipos = new ArrayList<>(Arrays.asList(Telefone.Tipo.values()));
		mAdaptadorTipos = new AdaptadorSpinner<>(getActivity(), mTipos);
		mSpinnerTipo.setAdapter(mAdaptadorTipos);

		mButtonAdicionar.setOnClickListener(this);
		mInputNumero.setOnFocusChangeListener(this);

		mInputNumero.requestFocus();
		mScrollTelefoneCadastro.fullScroll(View.FOCUS_UP);

		return view;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		switch (view.getId()) {
			case R.id.input_numero:
				if (!focused) {
					isNumeroValido();
				}
				break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		iterarFormulario();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.telefone_cadastro_titulo);
		limitarTiposDisponiveis();
		preencherFormulario();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(TELEFONE, mTelefone);
		outState.putSerializable(TELEFONE_AUXILIAR, mTelefoneAuxiliar);
	}

	private void alternarButtonAdicionar() {
		/*
		Método contains não se comparta corretamente
		 */
//		if (mTelefone.getPessoa().getTelefones().contains(mTelefone)) {
//			mButtonAdicionar.setHint(mTelefone.getId() == null
//					? R.string.telefone_cadastro_button_alterar_hint
//					: R.string.telefone_cadastro_button_salvar_hint);
//		}
		// TODO: 9/15/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<Telefone> telefones =
				new ArrayList<>(mTelefone.getPessoa().getTelefones());
		if (telefones.contains(mTelefone)) {
			mButtonAdicionar.setHint(mTelefone.getId() == null
					? R.string.telefone_cadastro_button_alterar_hint
					: R.string.telefone_cadastro_button_salvar_hint);
		}
		Credencial credencialLogado = (Credencial)
				Parametro.get(Parametro.Chave.CREDENCIAL);
		if (mTelefone.getPessoa().isPerfilUsuario()
				&& !credencialLogado.isPerfilAdministrador()) {
			mButtonAdicionar.setEnabled(false);
		} else {
			mButtonAdicionar.setEnabled(true);
		}
	}

	private VazioCallback callbackTelefonePUT() {
		return new VazioCallback() {

			@Override
			public void onFailure(@NonNull Call<Void> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
				Falha.tratar(mButtonAdicionar, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Void> chamada,
			                       @NonNull Response<Void> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mButtonAdicionar, resposta);
				} else {
					transcreverTelefoneAuxiliar();
					if (mOnTelefoneAdicionadoListener != null) {
						mOnTelefoneAdicionadoListener
								.onTelefoneAdicionado(mButtonAdicionar, mTelefone);
					}
					getActivity().onBackPressed();
				}
				ocultarProgresso(mProgressBarAdicionar, mButtonAdicionar);
			}
		};
	}

	private void consumirTelefonePUT() {
		mTarefasPendentes = 0;
		exibirProgresso(mProgressBarAdicionar, mButtonAdicionar);
		Teclado.ocultar(getActivity(), mButtonAdicionar);
		TelefoneDTO dto = TelefoneMapeamento.paraDTO(mTelefoneAuxiliar);
		++mTarefasPendentes;
		TelefoneRequisicao.put(callbackTelefonePUT(), mTelefone.getId(), dto);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso,
	                             @Nullable View view) {
		progresso.setVisibility(View.VISIBLE);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	private boolean isFormularioValido() {
		return isNumeroValido();
	}

	private boolean isNumeroValido() {
		if (TextUtils.isEmpty(mInputNumero.getText())) {
			mLabelNumero.setError(getString(R.string.input_obrigatorio));
			return false;
		}
		if (!TextUtils.isDigitsOnly(mInputNumero.getText())
				|| TextUtils.getTrimmedLength(mInputNumero.getText()) < 8) {
			mLabelNumero.setError(getString(R.string.input_invalido));
			return false;
		}
		mLabelNumero.setError(null);
		mLabelNumero.setErrorEnabled(false);
		return true;
	}

	private void iterarFormulario() {
		mTelefoneAuxiliar
				.setTipo((Telefone.Tipo) mSpinnerTipo.getSelectedItem());
		mTelefoneAuxiliar.setNumero(mInputNumero.getText().toString());
		mTelefoneAuxiliar
				.setNomeContato(mInputNomeContato.getText().toString());
	}

	private void limitarTiposDisponiveis() {
		for (Telefone telefone : mTelefone.getPessoa().getTelefones()) {
			if (!telefone.equals(mTelefone)
					|| TextUtils.isEmpty(mTelefone.getNumero())) {
				mAdaptadorTipos.remove(telefone.getTipo());
			}
		}
		mAdaptadorTipos.notifyDataSetChanged();
	}

	private void limparErros() {
		mLabelNumero.setError(null);
		mLabelNumero.setErrorEnabled(false);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              @Nullable View view) {
		if (mTarefasPendentes <= 0) {
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
			progresso.setVisibility(View.GONE);
		}
	}

	private void preencherFormulario() {
		limparErros();
		mSpinnerTipo.setSelection(mTelefoneAuxiliar.getTipo() == null
				? 0 : mAdaptadorTipos.getPosition(mTelefoneAuxiliar.getTipo()));
		mInputNumero.setText(mTelefoneAuxiliar.getNumero() == null
				? "" : mTelefoneAuxiliar.getNumero());
		mInputNomeContato.setText(mTelefoneAuxiliar.getNomeContato() == null
				? "" : mTelefoneAuxiliar.getNomeContato());
		alternarButtonAdicionar();
	}

	public void setOnTelefoneAdicionadoListener(@Nullable OnTelefoneAdicionadoListener ouvinte) {
		mOnTelefoneAdicionadoListener = ouvinte;
	}

	public void setTelefone(@NonNull Telefone telefone) {
		mTelefone = telefone;
		if (getArguments() != null) {
			getArguments().putSerializable(TELEFONE, mTelefone);
		}

		mTelefone = telefone;
		if (getArguments() != null) {
			getArguments().putSerializable(TELEFONE, mTelefone);
		}
		if (mTelefoneAuxiliar != null) {
			transcreverTelefone();
		}
	}

	private void submeterFormulario() {
		iterarFormulario();
		if (mTelefone.getId() != null) {
			consumirTelefonePUT();
		} else {
			transcreverTelefoneAuxiliar();
			if (mOnTelefoneAdicionadoListener != null) {
				mOnTelefoneAdicionadoListener
						.onTelefoneAdicionado(mButtonAdicionar, mTelefone);
			}
			getActivity().onBackPressed();
		}
	}

	private void transcreverTelefone() {
		mTelefoneAuxiliar.setId(mTelefone.getId());
		mTelefoneAuxiliar.setNumero(mTelefone.getNumero());
		mTelefoneAuxiliar.setNomeContato(mTelefone.getNomeContato());
		mTelefoneAuxiliar.setPessoa(mTelefone.getPessoa());
		mTelefoneAuxiliar.setTipo(mTelefone.getTipo());
	}

	private void transcreverTelefoneAuxiliar() {
		mTelefone.setNumero(mTelefoneAuxiliar.getNumero());
		mTelefone.setNomeContato(mTelefoneAuxiliar.getNomeContato());
		mTelefone.setTipo(mTelefoneAuxiliar.getTipo());
	}

	public interface OnTelefoneAdicionadoListener {

		void onTelefoneAdicionado(@NonNull View view,
		                          @NonNull Telefone telefone);
	}
}
