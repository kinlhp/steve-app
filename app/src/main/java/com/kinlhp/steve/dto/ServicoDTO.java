package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.links.ServicoDTOLinks;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by kin on 9/20/17.
 */
@Builder
@Getter
public class ServicoDTO extends DTO {
	private static final long serialVersionUID = 1370747626106910614L;

	@SerializedName(value = "_links")
	private ServicoDTOLinks links;

	@SerializedName(value = "descricao")
	private String descricao;
}
