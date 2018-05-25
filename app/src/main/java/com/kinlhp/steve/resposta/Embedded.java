package com.kinlhp.steve.resposta;

import com.google.gson.annotations.SerializedName;
import com.kinlhp.steve.dto.DTO;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;

/**
 * Created by kin on 8/14/17.
 */
@Getter
public class Embedded<T extends DTO> implements Serializable {
	private static final long serialVersionUID = -2983676878419872080L;

	@SerializedName(
			alternate = {
					"condicoesPagamento", "contasPagar", "contasReceber",
					"credenciais", "emails", "enderecos", "formasPagamento",
					"itensOrdemServico", "movimentacoesContaPagar",
					"movimentacoesContaReceber", "ordens", "pessoas", "servicos",
					"telefones", "ufs"
			},
			value = "dtos"
	)
	private Set<T> dtos = new LinkedHashSet<>();
}
