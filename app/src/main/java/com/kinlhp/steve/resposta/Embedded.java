package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.dto.DTO;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by kin on 8/14/17.
 */
class Embedded<T extends DTO> implements Serializable {
	private static final long serialVersionUID = -4420701579771723572L;

	@SerializedName(alternate = {"credenciais"}, value = "dtos")
	private Set<T> dtos;
}
