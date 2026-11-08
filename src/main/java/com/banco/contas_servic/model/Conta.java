package com.banco.contas_servic.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    @Column(nullable = false)
    private boolean primeiroAcesso;

    @PrePersist
    public void definirPadroes() {
        if (estado == null || estado.isBlank()) {
            estado = "ACTIVA";
        }
        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
        }
        primeiroAcesso = true;
    }
}