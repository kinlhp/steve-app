package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 8/14/17.
 */
public class TelefoneDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 4754882635188467120L;

	@SerializedName(value = "pessoa")
	private HRef pessoa;

	@SerializedName(value = "telefone")
	private HRef telefone;
}
