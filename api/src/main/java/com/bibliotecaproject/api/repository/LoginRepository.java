package com.bibliotecaproject.api.repository;

import com.bibliotecaproject.api.domain.usuario.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoginRepository extends JpaRepository<Login, UUID> { }
