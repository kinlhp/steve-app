package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.dto.DTO;

import java.io.Serializable;

/**
 * Created by kin on 8/14/17.
 */
public class Resposta<T extends DTO> implements Serializable {
	private static final long serialVersionUID = 4354428219962895796L;

	@SerializedName(value = "_embedded")
	private Embedded<T> embedded;

	@SerializedName(value = "_links")
	private Links links;

	@SerializedName(value = "page")
	private Page page;
}
