package com.banco.contas_servic.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banco.contas_servic.config.JwtService;
import com.banco.contas_servic.dto.LoginResponse;
import com.banco.contas_servic.model.Conta;
import com.banco.contas_servic.model.Funcionario;
import com.banco.contas_servic.repository.ContaRepository;
import com.banco.contas_servic.repository.FuncionarioRepository;

@Service
public class AuthService {

    private final FuncionarioRepository funcionarioRepo;
    private final ContaRepository contaRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(FuncionarioRepository funcionarioRepo, ContaRepository contaRepo,
                       PasswordEncoder encoder, JwtService jwtService) {
        this.funcionarioRepo = funcionarioRepo;
        this.contaRepo = contaRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public LoginResponse loginFuncionario(String email, String palavraPasse) {
        Funcionario f = funcionarioRepo.findByEmail(email).orElse(null);
        if (f == null) {
            return null;
        }
        if (!"ACTIVO".equals(f.getEstado())) {
            return null;
        }
        if (!encoder.matches(palavraPasse, f.getPalavraPasse())) {
            return null;
        }
        String token = jwtService.gerar(f.getEmail(), "FUNCIONARIO");
        return new LoginResponse(token, "FUNCIONARIO", f.getEmail(), false);
    }

    public LoginResponse loginCliente(String numeroConta, String pin) {
        Conta conta = contaRepo.findByNumero(numeroConta).orElse(null);
        if (conta == null) {
            return null;
        }
        if (!"ACTIVA".equals(conta.getEstado())) {
            return null;
        }
        if (!pin.equals(conta.getPin())) { // TEMPORARIO: PIN em texto. REVERTER para encoder.matches
            return null;
        }
        String token = jwtService.gerar(conta.getNumero(), "CLIENTE");
        return new LoginResponse(token, "CLIENTE", conta.getNumero(), conta.isPrimeiroAcesso());
    }
}