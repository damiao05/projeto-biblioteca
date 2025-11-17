package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.domain.usuario.Multa;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MultaRepository extends JpaRepository<Multa, UUID> {

    Optional<Multa> findByEmprestimo(Emprestimo emprestimo);

    boolean existsByEmprestimoInAndStatusPagamento(List<Emprestimo> emprestimo, String status);

    @Query("SELECT m.emprestimo.usuario.nome, m.valorMulta, m.dtGeracaoMulta, COUNT(m) " +
            "FROM Multa m " +
            "WHERE m.statusPagamento = :status " + // Filtra pelo status
            "GROUP BY m.emprestimo.usuario.nome, m.valorMulta " + // Agrupa pelos campos selecionados
            "ORDER BY COUNT(m) DESC")
    List<Object[]> findByStatusPagamento(@Param("status") String status);

}
