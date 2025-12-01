package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Livro;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID> {

    List<Livro> findByCategoria(String categoria, Sort sort);

    Optional<Livro> findByIsbn(String isbn);

    Optional<Livro> findById(UUID id);

    @Query("SELECT l FROM Livro l WHERE " +
        "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :input, '%')) OR " +
        "LOWER(l.autor) LIKE LOWER(CONCAT('%', :input, '%')) OR " +
        "LOWER(l.isbn) LIKE LOWER(CONCAT('%', :input, '%')) OR " +
        "LOWER(l.editora) LIKE LOWER(CONCAT('%', :input, '%'))")
    List<Livro> findByInput(@Param("input") String input);
}
