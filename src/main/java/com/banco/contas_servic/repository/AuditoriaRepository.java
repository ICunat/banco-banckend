package com.banco.contas_servic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.contas_servic.model.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findAllByOrderByDataHoraDesc();
}