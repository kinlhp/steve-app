package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public abstract class DTO implements Serializable {
	private static final long serialVersionUID = -8405748333630231105L;

	@SerializedName(value = "dataCriacao")
	private Date dataCriacao;

	@SerializedName(value = "dataUltimaAlteracao")
	private Date dataUltimaAlteracao;
}
