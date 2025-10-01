package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, String> {
    List<Livro> findByCategoria(String categoria);
}
