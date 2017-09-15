package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;

import lombok.Getter;

/**
 * Created by kin on 8/16/17.
 */
@Getter
public class Links implements Serializable {
	private static final long serialVersionUID = -8368998941252368568L;

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

	@SerializedName(value = "search")
	private HRef search;

	@SerializedName(value = "self")
	private HRef self;
}
