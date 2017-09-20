package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class CredencialDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 7100404389330472423L;

	@SerializedName(value = "credencial")
	private HRef credencial;

	@SerializedName(value = "funcionario")
	private HRef funcionario;
}
