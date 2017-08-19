package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.href.HRef;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by kin on 8/14/17.
 */
public final class CredencialMapeamento implements Serializable {
	private static final long serialVersionUID = -274704428741036210L;

	private CredencialMapeamento() {
	}

	public static Credencial deDTO(@NonNull CredencialDTO dto) {
		final Credencial dominio = Credencial.builder()
				.perfilAdministrador(dto.isPerfilAdministrador())
				.perfilPadrao(dto.isPerfilPadrao())
				.perfilSistema(dto.isPerfilSistema())
				.senha(dto.getSenha())
				.situacao(Credencial.Situacao.valueOf(dto.getSituacao().name()))
				.usuario(dto.getUsuario())
				.build();
		dominio.setDataAlteracao(dto.getDataAlteracao());
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
