package com.banco.contas_servic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banco.contas_servic.model.Cliente;
import com.banco.contas_servic.model.Conta;
import com.banco.contas_servic.repository.ClienteRepository;
import com.banco.contas_servic.repository.ContaRepository;
import com.banco.contas_servic.service.ContaService;

@SpringBootTest
class ConcorrenciaTest {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    private Cliente criarCliente() {
        Cliente c = new Cliente();
        c.setNome("Teste Concorrencia");
        c.setBi("BI" + System.nanoTime());
        c.setTelefone("840000000");
        return clienteRepo.save(c);
    }

    private Conta criarConta(Cliente cliente, String pin, String saldo) {
        return contaService.abrir(cliente.getId(), pin, new BigDecimal(saldo));
    }

    private BigDecimal saldoDe(String numero) {
        return contaRepo.findByNumero(numero).orElseThrow().getSaldo();
    }

    // Executa todas as tarefas ao mesmo tempo e devolve os resultados
    private <T> List<T> correrJuntas(List<Callable<T>> tarefas) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(tarefas.size());
        CountDownLatch arranque = new CountDownLatch(1);
        List<Future<T>> futuros = new ArrayList<>();
        for (Callable<T> tarefa : tarefas) {
            futuros.add(pool.submit(() -> {
                arranque.await();
                return tarefa.call();
            }));
        }
        arranque.countDown();
        List<T> resultados = new ArrayList<>();
        for (Future<T> f : futuros) {
            resultados.add(f.get(60, TimeUnit.SECONDS));
        }
        pool.shutdown();
        return resultados;
    }

    @Test
    void teste1_doisDepositosSimultaneos() throws Exception {
        Cliente cliente = criarCliente();
        Conta conta = criarConta(cliente, "1111", "0");
        String numero = conta.getNumero();

        List<Callable<Conta>> tarefas = new ArrayList<>();
        tarefas.add(() -> contaService.creditar(numero, new BigDecimal("100")));
        tarefas.add(() -> contaService.creditar(numero, new BigDecimal("100")));
        correrJuntas(tarefas);

        assertEquals(0, new BigDecimal("200").compareTo(saldoDe(numero)));
    }

    @Test
    void teste2_duasTransferenciasSaldoParaUma() throws Exception {
        Cliente cliente = criarCliente();
        Conta a = criarConta(cliente, "1111", "100");
        Conta b = criarConta(cliente, "2222", "0");
        String numA = a.getNumero();
        String numB = b.getNumero();

        List<Callable<Conta>> tarefas = new ArrayList<>();
        tarefas.add(() -> contaService.transferir(numA, numB, new BigDecimal("100"), "1111"));
        tarefas.add(() -> contaService.transferir(numA, numB, new BigDecimal("100"), "1111"));
        List<Conta> resultados = correrJuntas(tarefas);

        int aceites = 0;
        for (Conta r : resultados) {
            if (r != null) {
                aceites++;
            }
        }
        assertEquals(1, aceites);
        assertEquals(0, BigDecimal.ZERO.compareTo(saldoDe(numA)));
        assertEquals(0, new BigDecimal("100").compareTo(saldoDe(numB)));
    }

    @Test
    void teste3_transferenciasCruzadasSemDeadlock() throws Exception {
        Cliente cliente = criarCliente();
        Conta a = criarConta(cliente, "1111", "1000");
        Conta b = criarConta(cliente, "2222", "1000");
        String numA = a.getNumero();
        String numB = b.getNumero();

        List<Callable<Conta>> tarefas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tarefas.add(() -> contaService.transferir(numA, numB, new BigDecimal("10"), "1111"));
            tarefas.add(() -> contaService.transferir(numB, numA, new BigDecimal("10"), "2222"));
        }
        correrJuntas(tarefas); // se houvesse deadlock, daria timeout aqui

        BigDecimal total = saldoDe(numA).add(saldoDe(numB));
        assertEquals(0, new BigDecimal("2000").compareTo(total));
        assertTrue(saldoDe(numA).compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(saldoDe(numB).compareTo(BigDecimal.ZERO) >= 0);
    }
}