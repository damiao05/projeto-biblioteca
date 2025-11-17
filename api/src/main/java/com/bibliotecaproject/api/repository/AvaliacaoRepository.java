package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {
    @Query("SELECT COALESCE(AVG(a.avaliacao), 0.0) FROM Avaliacao a WHERE a.livro.id = :idLivro")
    Double getMediaAvaliacoesPorLivro(@Param("idLivro") UUID idLivro);

}
