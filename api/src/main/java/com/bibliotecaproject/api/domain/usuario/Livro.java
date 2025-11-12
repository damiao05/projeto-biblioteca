package com.bibliotecaproject.api.domain.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import lombok.EqualsAndHashCode;
import java.util.Set;
import java.util.UUID;

@Table(name = "livro")
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Livro {

    @Id
    @GeneratedValue
    private UUID id;
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

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Reserva> reservas;

}
