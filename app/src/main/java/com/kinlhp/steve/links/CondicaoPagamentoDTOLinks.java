package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 9/25/17.
 */
public class CondicaoPagamentoDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -825522938886244612L;

	@SerializedName(value = "condicaopagamento")
	private HRef condicaoPagamento;

	@SerializedName(value = "formaPagamento")
	private HRef formaPagamento;
}
