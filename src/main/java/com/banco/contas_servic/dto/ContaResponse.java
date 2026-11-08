package com.banco.contas_servic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.contas_servic.model.Conta;

import lombok.Getter;

@Getter
public class ContaResponse {

    private final Long id;
    private final String numero;
    private final Long clienteId;
    private final BigDecimal saldo;
    private final String estado;
    private final LocalDateTime dataAbertura;
    private final boolean primeiroAcesso;

    public ContaResponse(Conta conta) {
        this.id = conta.getId();
        this.numero = conta.getNumero();
        this.clienteId = conta.getCliente().getId();
        this.saldo = conta.getSaldo();
        this.estado = conta.getEstado();
        this.dataAbertura = conta.getDataAbertura();
        this.primeiroAcesso = conta.isPrimeiroAcesso();
    }
}