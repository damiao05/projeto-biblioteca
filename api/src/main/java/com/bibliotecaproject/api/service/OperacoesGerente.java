package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Usuario;

import java.util.List;

public interface OperacoesGerente extends OperacoesBibliotecario {

    Usuario criarFuncionario(Usuario novoFuncionario);

    List<Usuario> listarFuncionarios();

}
