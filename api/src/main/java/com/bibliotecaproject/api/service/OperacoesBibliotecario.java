package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OperacoesBibliotecario {

    List<Usuario> listarUsuarios();

    void deletarUsuario(UUID id);

    Livro cadastrarLivro(Livro livro, MultipartFile file) throws IOException;

    Livro editarLivro(String isbn, Livro livroAtualizado, MultipartFile file) throws IOException;

    //Emprestimo registrarEmprestimo(String id_livro, UUID id_usuario);

    //Emprestimo registrarDevolucao(UUID id);

}
