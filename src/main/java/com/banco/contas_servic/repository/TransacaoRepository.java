package com.banco.contas_servic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.contas_servic.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByContaOrigemOrContaDestinoOrderByDataHoraDesc(String origem, String destino);
}