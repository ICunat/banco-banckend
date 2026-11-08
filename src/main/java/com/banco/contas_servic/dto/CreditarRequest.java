package com.banco.contas_servic.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditarRequest {

    @NotNull
    private BigDecimal valor;
}