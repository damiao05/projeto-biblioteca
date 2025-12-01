package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Avaliacao;
import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.AvaliacaoRepository;
import com.bibliotecaproject.api.service.AvaliacaoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void registrarAvaliacao (@AuthenticationPrincipal Usuario usuarioLogado, @RequestBody Avaliacao avaliacao)
    {
        avaliacaoService.registrarAvalicao(usuarioLogado, avaliacao);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Avaliacao> listarAvaliacoes(@RequestParam UUID idLivro)
    {
        return avaliacaoService.listarAvaliacoes(idLivro);
    }

}
