package com.bibliotecaproject.api.relatorios;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.repository.EmprestimoRepository;
import com.bibliotecaproject.api.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class RelatorioLivrosMaisEmprestados extends Relatorio {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository  emprestimoRepository;

    @Override
    protected String getCabecalho() {
        return "Relatório de Livros Mais Emprestados";
    }

    @Override
    protected List<String> getCorpo() {
        List<Object[]> resultados = emprestimoRepository.findLivrosMaisEmprestados();

        if(resultados.isEmpty()){
            return List.of("Nenhum dado de empréstimo encontrado");
        }

        List<String> linhas = new ArrayList<>();

        linhas.add(String.format("%-40s | %-30s | %5s", "Livros", "Editora", "Empréstimos"));
        linhas.add("-".repeat(120));

        linhas.addAll(
                resultados.stream()
                        .map(r ->
                                String.format("%-40s | %-30s | %5s", r[0], r[1], r[2]))
                        .collect(Collectors.toList())
        );

        return linhas;

    }

}
