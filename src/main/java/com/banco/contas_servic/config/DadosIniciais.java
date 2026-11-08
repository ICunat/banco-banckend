package com.banco.contas_servic.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.banco.contas_servic.model.Funcionario;
import com.banco.contas_servic.repository.FuncionarioRepository;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final FuncionarioRepository repo;
    private final PasswordEncoder encoder;

    public DadosIniciais(FuncionarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (repo.findByEmail("admin@banco.com").isPresent()) {
            return;
        }
        Funcionario f = new Funcionario();
        f.setNome("Administrador");
        f.setEmail("admin@banco.com");
        f.setPalavraPasse(encoder.encode("admin123"));
        repo.save(f);
    }
}