package com.banco.contas_servic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.contas_servic.model.Cliente;
import com.banco.contas_servic.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repo;
    private final AuditoriaService auditoria;

    public ClienteService(ClienteRepository repo, AuditoriaService auditoria) {
        this.repo = repo;
        this.auditoria = auditoria;
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        cliente.setId(null);
        Cliente guardado = repo.save(cliente);
        auditoria.registar("Criou o cliente " + guardado.getNome() + " (id " + guardado.getId() + ")");
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(String q) {
        if (q == null || q.isBlank()) {
            return repo.findAll();
        }
        return repo.findByNomeContainingIgnoreCaseOrBiContainingOrTelefoneContaining(q, q, q);
    }

    @Transactional(readOnly = true)
    public Cliente obter(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional
    public Cliente editar(Long id, Cliente dados) {
        Cliente cliente = repo.findById(id).orElse(null);
        if (cliente == null) {
            return null;
        }
        cliente.setNome(dados.getNome());
        cliente.setBi(dados.getBi());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());
        if (dados.getEstado() != null) {
            cliente.setEstado(dados.getEstado());
        }
        Cliente guardado = repo.save(cliente);
        auditoria.registar("Editou o cliente id " + id);
        return guardado;
    }
}