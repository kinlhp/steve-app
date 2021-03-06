package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class PessoaDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 7603911971955248894L;

	@SerializedName(value = "emails")
	private HRef emails;

	@SerializedName(value = "enderecos")
	private HRef enderecos;

	@SerializedName(value = "pessoa")
	private HRef pessoa;

	@SerializedName(value = "telefones")
	private HRef telefones;
}
