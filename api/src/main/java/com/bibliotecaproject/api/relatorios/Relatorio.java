package com.bibliotecaproject.api.relatorios;

import java.util.List;

public abstract class Relatorio {

    public final String gerarRelatorio() {
        StringBuilder sb = new StringBuilder();

        sb.append(getCabecalho()).append("\n\n");

        List<String> corpo = getCorpo();
        for(String linha : corpo){
            sb.append(linha).append("\n");
        }

        sb.append("\n").append(getRodape());

        return sb.toString();
    }

    protected abstract String getCabecalho();
    protected abstract List<String> getCorpo();
    protected String getRodape() {
        return "Relatório gerado automaticamente pelo sistema.";
    }

}
