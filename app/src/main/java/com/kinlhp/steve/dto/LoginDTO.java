package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kin on 8/14/17.
 */
@Builder
@Getter
@Setter
public class LoginDTO implements Serializable {
	private static final long serialVersionUID = 8911698345425633368L;

	@SerializedName(value = "senha")
	private String senha;

	@SerializedName(value = "usuario")
	private String usuario;
}
