package com.banco.contas_servic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banco.contas_servic.model.Conta;

import jakarta.persistence.LockModeType;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumero(String numero);

    boolean existsByNumero(String numero);

    List<Conta> findByClienteId(Long clienteId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Conta c where c.numero = :numero")
    Optional<Conta> findByNumeroComBloqueio(@Param("numero") String numero);
}