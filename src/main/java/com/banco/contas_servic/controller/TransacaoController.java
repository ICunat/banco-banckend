package com.banco.contas_servic.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.banco.contas_servic.dto.DepositoRequest;
import com.banco.contas_servic.dto.TransacaoResponse;
import com.banco.contas_servic.dto.TransferenciaRequest;
import com.banco.contas_servic.model.Transacao;
import com.banco.contas_servic.service.TransacaoService;

import jakarta.validation.Valid;

@RestController
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping("/contas/{numero}/depositos")
    public ResponseEntity<TransacaoResponse> depositar(@PathVariable String numero,
                                                       @Valid @RequestBody DepositoRequest pedido) {
        Transacao t = service.depositar(numero, pedido.getValor());
        if (t == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new TransacaoResponse(t));
    }

    @PostMapping("/transferencias")
    public ResponseEntity<TransacaoResponse> transferir(@Valid @RequestBody TransferenciaRequest pedido,
                                                        Principal principal) {
        // So se pode transferir a partir da propria conta
        if (!pedido.getOrigem().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Transacao t = service.transferir(pedido.getOrigem(), pedido.getDestino(),
                pedido.getValor(), pedido.getPin());
        if (t == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new TransacaoResponse(t));
    }

    @GetMapping("/contas/{numero}/movimentos")
    public List<TransacaoResponse> movimentos(@PathVariable String numero) {
        List<TransacaoResponse> respostas = new ArrayList<>();
        for (Transacao t : service.movimentos(numero)) {
            respostas.add(new TransacaoResponse(t));
        }
        return respostas;
    }
}