package com.kinlhp.steve.dominio;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 9/19/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = {"descricao"})
@Getter
@NoArgsConstructor
@Setter
public class FormaPagamento extends Dominio<BigInteger> {
	private static final long serialVersionUID = 7158932283229250348L;
	@Builder.Default
	private Set<CondicaoPagamento> condicoesPagamento = new LinkedHashSet<>();
	private String descricao;
}
