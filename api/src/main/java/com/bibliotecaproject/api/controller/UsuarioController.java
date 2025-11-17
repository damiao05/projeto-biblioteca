package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.dto.AtualizarSenhaDTO;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.service.FuncionarioService;
import com.bibliotecaproject.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FuncionarioService funcionarioService;

    public UsuarioController(UsuarioService usuarioService, FuncionarioService funcionarioService) {
        this.usuarioService = usuarioService;
        this.funcionarioService = funcionarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.salvar(usuario);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @PostMapping("/funcionario")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Usuario> criarFuncionario(@RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.criarFuncionario(usuario);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @GetMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> getPerfil(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(usuarioLogado);
    }

//    @GetMapping
//    public List<Usuario> listar() {
//        return usuarioService.listarTodos();
//    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public Usuario atualizar(@AuthenticationPrincipal Usuario usuarioLogado, @RequestBody Usuario usuario) {
        return usuarioService.atualizar(usuarioLogado, usuario);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
//        usuarioService.deletar(id);
//        return ResponseEntity.noContent().build();
//    }

    @PutMapping("/atualizar-senha")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> atualizarSenha(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AtualizarSenhaDTO dados
            ) {
        try {
            usuarioService.alterarSenha(usuarioLogado, dados);
            return ResponseEntity.ok("Senha atualizada com sucesso!");
        } catch (SecurityException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
