package com.banco.contas_servic.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.contas_servic.model.Cliente;
import com.banco.contas_servic.model.Conta;
import com.banco.contas_servic.repository.ClienteRepository;
import com.banco.contas_servic.repository.ContaRepository;

@Service
public class ContaService {

    private final ContaRepository contaRepo;
    private final ClienteRepository clienteRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoria;
    private final SecureRandom random = new SecureRandom();

    public ContaService(ContaRepository contaRepo,
                        ClienteRepository clienteRepo,
                        PasswordEncoder passwordEncoder,
                        AuditoriaService auditoria) {
        this.contaRepo = contaRepo;
        this.clienteRepo = clienteRepo;
        this.passwordEncoder = passwordEncoder;
        this.auditoria = auditoria;
    }

    @Transactional
    public Conta abrir(Long clienteId, String pin, BigDecimal saldoInicial) {
        Cliente cliente = clienteRepo.findById(clienteId).orElse(null);
        if (cliente == null) {
            return null;
        }

        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setNumero(gerarNumero());
        conta.setPin(pin); // TEMPORARIO: PIN em texto. REVERTER: passwordEncoder.encode(pin)
        conta.setSaldo(saldoInicial == null ? BigDecimal.ZERO : saldoInicial);
        Conta guardada = contaRepo.save(conta);
        auditoria.registar("Abriu a conta " + guardada.getNumero() + " para o cliente id " + clienteId);
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<Conta> listarPorCliente(Long clienteId) {
        return contaRepo.findByClienteId(clienteId);
    }

    @Transactional
    public Conta bloquear(String numero) {
        Conta conta = contaRepo.findByNumero(numero).orElse(null);
        if (conta == null) {
            return null;
        }
        conta.setEstado("BLOQUEADA");
        Conta guardada = contaRepo.save(conta);
        auditoria.registar("Bloqueou a conta " + numero);
        return guardada;
    }

    @Transactional
    public Conta desbloquear(String numero) {
        Conta conta = contaRepo.findByNumero(numero).orElse(null);
        if (conta == null) {
            return null;
        }
        conta.setEstado("ACTIVA");
        Conta guardada = contaRepo.save(conta);
        auditoria.registar("Desbloqueou a conta " + numero);
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<Conta> listarTodas() {
        return contaRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Conta obterPorNumero(String numero) {
        return contaRepo.findByNumero(numero).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean validarPin(String numero, String pin) {
        Conta conta = contaRepo.findByNumero(numero).orElse(null);
        if (conta == null) {
            return false;
        }
        return pin != null && pin.equals(conta.getPin()); // TEMPORARIO
    }

    @Transactional
    public Conta mudarPin(String numero, String pinActual, String novoPin) {
        Conta conta = contaRepo.findByNumero(numero).orElse(null);
        if (conta == null) {
            return null;
        }
        if (!pinActual.equals(conta.getPin())) { // TEMPORARIO
            return null;
        }
        conta.setPin(novoPin); // TEMPORARIO: REVERTER: passwordEncoder.encode(novoPin)
        conta.setPrimeiroAcesso(false);
        Conta guardada = contaRepo.save(conta);
        auditoria.registar("Mudou o PIN da conta " + numero);
        return guardada;
    }

    @Transactional
    public Conta creditar(String numero, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        Conta conta = contaRepo.findByNumeroComBloqueio(numero).orElse(null);
        if (conta == null) {
            return null;
        }
        if (!"ACTIVA".equals(conta.getEstado())) {
            return null;
        }
        conta.setSaldo(conta.getSaldo().add(valor));
        return contaRepo.save(conta);
    }

    @Transactional
    public Conta transferir(String origem, String destino, BigDecimal valor, String pin) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (origem.equals(destino)) {
            return null;
        }

        String primeiro = origem.compareTo(destino) < 0 ? origem : destino;
        String segundo = origem.compareTo(destino) < 0 ? destino : origem;

        Conta contaPrimeiro = contaRepo.findByNumeroComBloqueio(primeiro).orElse(null);
        Conta contaSegundo = contaRepo.findByNumeroComBloqueio(segundo).orElse(null);
        if (contaPrimeiro == null || contaSegundo == null) {
            return null;
        }

        Conta contaOrigem = origem.equals(primeiro) ? contaPrimeiro : contaSegundo;
        Conta contaDestino = origem.equals(primeiro) ? contaSegundo : contaPrimeiro;

        if (!"ACTIVA".equals(contaOrigem.getEstado()) || !"ACTIVA".equals(contaDestino.getEstado())) {
            return null;
        }
        if (!pin.equals(contaOrigem.getPin())) { // TEMPORARIO
            return null;
        }
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            return null;
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaRepo.save(contaOrigem);
        contaRepo.save(contaDestino);
        return contaOrigem;
    }

    private String gerarNumero() {
        String numero;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            numero = sb.toString();
        } while (contaRepo.existsByNumero(numero));
        return numero;
    }
}