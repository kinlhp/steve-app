package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 9/20/17.
 */
@Getter
public class FormaPagamentoDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -7880269181482687394L;

	@SerializedName(value = "condicoesPagamento")
	private HRef condicoesPagamento;

	@SerializedName(value = "formapagamento")
	private HRef formaPagamento;
}
