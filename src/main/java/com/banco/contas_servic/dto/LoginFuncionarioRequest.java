package com.banco.contas_servic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFuncionarioRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String palavraPasse;
}