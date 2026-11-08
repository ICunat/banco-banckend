package com.banco.contas_servic.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.contas_servic.model.Auditoria;
import com.banco.contas_servic.service.AuditoriaService;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Auditoria> listar() {
        return service.listar();
    }
}