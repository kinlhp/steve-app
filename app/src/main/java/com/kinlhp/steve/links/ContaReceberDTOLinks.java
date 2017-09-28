package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 9/28/17.
 */
public class ContaReceberDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 7717885208253103173L;

	@SerializedName(value = "condicaoPagamento")
	private HRef condicaoPagamento;

	@SerializedName(value = "contareceber")
	private HRef contaReceber;

	@SerializedName(value = "ordem")
	private HRef ordem;

	@SerializedName(value = "sacado")
	private HRef sacado;
}
