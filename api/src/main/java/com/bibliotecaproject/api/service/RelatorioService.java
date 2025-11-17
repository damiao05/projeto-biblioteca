package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.relatorios.RelatorioLivrosMaisEmprestados;
import com.bibliotecaproject.api.relatorios.RelatorioMultasAtivas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioLivrosMaisEmprestados relatorioLivrosMaisEmprestados;

    @Autowired
    private RelatorioMultasAtivas relatorioMultasAtivas;

    public String gerarRelatorioLivrosMaisEmprestados() {
        return relatorioLivrosMaisEmprestados.gerarRelatorio();
    }

    public String gerarRelatorioMultasAtivas() {
        return relatorioMultasAtivas.gerarRelatorio();
    }

}
