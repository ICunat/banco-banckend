package com.banco.contas_servic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.contas_servic.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeContainingIgnoreCaseOrBiContainingOrTelefoneContaining(
            String nome, String bi, String telefone);
}