package com.bibliotecaproject.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "reserva")
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDate dt_reserva;

    @CreationTimestamp
    @Column(name = "dt_reserva_cadastro", updatable = false, nullable = false)
    private LocalDateTime dt_reserva_cadastro;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;

}
