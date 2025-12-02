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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reserva")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/{id_livro}/{dataReserva}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> criarReserva(@AuthenticationPrincipal Usuario usuarioLogado, @PathVariable UUID id_livro, @PathVariable LocalDate dataReserva){
        Reserva novaReserva = reservaService.criarReserva(usuarioLogado, id_livro, dataReserva);
        return new ResponseEntity<>(novaReserva, HttpStatus.CREATED);
    }

    @PostMapping("/{idLivro}/lista-espera")
    @PreAuthorize("isAuthenticated()")
    public void entrarEspera(@AuthenticationPrincipal Usuario usuarioLogado, @PathVariable UUID idLivro){
        reservaService.entrarEspera(usuarioLogado, idLivro);
    }

    @PostMapping("/{idReserva}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public void cancelarReserva(@PathVariable UUID idReserva){
        reservaService.cancelarReserva(idReserva);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reserva>> listarReservas(@AuthenticationPrincipal Usuario usuarioLogado, @RequestParam(name = "reserva", required = false) UUID reserva){
        List<Reserva> reservas = reservaService.listarReservas(usuarioLogado, reserva);

        if(reservas.isEmpty()) {
            return  ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(reservas);
        }
    }

}
