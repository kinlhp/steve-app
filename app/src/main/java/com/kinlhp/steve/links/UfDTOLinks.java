package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class UfDTOLinks extends DTOLinks {
	private static final long serialVersionUID = -5726666685126842213L;

	@SerializedName(value = "uf")
	private HRef uf;
}
