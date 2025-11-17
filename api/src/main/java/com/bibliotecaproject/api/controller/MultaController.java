package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Multa;
import com.bibliotecaproject.api.service.FuncionarioService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/multas")
public class MultaController {

    @Autowired
    private FuncionarioService funcionarioService;

    @PutMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','GERENTE)")
    public ResponseEntity<Multa> registrarPagamento(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
            ) {

        Multa registrarDevolucao = funcionarioService.registrarPagamento(id, data);

        return  ResponseEntity.ok(registrarDevolucao);

    }

}
