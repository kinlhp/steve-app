package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 9/20/17.
 */
@Getter
public class ServicoDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -8831324849224690457L;

	@SerializedName(value = "servico")
	private HRef servico;
}
