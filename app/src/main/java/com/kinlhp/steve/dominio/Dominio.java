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
@Setter()
abstract class Dominio<ID extends Serializable> implements Serializable {
	private static final long serialVersionUID = 7069326960840551700L;
	private Date dataAlteracao;
	private Date dataCriacao;
	private ID id;
	private Credencial usuarioAlteracao;
	private Credencial usuarioCriacao;
}
