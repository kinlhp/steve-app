package com.kinlhp.steve.dominio;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kin on 8/13/17.
 */
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Getter
@NoArgsConstructor
@Setter
public abstract class Dominio<ID extends Serializable> implements Serializable {
	private static final long serialVersionUID = 3191203908222203230L;
	protected Date dataAlteracao;
	protected Date dataCriacao;
	protected ID id;
	protected Credencial usuarioAlteracao;
	protected Credencial usuarioCriacao;
}
