package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 9/21/17.
 */
@Getter
public class OrdemDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -2267975978591548149L;

	@SerializedName(value = "cliente")
	private HRef cliente;

	@SerializedName(value = "itens")
	private HRef itens;

	@SerializedName(value = "ordem")
	private HRef ordem;
}
