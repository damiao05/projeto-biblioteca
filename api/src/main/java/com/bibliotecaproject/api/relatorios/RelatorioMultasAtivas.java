package com.bibliotecaproject.api.relatorios;

import com.bibliotecaproject.api.repository.EmprestimoRepository;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.MultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RelatorioMultasAtivas extends Relatorio {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private MultaRepository multaRepository;

    @Override
    protected String getCabecalho() {
        return "Relatório de Multas com status ativo";
    }

    @Override
    protected List<String> getCorpo() {
        List<Object[]> resultados = multaRepository.findByStatusPagamento("ATIVA");

        if(resultados.isEmpty()){
            return List.of("Nenhum dado de multa encontrado");
        }

        List<String> linhas = new ArrayList<>();

        linhas.add(String.format("%-35s | %-30s | %-30s", "Usuário", "Valor da Multa (hoje)", "Data Inicial da Multa"));
        linhas.add("-".repeat(120));

        linhas.addAll(
                resultados.stream()
                        .map(r ->
                                String.format("%-35.35s | %-30s | %-30s", r[0], r[1], r[2]))
                        .collect(Collectors.toList())
        );

        return linhas;

    }

}
