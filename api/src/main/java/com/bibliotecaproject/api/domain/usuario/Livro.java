package com.bibliotecaproject.api.domain.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "livro")
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Livro {

    @Id
    private String isbn;
    private String autor;
    private String editora;
    private String titulo;
    private String categoria;
    private Integer qtd_total;
    private Integer qtd_disponivel;
    private String capaFilename;
    @Column(columnDefinition = "TEXT")
    private String sinopse;

}
