package com.banco.contas_servic.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbrirContaRequest {

    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "O PIN deve ter 4 dígitos")
    private String pin;

    @DecimalMin(value = "0.00", message = "O saldo inicial não pode ser negativo")
    private BigDecimal saldoInicial;
}