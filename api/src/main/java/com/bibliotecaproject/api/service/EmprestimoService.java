package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.EmprestimoRepository;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository, UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Emprestimo registrarEmprestimo(Emprestimo emprestimo) {

        Livro livro = livroRepository.findById(emprestimo.getLivro().getId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if(livro.getQtd_disponivel() <= 0) {
            throw new RuntimeException("Livro indisponível para empréstimo");
        }

        Usuario usuario = usuarioRepository.findById(emprestimo.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);

        livro.setQtd_disponivel(livro.getQtd_disponivel()-1);
        livroRepository.save(livro);

        return emprestimoRepository.save(emprestimo);

    }

    @Transactional
    public Emprestimo registrarDevolucao(UUID id, LocalDate data) {

        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        emprestimo.setDtDevolucaoReal(data);

        Livro livro = emprestimo.getLivro();
        livro.setQtd_disponivel(livro.getQtd_disponivel()+1);
        livroRepository.save(livro);

        return emprestimoRepository.save(emprestimo);

    }

    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();

    }

    public Optional<Emprestimo> buscarEmprestimo(UUID id) {
        return emprestimoRepository.findById(id);
    }

}
