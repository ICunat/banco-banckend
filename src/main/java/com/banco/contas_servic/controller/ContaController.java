package com.banco.contas_servic.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.contas_servic.dto.AbrirContaRequest;
import com.banco.contas_servic.dto.ContaResponse;
import com.banco.contas_servic.model.Conta;
import com.banco.contas_servic.service.ContaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes/{clienteId}/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> abrir(@PathVariable Long clienteId,
                                               @Valid @RequestBody AbrirContaRequest pedido) {
        Conta conta = service.abrir(clienteId, pedido.getPin(), pedido.getSaldoInicial());
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        ContaResponse resposta = new ContaResponse(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public List<ContaResponse> listar(@PathVariable Long clienteId) {
        List<Conta> contas = service.listarPorCliente(clienteId);
        List<ContaResponse> respostas = new ArrayList<>();
        for (Conta conta : contas) {
            respostas.add(new ContaResponse(conta));
        }
        return respostas;
    }
}