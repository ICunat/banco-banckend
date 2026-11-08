package com.banco.contas_servic.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.banco.contas_servic.model.Funcionario;
import com.banco.contas_servic.repository.FuncionarioRepository;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final FuncionarioRepository repo;
    private final PasswordEncoder encoder;

    // Vem da variavel ADMIN_PASSWORD. Se nao existir, fica vazio.
    @Value("${ADMIN_PASSWORD:}")
    private String palavraPasseAdmin;

    public DadosIniciais(FuncionarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        boolean temNova = palavraPasseAdmin != null && !palavraPasseAdmin.isBlank();

        Optional<Funcionario> existente = repo.findByEmail("admin@banco.com");

        if (existente.isPresent()) {
            // O admin ja existe: se foi definida uma palavra-passe nova, actualiza.
            if (temNova) {
                Funcionario f = existente.get();
                if (!encoder.matches(palavraPasseAdmin, f.getPalavraPasse())) {
                    f.setPalavraPasse(encoder.encode(palavraPasseAdmin));
                    repo.save(f);
                }
            }
            return;
        }

        Funcionario f = new Funcionario();
        f.setNome("Administrador");
        f.setEmail("admin@banco.com");
        f.setPalavraPasse(encoder.encode(temNova ? palavraPasseAdmin : "admin123"));
        repo.save(f);
    }
}