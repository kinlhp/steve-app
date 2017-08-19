package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;

/**
 * Created by kin on 8/16/17.
 */
class Links implements Serializable {
	private static final long serialVersionUID = -467019849185166625L;

	@SerializedName(value = "first")
	private HRef first;

	@SerializedName(value = "last")
	private HRef last;

	@SerializedName(value = "next")
	private HRef next;

	@SerializedName(value = "prev")
	private HRef prev;

	@SerializedName(value = "profile")
	private HRef profile;

	@SerializedName(value = "self")
	private HRef self;
}
