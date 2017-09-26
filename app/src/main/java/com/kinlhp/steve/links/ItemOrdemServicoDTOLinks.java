package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 9/21/17.
 */
public class ItemOrdemServicoDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 3325287851466875135L;

	@SerializedName(value = "itemordemservico")
	private HRef itemOrdemServico;

	@SerializedName(value = "ordem")
	private HRef ordem;

	@SerializedName(value = "servico")
	private HRef servico;
}
