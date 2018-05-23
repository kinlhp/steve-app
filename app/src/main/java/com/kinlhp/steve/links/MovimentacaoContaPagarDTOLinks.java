package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 5/22/18.
 */
public class MovimentacaoContaPagarDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 8419267389160801176L;

	@SerializedName(value = "condicaoPagamento")
	private HRef condicaoPagamento;

	@SerializedName(value = "contaPagar")
	private HRef contaPagar;

	@SerializedName(value = "movimentacaocontapagar")
	private HRef movimentacaoContaPagar;
}
