package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kin on 5/12/18.
 */
@Builder
@Getter
@Setter
public class TokenDTO extends DTO {
	private static final long serialVersionUID = -8529721710702827170L;

	@SerializedName(value = "access_token")
	private String accessToken;

	@SerializedName(value = "expires_in")
	private Integer expiresIn;

	@SerializedName(value = "jti")
	private String jti;

	@SerializedName(value = "refresh_token")
	private String refreshToken;

	@SerializedName(value = "scope")
	private String scope;

	@SerializedName(value = "token_type")
	private String tokenType;
}
