package com.kinlhp.steve.dominio;

import java.math.BigInteger;

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
	private static final long serialVersionUID = -6636990457235789054L;
	private String descricao;
}
