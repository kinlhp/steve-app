package com.kinlhp.steve.dominio;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 9/25/17.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(
		callSuper = false,
		of = {"formaPagamento", "periodoEntreParcelas", "quantidadeParcelas"}
)
@Getter
@NoArgsConstructor
@Setter
public class CondicaoPagamento extends Dominio<BigInteger> {
	private static final long serialVersionUID = 1264322968437555736L;
	private String descricao;
	private FormaPagamento formaPagamento;
	@Builder.Default
	private BigInteger periodoEntreParcelas = BigInteger.ZERO;
	@Builder.Default
	private BigInteger quantidadeParcelas = BigInteger.ZERO;

	@Override
	public String toString() {
		return descricao;
	}
}
