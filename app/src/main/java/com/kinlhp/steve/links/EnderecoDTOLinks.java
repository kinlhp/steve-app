package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class EnderecoDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 8112709475084130560L;

	@SerializedName(value = "endereco")
	private HRef endereco;

	@SerializedName(value = "pessoa")
	private HRef pessoa;

	@SerializedName(value = "uf")
	private HRef uf;
}
