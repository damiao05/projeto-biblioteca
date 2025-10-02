package com.bibliotecaproject.api.domain.usuario;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "login")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String email;
    private String senha;
    private String telefone;

    @Column(name = "inativo")
    private Boolean inativo = false;

    @CreationTimestamp
    @Column(name = "data_hora_criacao", updatable = false)
    private LocalDateTime dataHoraCriacao;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;
}
