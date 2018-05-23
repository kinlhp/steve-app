package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 9/28/17.
 */
public class ContaReceberDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -5910409999146440550L;

	@SerializedName(value = "condicaoPagamento")
	private HRef condicaoPagamento;

	@SerializedName(value = "contareceber")
	private HRef contaReceber;

	@SerializedName(value = "movimentacoes")
	private HRef movimentacoes;

	@SerializedName(value = "ordem")
	private HRef ordem;

	@SerializedName(value = "sacado")
	private HRef sacado;
}
