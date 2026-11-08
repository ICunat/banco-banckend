package com.banco.contas_servic.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenciaRequest {

    @NotBlank
    private String origem;

    @NotBlank
    private String destino;

    @NotNull
    private BigDecimal valor;

    @NotBlank
    private String pin;
}