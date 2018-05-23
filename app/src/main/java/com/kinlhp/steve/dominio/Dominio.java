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
	private static final long serialVersionUID = -2277120568717879939L;
	protected Date dataCriacao;
	protected Date dataUltimaAlteracao;
	protected ID id;
	protected Credencial usuarioCriacao;
	protected Credencial usuarioUltimaAlteracao;
}
