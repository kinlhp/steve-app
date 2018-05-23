package com.kinlhp.steve.mapeamento;

import android.support.annotation.NonNull;

import com.kinlhp.steve.dominio.Credencial;
import com.kinlhp.steve.dto.CredencialDTO;
import com.kinlhp.steve.href.HRef;
import com.kinlhp.steve.util.Parametro;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kin on 8/14/17.
 */
public final class CredencialMapeamento implements Serializable {
	private static final long serialVersionUID = 4969524799319610450L;
	private static final String URL_BASE = Parametro
			.get(Parametro.Chave.URL_BASE).toString();

	private CredencialMapeamento() {
	}

	@NonNull
	public static Credencial paraDominio(@NonNull CredencialDTO dto) {
		final Credencial dominio = Credencial.builder()
				.perfilAdministrador(dto.isPerfilAdministrador())
				.perfilPadrao(dto.isPerfilPadrao())
				.perfilSistema(dto.isPerfilSistema())
				.senha(dto.getSenha())
				.situacao(Credencial.Situacao.valueOf(dto.getSituacao().name()))
				.usuario(dto.getUsuario())
				.build();
		dominio.setDataCriacao(dto.getDataCriacao());
		dominio.setDataUltimaAlteracao(dto.getDataUltimaAlteracao());
		dominio.setId(obterId(dto.getLinks().getSelf()));
		return dominio;
	}

	@NonNull
	public static Set<Credencial> paraDominios(@NonNull Set<CredencialDTO> dtos) {
		Set<Credencial> dominios = new LinkedHashSet<>();
		for (CredencialDTO dto : dtos) {
			dominios.add(paraDominio(dto));
		}
		return dominios;
	}

	public static CredencialDTO paraDTO(@NonNull Credencial dominio) {
		return CredencialDTO.builder()
				.perfilAdministrador(dominio.isPerfilAdministrador())
				.perfilPadrao(dominio.isPerfilPadrao())
				.perfilSistema(dominio.isPerfilSistema())
				.senha(dominio.getSenha())
				.situacao(CredencialDTO.SituacaoDTO.valueOf(dominio.getSituacao().name()))
				.usuario(dominio.getUsuario())
				.funcionario(URL_BASE + "pessoas/" + dominio.getFuncionario().getId().intValue())
				.build();
	}

	private static BigInteger obterId(@NonNull HRef self) {
		String href = self.getHref();
		return new BigInteger(href.substring(href.lastIndexOf("/") + 1));
	}
}
