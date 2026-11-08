package com.banco.contas_servic.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false)
    private String papel;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @PrePersist
    public void definirPadroes() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}