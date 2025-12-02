package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/favoritos")
@RestController
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @PostMapping("/favoritar")
    @PreAuthorize("isAuthenticated()")
    public void favoritarLivro(@RequestBody UUID idlivro, @AuthenticationPrincipal Usuario usuarioLogado) {
        favoritoService.favoritarLivro(idlivro, usuarioLogado);
    }

    @PostMapping("/retirar-favorito")
    @PreAuthorize("isAuthenticated()")
    public void retirarFavorito(@RequestBody UUID idlivro, @AuthenticationPrincipal Usuario usuarioLogado) {
        favoritoService.retirarFavorito(idlivro, usuarioLogado);
    }

}
