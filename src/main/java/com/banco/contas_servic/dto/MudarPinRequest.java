package com.banco.contas_servic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MudarPinRequest {

    @NotBlank
    private String pinActual;

    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "O novo PIN deve ter 4 dígitos")
    private String novoPin;
}