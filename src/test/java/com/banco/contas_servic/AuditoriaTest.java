package com.banco.contas_servic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banco.contas_servic.model.Auditoria;
import com.banco.contas_servic.model.Cliente;
import com.banco.contas_servic.service.AuditoriaService;
import com.banco.contas_servic.service.ClienteService;

@SpringBootTest
class AuditoriaTest {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private ClienteService clienteService;

    @Test
    void registarAccaoFicaGuardada() {
        long antes = auditoriaService.listar().size();

        auditoriaService.registar("Acao de teste");

        List<Auditoria> lista = auditoriaService.listar();
        assertEquals(antes + 1, lista.size());
        assertEquals("Acao de teste", lista.get(0).getAcao());
        assertEquals("sistema", lista.get(0).getAutor());
    }

    @Test
    void criarClienteRegistaAuditoria() {
        Cliente c = new Cliente();
        c.setNome("Cliente Auditoria");
        c.setBi("BI" + System.nanoTime());
        c.setTelefone("841111111");

        long antes = auditoriaService.listar().size();
        clienteService.criar(c);

        List<Auditoria> lista = auditoriaService.listar();
        assertEquals(antes + 1, lista.size());
        assertTrue(lista.get(0).getAcao().contains("Criou o cliente"));
    }
}