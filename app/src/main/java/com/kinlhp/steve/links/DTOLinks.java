package com.kinlhp.steve.links;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
abstract class DTOLinks implements Serializable {
	private static final long serialVersionUID = -6422911366375950504L;

	@SerializedName(value = "self")
	private HRef self;

	@SerializedName(value = "usuarioAlteracao")
	private HRef usuarioAlteracao;

	@SerializedName(value = "usuarioCriacao")
	private HRef usuarioCriacao;
}
