package com.banco.contas_servic.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.banco.contas_servic.model.Auditoria;
import com.banco.contas_servic.repository.AuditoriaRepository;

@Service
public class AuditoriaService {

    private final AuditoriaRepository repo;

    public AuditoriaService(AuditoriaRepository repo) {
        this.repo = repo;
    }

    public void registar(String acao) {
        String autor = "sistema";
        String papel = "SISTEMA";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            autor = auth.getName();
            for (GrantedAuthority g : auth.getAuthorities()) {
                papel = g.getAuthority().replace("ROLE_", "");
            }
        }

        Auditoria a = new Auditoria();
        a.setAutor(autor);
        a.setPapel(papel);
        a.setAcao(acao);
        repo.save(a);
    }

    public List<Auditoria> listar() {
        return repo.findAllByOrderByDataHoraDesc();
    }
}