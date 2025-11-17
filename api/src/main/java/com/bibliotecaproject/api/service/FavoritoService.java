package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Favorito;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.FavoritoRepository;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void favoritarLivro(UUID idLivro, UUID idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Optional<Favorito> existente = favoritoRepository.findByUsuarioAndLivro(usuario, livro);
        if(existente.isPresent()) {
            throw new RuntimeException("Este livro já está nos favoritos");

        }

        Favorito favorito = new Favorito();

        favorito.setUsuario(usuario);
        favorito.setLivro(livro);

        favoritoRepository.save(favorito);

    }

    public void retirarFavorito(UUID idUsuario, UUID idLivro) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Optional<Favorito> favorito = favoritoRepository.findByUsuarioAndLivro(usuario, livro);

        favoritoRepository.delete(favorito.get());

    }

}
