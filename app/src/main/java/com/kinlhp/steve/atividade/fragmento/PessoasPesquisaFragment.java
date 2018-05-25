package com.kinlhp.steve.atividade.fragmento;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.adaptador.AdaptadorRecyclerPessoas;
import com.kinlhp.steve.dominio.Email;
import com.kinlhp.steve.dominio.Endereco;
import com.kinlhp.steve.dominio.Pessoa;
import com.kinlhp.steve.dominio.Telefone;
import com.kinlhp.steve.dominio.Uf;
import com.kinlhp.steve.dto.EmailDTO;
import com.kinlhp.steve.dto.EnderecoDTO;
import com.kinlhp.steve.dto.PessoaDTO;
import com.kinlhp.steve.dto.TelefoneDTO;
import com.kinlhp.steve.dto.UfDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.mapeamento.EmailMapeamento;
import com.kinlhp.steve.mapeamento.EnderecoMapeamento;
import com.kinlhp.steve.mapeamento.PessoaMapeamento;
import com.kinlhp.steve.mapeamento.TelefoneMapeamento;
import com.kinlhp.steve.mapeamento.UfMapeamento;
import com.kinlhp.steve.requisicao.EnderecoRequisicao;
import com.kinlhp.steve.requisicao.Falha;
import com.kinlhp.steve.requisicao.PessoaRequisicao;
import com.kinlhp.steve.resposta.Colecao;
import com.kinlhp.steve.resposta.ColecaoCallback;
import com.kinlhp.steve.resposta.ItemCallback;
import com.kinlhp.steve.resposta.Links;
import com.kinlhp.steve.util.Parametro;
import com.kinlhp.steve.util.Teclado;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class PessoasPesquisaFragment extends Fragment
		implements AdaptadorRecyclerPessoas.OnItemClickListener,
		AdaptadorRecyclerPessoas.OnItemLongClickListener,
		MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener,
		Serializable {
	private static final long serialVersionUID = 7719005372445071516L;
	private static final String LINKS = "_links";
	private static final String PAGINA_0 =
			"pessoas?sort=nomeRazao,asc&page=0&size=20";
	private static final String PESSOAS = "pessoas";
	private AdaptadorRecyclerPessoas mAdaptadorPessoas;
	private ArrayList<Pessoa> mPessoas = new ArrayList<>();
	private Pessoa mPessoaSelecionada;
	private Links mLinks;
	private OnLongoPessoaSelecionadoListener mOnLongoPessoaSelecionadoListener;
	private OnPessoaSelecionadoListener mOnPessoaSelecionadoListener;
	private int mTarefasPendentes;
	private View mViewSelecionada;

	private AppCompatTextView mLabel0Registros;
	private ProgressBar mProgressBarConsumirPessoasPaginado;
	private RecyclerView mRecyclerPessoas;

	/**
	 * Construtor padrão é obrigatório
	 */
	public PessoasPesquisaFragment() {
	}

	public static PessoasPesquisaFragment newInstance() {
		PessoasPesquisaFragment fragmento = new PessoasPesquisaFragment();
		Bundle argumentos = new Bundle();
		fragmento.setArguments(argumentos);
		return fragmento;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(this);
		searchView.setQueryHint(getString(R.string.app_search));
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_pessoas_pesquisa, container, false);
		mLabel0Registros = view.findViewById(R.id.label_0_registros);
		mProgressBarConsumirPessoasPaginado = view
				.findViewById(R.id.progress_bar_consumir_pessoas_paginado);
		mRecyclerPessoas = view.findViewById(R.id.recycler_pessoas);

		mRecyclerPessoas.setHasFixedSize(true);
		mAdaptadorPessoas = new AdaptadorRecyclerPessoas(mPessoas);
		mAdaptadorPessoas.setOnItemClickListener(this);
		mAdaptadorPessoas.setOnItemLongClickListener(this);
		mRecyclerPessoas.setAdapter(mAdaptadorPessoas);
		RecyclerView.LayoutManager gerenciador =
				new LinearLayoutManager(getActivity());
		mRecyclerPessoas.setLayoutManager(gerenciador);

		mRecyclerPessoas.addOnScrollListener(new OnPessoaScrollListener());

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onItemClick(View view, int posicao) {
		mPessoaSelecionada = mPessoas.get(posicao);
		mViewSelecionada = view;
		consumirPessoaGETEmails();
		consumirPessoaGETEnderecos();
		consumirPessoaGETTelefones();
	}

	@Override
	public void onItemLongClickListener(View view, int posicao) {
		mPessoaSelecionada = mPessoas.get(posicao);
		mViewSelecionada = view;
		consumirPessoaGETEmails();
		consumirPessoaGETEnderecos();
		consumirPessoaGETTelefones();
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem menuItem) {
		return true;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem menuItem) {
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		StringBuilder url =
				new StringBuilder(getString(R.string.requisicao_url_base))
						.append("pessoas/")
						.append("search/")
						.append("cnpjCpf-nomeRazao-fantasiaSobrenome")
						.append("?cnpjCpf=").append(query)
						.append("&nomeRazao=").append(query)
						.append("&fantasiaSobrenome=").append(query)
						.append("&sort=nomeRazao,asc")
						.append("&page=0&size=20");
		HRef pagina0 = new HRef(url.toString());
		consumirPessoasGETPesquisaPaginado(pagina0);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.pessoas_pesquisa_titulo);
		if (mPessoas.isEmpty()) {
			String url = getString(R.string.requisicao_url_base)
					.concat(PAGINA_0);
			HRef pagina0 = new HRef(url);
			consumirPessoasGETPaginado(pagina0);
		}
		alternarLabel0Registros();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(LINKS, mLinks);
		outState.putSerializable(PESSOAS, mPessoas);
	}

	private void addPessoa(@NonNull Pessoa pessoa) {
		if (pessoa.getId().compareTo(BigInteger.ZERO) > 0) {
			mPessoas.add(pessoa);
			int indice = mPessoas.indexOf(pessoa);
			mAdaptadorPessoas.notifyItemInserted(indice);
		}
		alternarLabel0Registros();
	}

	private void alternarLabel0Registros() {
		mLabel0Registros.setVisibility(mPessoas.isEmpty()
				? View.VISIBLE : View.GONE);
	}

	private ItemCallback<UfDTO> callbackEnderecoGETUf(@NonNull Endereco endereco) {
		return new ItemCallback<UfDTO>() {

			@Override
			public void onFailure(@NonNull Call<UfDTO> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<UfDTO> chamada,
			                       @NonNull Response<UfDTO> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					UfDTO dto = resposta.body();
					Uf uf = UfMapeamento.paraDominio(dto);
					endereco.setUf(uf);
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, true);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<EmailDTO>> callbackPessoaGETEmails() {
		return new ColecaoCallback<Colecao<EmailDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<EmailDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<EmailDTO>> chamada,
			                       @NonNull Response<Colecao<EmailDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					Set<EmailDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Email> emails = EmailMapeamento
							.paraDominios(dtos, mPessoaSelecionada);
					mPessoaSelecionada.getEmails().addAll(emails);
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, true);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<EnderecoDTO>> callbackPessoaGETEnderecos() {
		return new ColecaoCallback<Colecao<EnderecoDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<EnderecoDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<EnderecoDTO>> chamada,
			                       @NonNull Response<Colecao<EnderecoDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					Set<EnderecoDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Endereco> enderecos = EnderecoMapeamento
							.paraDominios(dtos, mPessoaSelecionada);
					mPessoaSelecionada.getEnderecos().addAll(enderecos);
					for (Endereco endereco : enderecos) {
						consumirEnderecoGETUf(endereco);
					}
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, true);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<TelefoneDTO>> callbackPessoaGETTelefones() {
		return new ColecaoCallback<Colecao<TelefoneDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<TelefoneDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<TelefoneDTO>> chamada,
			                       @NonNull Response<Colecao<TelefoneDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					Set<TelefoneDTO> dtos = resposta.body().getEmbedded()
							.getDtos();
					Set<Telefone> telefones = TelefoneMapeamento
							.paraDominios(dtos, mPessoaSelecionada);
					mPessoaSelecionada.getTelefones().addAll(telefones);
					ocultarProgresso(mProgressBarConsumirPessoasPaginado, true);
				}
			}
		};
	}

	private ColecaoCallback<Colecao<PessoaDTO>> callbackPessoasGETPaginado() {
		return new ColecaoCallback<Colecao<PessoaDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<PessoaDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<PessoaDTO>> chamada,
			                       @NonNull Response<Colecao<PessoaDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					Colecao<PessoaDTO> colecao = resposta.body();
					Set<PessoaDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Pessoa> pessoas = PessoaMapeamento.paraDominios(dtos);
					for (Pessoa pessoa : pessoas) {
						addPessoa(pessoa);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
			}
		};
	}

	private ColecaoCallback<Colecao<PessoaDTO>> callbackPessoasGETPesquisaPaginado() {
		return new ColecaoCallback<Colecao<PessoaDTO>>() {

			@Override
			public void onFailure(@NonNull Call<Colecao<PessoaDTO>> chamada,
			                      @NonNull Throwable causa) {
				--mTarefasPendentes;
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
				Falha.tratar(mProgressBarConsumirPessoasPaginado, causa);
			}

			@Override
			public void onResponse(@NonNull Call<Colecao<PessoaDTO>> chamada,
			                       @NonNull Response<Colecao<PessoaDTO>> resposta) {
				--mTarefasPendentes;
				if (!resposta.isSuccessful()) {
					Falha.tratar(mProgressBarConsumirPessoasPaginado, resposta);
				} else {
					Colecao<PessoaDTO> colecao = resposta.body();
					Set<PessoaDTO> dtos = colecao.getEmbedded().getDtos();
					Set<Pessoa> pessoas = PessoaMapeamento.paraDominios(dtos);
					for (Pessoa pessoa : pessoas) {
						addPessoa(pessoa);
					}
					mLinks = colecao.getLinks();
				}
				ocultarProgresso(mProgressBarConsumirPessoasPaginado, false);
			}
		};
	}

	private void consumirEnderecoGETUf(@NonNull Endereco endereco) {
		// TODO: 9/11/17 corrigir hard-coded
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("enderecos/%d/uf");
		HRef href = new HRef(String.format(url, endereco.getId()));
		++mTarefasPendentes;
		EnderecoRequisicao.getUf(callbackEnderecoGETUf(endereco), href);
	}

	private void consumirPessoaGETEmails() {
		exibirProgresso(mProgressBarConsumirPessoasPaginado);
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d/emails");
		HRef href = new HRef(String.format(url, mPessoaSelecionada.getId()));
		++mTarefasPendentes;
		PessoaRequisicao.getEmails(callbackPessoaGETEmails(), href);
	}

	private void consumirPessoaGETEnderecos() {
		exibirProgresso(mProgressBarConsumirPessoasPaginado);
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d/enderecos");
		HRef href = new HRef(String.format(url, mPessoaSelecionada.getId()));
		++mTarefasPendentes;
		PessoaRequisicao.getEnderecos(callbackPessoaGETEnderecos(), href);
	}

	private void consumirPessoaGETTelefones() {
		exibirProgresso(mProgressBarConsumirPessoasPaginado);
		String url = Parametro.get(Parametro.Chave.URL_BASE).toString()
				.concat("pessoas/%d/telefones");
		HRef href = new HRef(String.format(url, mPessoaSelecionada.getId()));
		++mTarefasPendentes;
		PessoaRequisicao.getTelefones(callbackPessoaGETTelefones(), href);
	}

	private void consumirPessoasGETPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirPessoasPaginado);
		exibirProgresso(mProgressBarConsumirPessoasPaginado);
		mAdaptadorPessoas.notifyItemRangeRemoved(0, mPessoas.size());
		++mTarefasPendentes;
		PessoaRequisicao.getPaginado(callbackPessoasGETPaginado(), href);
	}

	private void consumirPessoasGETPesquisaPaginado(@NonNull HRef href) {
		mTarefasPendentes = 0;
		Teclado.ocultar(getActivity(), mProgressBarConsumirPessoasPaginado);
		exibirProgresso(mProgressBarConsumirPessoasPaginado);
		mAdaptadorPessoas.notifyItemRangeRemoved(0, mPessoas.size());
		mPessoas.clear();
		mLinks = null;
		++mTarefasPendentes;
		PessoaRequisicao.getPaginado(callbackPessoasGETPesquisaPaginado(), href);
	}

	private void exibirProgresso(@NonNull ProgressBar progresso) {
		progresso.setVisibility(View.VISIBLE);
	}

	private void ocultarProgresso(@NonNull ProgressBar progresso,
	                              boolean chamarOuvinte) {
		if (mTarefasPendentes <= 0) {
			alternarLabel0Registros();
			progresso.setVisibility(View.GONE);
			if (chamarOuvinte) {
				// TODO: 9/18/17 definir implementações diferentes para clique curto e longo
				if (mOnLongoPessoaSelecionadoListener != null) {
					mOnLongoPessoaSelecionadoListener
							.onLongoPessoaSelecionado(mViewSelecionada, mPessoaSelecionada);
				}
				if (mOnPessoaSelecionadoListener != null) {
					mOnPessoaSelecionadoListener
							.onPessoaSelecionado(mViewSelecionada, mPessoaSelecionada);
				}
				getActivity().onBackPressed();
			}
		}
	}

	public void setOnLongoPessoaSelecionadoListener(@Nullable OnLongoPessoaSelecionadoListener ouvinte) {
		mOnLongoPessoaSelecionadoListener = ouvinte;
	}

	public void setOnPessoaSelecionadoListener(@Nullable OnPessoaSelecionadoListener ouvinte) {
		mOnPessoaSelecionadoListener = ouvinte;
	}

	public interface OnLongoPessoaSelecionadoListener {

		void onLongoPessoaSelecionado(@NonNull View view, @NonNull Pessoa pessoa);
	}

	public interface OnPessoaSelecionadoListener {

		void onPessoaSelecionado(@NonNull View view, @NonNull Pessoa pessoa);
	}

	private final class OnPessoaScrollListener
			extends RecyclerView.OnScrollListener implements Serializable {
		private static final long serialVersionUID = -8804582747472868549L;

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			LinearLayoutManager gerenciador =
					(LinearLayoutManager) mRecyclerPessoas.getLayoutManager();
			if (mPessoas.size() == (gerenciador.findLastCompletelyVisibleItemPosition() + 1)) {
				if (mLinks != null && mLinks.getNext() != null) {
					consumirPessoasGETPaginado(mLinks.getNext());
				}
			}
		}
	}
}
