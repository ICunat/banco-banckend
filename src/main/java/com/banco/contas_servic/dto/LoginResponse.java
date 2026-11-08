package com.banco.contas_servic.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String token;
    private final String papel;
    private final String identificador;
    private final boolean primeiroAcesso;

    public LoginResponse(String token, String papel, String identificador, boolean primeiroAcesso) {
        this.token = token;
        this.papel = papel;
        this.identificador = identificador;
        this.primeiroAcesso = primeiroAcesso;
    }
}