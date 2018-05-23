package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 5/21/18.
 */
public class MovimentacaoContaReceberDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 6400448946507490667L;

	@SerializedName(value = "condicaoPagamento")
	private HRef condicaoPagamento;

	@SerializedName(value = "contaReceber")
	private HRef contaReceber;

	@SerializedName(value = "movimentacaocontareceber")
	private HRef movimentacaoContaReceber;
}
