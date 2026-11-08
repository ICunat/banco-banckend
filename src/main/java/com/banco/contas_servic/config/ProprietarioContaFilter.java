package com.banco.contas_servic.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProprietarioContaFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao != null && eCliente(autenticacao)) {
            String caminho = request.getRequestURI();

            // Um cliente nao pode listar todas as contas
            if (caminho.equals("/contas") || caminho.equals("/contas/")) {
                proibir(response);
                return;
            }

            // /contas/{numero}... so pode ser a conta do proprio cliente
            if (caminho.startsWith("/contas/")) {
                String resto = caminho.substring("/contas/".length());
                int barra = resto.indexOf('/');
                String numero = resto;
                if (barra >= 0) {
                    numero = resto.substring(0, barra);
                }
                if (!numero.equals(autenticacao.getName())) {
                    proibir(response);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean eCliente(Authentication autenticacao) {
        for (GrantedAuthority permissao : autenticacao.getAuthorities()) {
            if (permissao.getAuthority().equals("ROLE_CLIENTE")) {
                return true;
            }
        }
        return false;
    }

    private void proibir(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"erro\":\"Sem permissao para aceder a esta conta.\"}");
    }
}