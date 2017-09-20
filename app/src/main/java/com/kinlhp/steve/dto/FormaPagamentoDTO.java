package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.FormaPagamentoDTOLinks;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 9/20/17.
 */
@Builder
@Getter
public class FormaPagamentoDTO extends DTO {
	private static final long serialVersionUID = -8098569758490795603L;

	@SerializedName(value = "_links")
	private FormaPagamentoDTOLinks links;

	@SerializedName(value = "descricao")
	private String descricao;
}
