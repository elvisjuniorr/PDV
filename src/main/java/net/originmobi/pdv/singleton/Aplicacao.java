package net.originmobi.pdv.singleton;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Aplicacao {

	private static Aplicacao aplicacao;
	private String usuarioAtual;

	public Aplicacao() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
            usuarioAtual = SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            usuarioAtual = "Sistema"; 
        }
	}

	public static synchronized Aplicacao getInstancia() {
		if (aplicacao == null) {
			aplicacao = new Aplicacao();
		}
		return aplicacao;
	}

	public String getUsuarioAtual() {
		return usuarioAtual;
	}
	
}
