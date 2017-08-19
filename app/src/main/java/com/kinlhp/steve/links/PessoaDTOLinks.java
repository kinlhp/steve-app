package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 8/14/17.
 */
public class PessoaDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -8084306742716809928L;

	@SerializedName(value = "emails")
	private HRef emails;

	@SerializedName(value = "enderecos")
	private HRef enderecos;

	@SerializedName(value = "telefones")
	private HRef telefones;
}
