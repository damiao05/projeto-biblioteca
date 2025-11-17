package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {

    List<Emprestimo> findByDtDevolucaoRealIsNull();

    @Query("SELECT e.livro.titulo, e.livro.editora, COUNT(e) FROM Emprestimo e GROUP BY e.livro.titulo, e.livro.editora, e.livro.isbn ORDER BY COUNT(e) DESC")
    List<Object[]> findLivrosMaisEmprestados();

    boolean existsByUsuarioAndDtDevolucaoRealIsNull(Usuario usuario);

    boolean existsByLivroAndDtDevolucaoRealIsNull(Livro livro);

}
