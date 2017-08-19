package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class CredencialDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 1475013743792297073L;

	@SerializedName(value = "funcionario")
	private HRef funcionario;
}
