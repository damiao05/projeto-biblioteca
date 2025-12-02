package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.domain.usuario.Multa;
import com.bibliotecaproject.api.repository.EmprestimoRepository;
import com.bibliotecaproject.api.repository.MultaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MultaService {

    private final MultaRepository multaRepository;
    private final EmprestimoRepository emprestimoRepository;

    public MultaService(MultaRepository multaRepository, EmprestimoRepository emprestimoRepository) {
        this.multaRepository = multaRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void calcularMulta() {

        List<Emprestimo> emprestimos = emprestimoRepository.findByDtDevolucaoRealIsNull();

        for(Emprestimo emprestimo : emprestimos) {

            LocalDate dataPrevista = emprestimo.getDtPrevistaDevolucao();
            LocalDate dataHoje = LocalDate.now();

            if(dataHoje.isAfter(dataPrevista)) {

                double diferencaDias = ChronoUnit.DAYS.between(dataPrevista, dataHoje);
                BigDecimal valorMulta = BigDecimal.valueOf(diferencaDias * 1.5);

                Optional<Multa> multaExistenteOpt = multaRepository.findByEmprestimo(emprestimo);

                if(multaExistenteOpt.isEmpty()) {

                    Multa multa = new Multa();

                    multa.setValorMulta(valorMulta);
                    multa.setDtGeracaoMulta(dataPrevista);
                    multa.setStatusPagamento("ATIVA");
                    multa.setEmprestimo(emprestimo);

                    multaRepository.save(multa);

                } else {
                    Multa multaExistente = multaExistenteOpt.get();

                    multaExistente.setValorMulta(valorMulta);

                    multaRepository.save(multaExistente);

                }

            }

        }

    }

    public Multa registrarPagamento(UUID id, LocalDate dataPagamento) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Multa não encontrada"));

        multa.setDtPagamento(dataPagamento);
        multa.setStatusPagamento("PAGAMENTO");

        return multaRepository.save(multa);

    }

    public List<Multa> listarMultas() {
        return multaRepository.findAll();
    }

}
