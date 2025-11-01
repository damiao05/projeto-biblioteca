package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}
