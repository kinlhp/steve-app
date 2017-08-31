package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
@Setter
abstract class DTOLinks implements Serializable {
	private static final long serialVersionUID = 8530540004705019797L;

	@SerializedName(value = "self")
	private HRef self;

	@SerializedName(value = "usuarioAlteracao")
	private HRef usuarioAlteracao;

	@SerializedName(value = "usuarioCriacao")
	private HRef usuarioCriacao;
}
