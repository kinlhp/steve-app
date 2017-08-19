package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kin on 8/16/17.
 */
class Page implements Serializable {
	private static final long serialVersionUID = 5289295057748079315L;

	@SerializedName(value = "number")
	private Integer number;

	@SerializedName(value = "size")
	private Integer size;

	@SerializedName(value = "totalElements")
	private Integer totalElements;

	@SerializedName(value = "totalPages")
	private Integer totalPages;
}
