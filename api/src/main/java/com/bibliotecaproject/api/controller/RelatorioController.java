package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.service.RelatorioService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/livros-emprestados")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','GERENTE')")
    public String relatorioLivrosMaisEmprestados() {
        return relatorioService.gerarRelatorioLivrosMaisEmprestados();

    }

    @GetMapping("/multas-ativas")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO', 'GERENTE')")
    public String relatorioMultasAtivas() {
        return relatorioService.gerarRelatorioMultasAtivas();

    }

}
