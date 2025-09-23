package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.service.LivroService;
import com.bibliotecaproject.api.repository.LivroRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/livros")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LivroController {

    private final LivroService livroService;
    private final LivroRepository livroRepository;

    public LivroController(LivroService livroService, LivroRepository livroRepository) {
        this.livroService = livroService;
        this.livroRepository = livroRepository;
    }

    @PostMapping("/{isbn}/capa")
    public ResponseEntity<?> uploadCapa(@PathVariable String isbn, @RequestParam("file") MultipartFile file) {
        try {
            Livro atualizado = livroService.salvarCapa(isbn, file);
            String imageUrl = "/livros/"+isbn+"/capa";
            return ResponseEntity.ok().body(imageUrl);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem");
        }
    }

    @GetMapping("/{isbn}/capa")
    public ResponseEntity<Resource> serveCapa(@PathVariable String isbn) {
        Livro livro = livroRepository.findById(isbn).orElse(null);
        if(livro == null || livro.getCapaFilename() == null) {
            return ResponseEntity.notFound().build();
        }
        Path filePath = livroService.getCaminhoCapa(livro);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if(!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if(contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch(MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
