package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, String> {
}
