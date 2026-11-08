package com.banco.contas_servic.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.contas_servic.model.Conta;
import com.banco.contas_servic.model.Transacao;
import com.banco.contas_servic.repository.TransacaoRepository;

@Service
public class TransacaoService {

    private final ContaService contaService;
    private final TransacaoRepository transacaoRepo;
    private final AuditoriaService auditoria;

    public TransacaoService(ContaService contaService, TransacaoRepository transacaoRepo,
                            AuditoriaService auditoria) {
        this.contaService = contaService;
        this.transacaoRepo = transacaoRepo;
        this.auditoria = auditoria;
    }

    @Transactional
    public Transacao depositar(String numero, BigDecimal valor) {
        Conta conta = contaService.creditar(numero, valor);
        if (conta == null) {
            return null;
        }
        Transacao t = new Transacao();
        t.setReferencia(gerarReferencia());
        t.setContaOrigem(null);
        t.setContaDestino(numero);
        t.setValor(valor);
        t.setTipo("DEPOSITO");
        t.setEstado("CONCLUIDA");
        Transacao guardada = transacaoRepo.save(t);
        auditoria.registar("Depositou " + valor + " MZN na conta " + numero
                + " (ref " + guardada.getReferencia() + ")");
        return guardada;
    }

    @Transactional
    public Transacao transferir(String origem, String destino, BigDecimal valor, String pin) {
        Conta conta = contaService.transferir(origem, destino, valor, pin);
        if (conta == null) {
            return null;
        }
        Transacao t = new Transacao();
        t.setReferencia(gerarReferencia());
        t.setContaOrigem(origem);
        t.setContaDestino(destino);
        t.setValor(valor);
        t.setTipo("TRANSFERENCIA");
        t.setEstado("CONCLUIDA");
        Transacao guardada = transacaoRepo.save(t);
        auditoria.registar("Transferiu " + valor + " MZN da conta " + origem
                + " para a conta " + destino + " (ref " + guardada.getReferencia() + ")");
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<Transacao> movimentos(String numero) {
        return transacaoRepo.findByContaOrigemOrContaDestinoOrderByDataHoraDesc(numero, numero);
    }

    private String gerarReferencia() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}