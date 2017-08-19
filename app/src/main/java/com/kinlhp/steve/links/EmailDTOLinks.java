package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

/**
 * Created by kin on 8/14/17.
 */
public class EmailDTOLinks extends DTOLinks {
	private static final long serialVersionUID = 5129996619017711721L;

	@SerializedName(value = "email")
	private HRef email;

	@SerializedName(value = "pessoa")
	private HRef pessoa;
}
