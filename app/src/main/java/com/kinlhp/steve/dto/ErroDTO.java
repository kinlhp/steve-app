package com.kinlhp.steve.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class ErroDTO implements Serializable {
	private static final long serialVersionUID = -5748375480774644837L;

	@SerializedName(value = "timestamp")
	private Date horaOcorrencia;

	@SerializedName(value = "status")
	private int codigoHttp;

	@SerializedName(value = "error")
	private String descricaoCodigoHttp;

	@SerializedName(value = "exception")
	private String excecao;

	@SerializedName(value = "message")
	private String mensagem;

	@SerializedName(value = "path")
	private String caminho;
}
