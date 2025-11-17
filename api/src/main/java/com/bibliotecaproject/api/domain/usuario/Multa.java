package com.bibliotecaproject.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "multa")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Multa {

    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal valorMulta;
    private LocalDate dtGeracaoMulta;
    private String statusPagamento;
    private LocalDate dtPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmprestimo", nullable = false)
    private Emprestimo emprestimo;

}
