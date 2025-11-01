package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Reserva;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.service.ReservaService;
import com.bibliotecaproject.api.service.ReservaService;
import com.bibliotecaproject.api.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reserva")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/{id_livro}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> criarReserva(@AuthenticationPrincipal Usuario usuarioLogado, @PathVariable UUID id_livro){
        Reserva novaReserva = reservaService.criarReserva(usuarioLogado, id_livro);
        return new ResponseEntity<>(novaReserva, HttpStatus.CREATED);
    }

}
