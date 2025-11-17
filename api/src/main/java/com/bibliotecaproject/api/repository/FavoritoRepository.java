package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Favorito;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    Optional<Favorito> findByUsuarioAndLivro(Usuario usuario, Livro livro);

}
