package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Multa;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OperacoesBibliotecario {

    List<Usuario> listarUsuarios();

    void deletarUsuario(UUID id);

    Livro cadastrarLivro(Livro livro, MultipartFile file) throws IOException;

    Livro editarLivro(String isbn, Livro livroAtualizado, MultipartFile file) throws IOException;

    Emprestimo registrarEmprestimo(Emprestimo emprestimo);

    Emprestimo registrarDevolucao(UUID id, LocalDate data);

    Multa registrarPagamento(UUID id, LocalDate dataPagamento);

    void deletarLivro(UUID id);

    List<Emprestimo> listarEmprestimos();

}
