package com.banco.contas_servic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.contas_servic.model.Transacao;

import lombok.Getter;

@Getter
public class TransacaoResponse {

    private final String referencia;
    private final String contaOrigem;
    private final String contaDestino;
    private final BigDecimal valor;
    private final LocalDateTime dataHora;
    private final String tipo;
    private final String estado;

    public TransacaoResponse(Transacao t) {
        this.referencia = t.getReferencia();
        this.contaOrigem = t.getContaOrigem();
        this.contaDestino = t.getContaDestino();
        this.valor = t.getValor();
        this.dataHora = t.getDataHora();
        this.tipo = t.getTipo();
        this.estado = t.getEstado();
    }
}