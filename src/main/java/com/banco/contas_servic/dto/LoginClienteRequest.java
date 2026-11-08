package com.banco.contas_servic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginClienteRequest {

    @NotBlank
    private String numeroConta;

    @NotBlank
    private String pin;
}