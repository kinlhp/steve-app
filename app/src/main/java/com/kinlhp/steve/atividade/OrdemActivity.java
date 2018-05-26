package com.kinlhp.steve.atividade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kinlhp.steve.R;
import com.kinlhp.steve.atividade.fragmento.ItemOrdemServicoCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.ItensOrdemServicoPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.OrdemCadastroFragment;
import com.kinlhp.steve.atividade.fragmento.OrdensPesquisaFragment;
import com.kinlhp.steve.atividade.fragmento.PessoasPesquisaFragment;
import com.kinlhp.steve.dominio.ItemOrdemServico;
import com.kinlhp.steve.dominio.Ordem;
import com.kinlhp.steve.dominio.Pessoa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OrdemActivity extends AppCompatActivity
		implements OrdemCadastroFragment.OnClientesPesquisaListener,
		OrdemCadastroFragment.OnOrdemAdicionadoListener,
		ItemOrdemServicoCadastroFragment.OnItemOrdemServicoAdicionadoListener,
		ItensOrdemServicoPesquisaFragment.OnItemOrdemServicoSelecionadoListener,
		ItensOrdemServicoPesquisaFragment.OnLongoItemOrdemServicoSelecionadoListener,
		OrdemCadastroFragment.OnItensOrdemServicoPesquisaListener,
		OrdemCadastroFragment.OnOrdensPesquisaListener,
		OrdemCadastroFragment.OnReferenciaOrdemAlteradoListener,
		OrdensPesquisaFragment.OnLongoOrdemSelecionadoListener,
		OrdensPesquisaFragment.OnOrdemSelecionadoListener,
		PessoasPesquisaFragment.OnLongoPessoaSelecionadoListener,
		PessoasPesquisaFragment.OnPessoaSelecionadoListener, Serializable {
	private static final long serialVersionUID = -1193108187604909251L;
	private ItemOrdemServicoCadastroFragment mFragmentoItemOrdemServicoCadastro;
	private ItensOrdemServicoPesquisaFragment mFragmentoItensOrdemServicoPesquisa;
	private OrdemCadastroFragment mFragmentoOrdemCadastro;
	private OrdensPesquisaFragment mFragmentoOrdensPesquisa;
	private PessoasPesquisaFragment mFragmentoPessoasPesquisa;
	private Ordem mOrdem = Ordem.builder().build();
	private Bundle mSavedInstanceState;

	@Override
	public void onClientesPesquisa(@NonNull View view) {
		inflarPessoasPesquisa();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ordem);

		Toolbar toolbar = findViewById(R.id.toolbar_ordem);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mSavedInstanceState = savedInstanceState;
		inflarOrdemCadastro();
	}

	@Override
	public void onItemOrdemServicoAdicionado(@NonNull View view,
	                                         @NonNull ItemOrdemServico itemOrdemServico) {
		ArrayList<ItemOrdemServico> itens = new ArrayList<>(mOrdem.getItens());
		boolean contem = false;
		for (ItemOrdemServico item : itens) {
			if (itemOrdemServico == item) {
				contem = true;
				break;
			}
		}
		if (!contem) {
			itens.add(itemOrdemServico);
			if (mFragmentoItensOrdemServicoPesquisa != null) {
				mFragmentoItensOrdemServicoPesquisa
						.addItemOrdemServico(itemOrdemServico);
			}
		}
		mOrdem.getItens().clear();
		mOrdem.getItens().addAll(itens);
	}

	@Override
	public void onItemOrdemServicoSelecionado(@NonNull View view,
	                                          @NonNull ItemOrdemServico itemOrdemServico) {
		inflarItemOrdemServicoCadastro(itemOrdemServico);
	}

	@Override
	public void onItensOrdemServicoPesquisa(@NonNull View view) {
		if (mOrdem.getItens().isEmpty()) {
			ItemOrdemServico itemOrdemServico = ItemOrdemServico.builder()
					.ordem(mOrdem).valorOrcamento(null).valorServico(null)
					.build();
			inflarItemOrdemServicoCadastro(itemOrdemServico);
		} else {
			inflarItensOrdemServicoPesquisa(mOrdem.getItens());
		}
	}

	@Override
	public void onLongoItemOrdemServicoSelecionado(@NonNull View view,
	                                               @NonNull ItemOrdemServico itemOrdemServico) {
		// TODO: 9/13/17 resolver de forma elegante a inconsistência acima (método contains não se comporta corretamente)
		List<ItemOrdemServico> itensOrdemServico =
				new ArrayList<>(itemOrdemServico.getOrdem().getItens());
		if (itensOrdemServico.contains(itemOrdemServico)
				&& itemOrdemServico.getId() == null) {
			itensOrdemServico.remove(itemOrdemServico);
			if (mFragmentoItensOrdemServicoPesquisa != null) {
				mFragmentoItensOrdemServicoPesquisa
						.removeItemOrdemServico(itemOrdemServico);
			}
		}
		mOrdem.getItens().clear();
		mOrdem.getItens().addAll(itensOrdemServico);
	}

	@Override
	public void onLongoOrdemSelecionado(@NonNull View view,
	                                    @NonNull Ordem ordem) {
		// TODO: 9/21/17 definir implementações diferentes para clique curto e longo
		mOrdem = ordem;
		if (mFragmentoOrdemCadastro != null) {
			mFragmentoOrdemCadastro.setOrdem(mOrdem);
		}
	}

	@Override
	public void onLongoPessoaSelecionado(@NonNull View view,
	                                     @NonNull Pessoa pessoa) {
		mOrdem.setCliente(pessoa);
		getSupportFragmentManager().beginTransaction()
				.remove(mFragmentoPessoasPesquisa);
		mFragmentoPessoasPesquisa = null;
	}

	@Override
	public void onOrdemSelecionado(@NonNull View view, @NonNull Ordem ordem) {
		// TODO: 9/21/17 definir implementações diferentes para clique curto e longo
		mOrdem = ordem;
		if (mFragmentoOrdemCadastro != null) {
			mFragmentoOrdemCadastro.setOrdem(mOrdem);
		}
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
	public void onOrdemAdicionado(@NonNull View view, @NonNull Ordem ordem) {
		mOrdem = ordem;
		finish();
	}

	@Override
	public void onOrdensPesquisa(@NonNull View view) {
		inflarOrdensPesquisa();
	}

	@Override
	public void onPessoaSelecionado(@NonNull View view,
	                                @NonNull Pessoa pessoa) {
		mOrdem.setCliente(pessoa);
		getSupportFragmentManager().beginTransaction()
				.remove(mFragmentoPessoasPesquisa);
		mFragmentoPessoasPesquisa = null;
	}

	@Override
	public void onReferenciaOrdemAlterado(@NonNull Ordem novaReferencia) {
		mOrdem = novaReferencia;
	}

	private void inflarItemOrdemServicoCadastro(@NonNull ItemOrdemServico itemOrdemServico) {
		if (itemOrdemServico.getOrdem() == null) {
			itemOrdemServico.setOrdem(mOrdem);
		}
		if (mFragmentoItemOrdemServicoCadastro == null) {
			mFragmentoItemOrdemServicoCadastro =
					ItemOrdemServicoCadastroFragment
							.newInstance(itemOrdemServico);
		} else {
			mFragmentoItemOrdemServicoCadastro
					.setItemOrdemServico(itemOrdemServico);
		}
		mFragmentoItemOrdemServicoCadastro
				.setOnItemOrdemServicoAdicionadoListener(this);
		String tag = getString(R.string.item_ordem_servico_cadastro_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_ordem, mFragmentoItemOrdemServicoCadastro, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarItensOrdemServicoPesquisa(@NonNull Set<ItemOrdemServico> itensOrdemServico) {
		if (mFragmentoItensOrdemServicoPesquisa == null) {
			mFragmentoItensOrdemServicoPesquisa =
					ItensOrdemServicoPesquisaFragment
							.newInstance(new ArrayList<>(itensOrdemServico));
		} else {
			mFragmentoItensOrdemServicoPesquisa
					.setItensOrdemServico(new ArrayList<>(itensOrdemServico));
		}
		mFragmentoItensOrdemServicoPesquisa.setOrdem(mOrdem);
		mFragmentoItensOrdemServicoPesquisa
				.setOnItemOrdemServicoSelecionadoListener(this);
		mFragmentoItensOrdemServicoPesquisa
				.setOnLongoItemOrdemServicoSelecionadoListener(this);
		String tag = getString(R.string.itens_ordem_servico_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_ordem, mFragmentoItensOrdemServicoPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarOrdemCadastro() {
		if (mFragmentoOrdemCadastro == null) {
			mFragmentoOrdemCadastro = OrdemCadastroFragment.newInstance(mOrdem);
		} else {
			mFragmentoOrdemCadastro.setOrdem(mOrdem);
		}
		mFragmentoOrdemCadastro.setOnClientesPesquisaListener(this);
		mFragmentoOrdemCadastro.setOnItensOrdemServicoPesquisaListener(this);
		mFragmentoOrdemCadastro.setOnOrdemAdicionadoListener(this);
		mFragmentoOrdemCadastro.setOnOrdensPesquisaListener(this);
		mFragmentoOrdemCadastro.setOnReferenciaOrdemAlteradoListener(this);
		String tag = getString(R.string.ordem_cadastro_titulo);

		if (mSavedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_ordem, mFragmentoOrdemCadastro, tag)
					.commit();
		}
	}

	private void inflarOrdensPesquisa() {
		if (mFragmentoOrdensPesquisa == null) {
			mFragmentoOrdensPesquisa = OrdensPesquisaFragment.newInstance();
		}
		mFragmentoOrdensPesquisa.setOnLongoOrdemSelecionadoListener(this);
		mFragmentoOrdensPesquisa.setOnOrdemSelecionadoListener(this);
		String tag = getString(R.string.ordens_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_ordem, mFragmentoOrdensPesquisa, tag)
				.addToBackStack(tag).commit();
	}

	private void inflarPessoasPesquisa() {
		if (mFragmentoPessoasPesquisa == null) {
			mFragmentoPessoasPesquisa = PessoasPesquisaFragment.newInstance();
		}
		mFragmentoPessoasPesquisa.setOnLongoPessoaSelecionadoListener(this);
		mFragmentoPessoasPesquisa.setOnPessoaSelecionadoListener(this);
		String tag = getString(R.string.pessoas_pesquisa_titulo);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_ordem, mFragmentoPessoasPesquisa, tag)
				.addToBackStack(tag).commit();
	}
}
