package com.banco.contas_servic.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.contas_servic.dto.ContaResponse;
import com.banco.contas_servic.dto.CreditarRequest;
import com.banco.contas_servic.dto.MudarPinRequest;
import com.banco.contas_servic.dto.TransferirRequest;
import com.banco.contas_servic.dto.ValidarPinRequest;
import com.banco.contas_servic.model.Conta;
import com.banco.contas_servic.service.ContaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contas")
public class ContaOperacoesController {

    private final ContaService service;

    public ContaOperacoesController(ContaService service) {
        this.service = service;
    }

    @PutMapping("/{numero}/bloquear")
    public ResponseEntity<ContaResponse> bloquear(@PathVariable String numero) {
        Conta conta = service.bloquear(numero);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ContaResponse(conta));
    }

    @PutMapping("/{numero}/desbloquear")
    public ResponseEntity<ContaResponse> desbloquear(@PathVariable String numero) {
        Conta conta = service.desbloquear(numero);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ContaResponse(conta));
    }

        @GetMapping
    public List<ContaResponse> listar() {
        List<Conta> contas = service.listarTodas();
        List<ContaResponse> respostas = new ArrayList<>();
        for (Conta conta : contas) {
            respostas.add(new ContaResponse(conta));
        }
        return respostas;
    }

    @GetMapping("/{numero}")
    public ResponseEntity<ContaResponse> obter(@PathVariable String numero) {
        Conta conta = service.obterPorNumero(numero);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ContaResponse(conta));
    }

        @PostMapping("/{numero}/validar-pin")
    public ResponseEntity<Void> validarPin(@PathVariable String numero,
                                           @Valid @RequestBody ValidarPinRequest pedido) {
        boolean valido = service.validarPin(numero, pedido.getPin());
        if (!valido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{numero}/pin")
    public ResponseEntity<ContaResponse> mudarPin(@PathVariable String numero,
                                                  @Valid @RequestBody MudarPinRequest pedido) {
        Conta conta = service.mudarPin(numero, pedido.getPinActual(), pedido.getNovoPin());
        if (conta == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new ContaResponse(conta));
    }

        @PostMapping("/{numero}/creditar")
    public ResponseEntity<ContaResponse> creditar(@PathVariable String numero,
                                                  @Valid @RequestBody CreditarRequest pedido) {
        Conta conta = service.creditar(numero, pedido.getValor());
        if (conta == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.ok(new ContaResponse(conta));
    }

    @PostMapping("/transferir")
    public ResponseEntity<ContaResponse> transferir(@Valid @RequestBody TransferirRequest pedido) {
        Conta conta = service.transferir(pedido.getOrigem(), pedido.getDestino(),
                pedido.getValor(), pedido.getPin());
        if (conta == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.ok(new ContaResponse(conta));
    }
}