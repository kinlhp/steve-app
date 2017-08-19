package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 8/14/17.
 */
public class EnderecoDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -2393482629958944402L;

	@SerializedName(value = "endereco")
	private HRef endereco;

	@SerializedName(value = "pessoa")
	private HRef pessoa;

	@SerializedName(value = "uf")
	private HRef uf;
}
