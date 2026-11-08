package com.banco.contas_servic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.contas_servic.dto.LoginClienteRequest;
import com.banco.contas_servic.dto.LoginFuncionarioRequest;
import com.banco.contas_servic.dto.LoginResponse;
import com.banco.contas_servic.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginFuncionario(@Valid @RequestBody LoginFuncionarioRequest pedido) {
        LoginResponse resposta = service.loginFuncionario(pedido.getEmail(), pedido.getPalavraPasse());
        if (resposta == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/cliente/login")
    public ResponseEntity<LoginResponse> loginCliente(@Valid @RequestBody LoginClienteRequest pedido) {
        LoginResponse resposta = service.loginCliente(pedido.getNumeroConta(), pedido.getPin());
        if (resposta == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(resposta);
    }
}