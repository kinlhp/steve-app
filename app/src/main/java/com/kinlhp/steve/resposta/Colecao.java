package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.dto.DTO;

import java.io.Serializable;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class Colecao<T extends DTO> implements Serializable {
	private static final long serialVersionUID = 3070030990239364562L;

	@SerializedName(value = "_embedded")
	private Embedded<T> embedded;

	@SerializedName(value = "_links")
	private Links links;

	@SerializedName(value = "page")
	private Page page;
}
