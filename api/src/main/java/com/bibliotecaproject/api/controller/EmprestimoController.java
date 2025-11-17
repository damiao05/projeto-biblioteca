package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Emprestimo;
import com.bibliotecaproject.api.service.EmprestimoService;
import com.bibliotecaproject.api.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final FuncionarioService funcionarioService;

    public  EmprestimoController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','GERENTE')")
    public ResponseEntity<Emprestimo> registrarEmprestimo(
            @RequestBody Emprestimo emprestimo) {

        Emprestimo emprestimoRegistrado = funcionarioService.registrarEmprestimo(emprestimo);

        return ResponseEntity.ok(emprestimoRegistrado);

    }

    @PutMapping("/{id}/devolucao")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','GERENTE')")
    public ResponseEntity<Emprestimo> registrarDevolucao(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        Emprestimo devolucaoRegistrada = funcionarioService.registrarDevolucao(id, data);

        return ResponseEntity.ok(devolucaoRegistrada);

    }

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public List<Emprestimo> listarEmprestimos() {
        return funcionarioService.listarEmprestimos();
    }

    @GetMapping("/{id}/buscar")
    @PreAuthorize("isAuthenticated()")
    public Optional<Emprestimo> buscarEmprestimo(@PathVariable UUID id) {
        return emprestimoService.buscarEmprestimo(id);
    }

}
