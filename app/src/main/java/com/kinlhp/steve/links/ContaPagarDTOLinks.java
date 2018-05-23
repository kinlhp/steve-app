package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 10/10/17.
 */
public class ContaPagarDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -7082813594197134749L;

	@SerializedName(value = "contapagar")
	private HRef contaPagar;

	@SerializedName(value = "cedente")
	private HRef cedente;

	@SerializedName(value = "movimentacoes")
	private HRef movimentacoes;
}
