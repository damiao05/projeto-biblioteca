package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Avaliacao;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.AvaliacaoRepository;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AvaliacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public void registrarAvalicao(Avaliacao avaliacao, UUID idLivro, UUID idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        avaliacao.setLivro(livro);
        avaliacao.setUsuario(usuario);

        avaliacaoRepository.save(avaliacao);

    }

    public void deletarAvaliacao(UUID id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        avaliacaoRepository.delete(avaliacao);

    }

    public List<Avaliacao> listarAvaliacoes() {
        return avaliacaoRepository.findAll();

    }

}
