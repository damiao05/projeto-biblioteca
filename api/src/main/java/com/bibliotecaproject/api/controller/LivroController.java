package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.service.FuncionarioService;
import com.bibliotecaproject.api.service.LivroService;
import com.bibliotecaproject.api.repository.LivroRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/livros")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LivroController {

    private final LivroService livroService;
    private final LivroRepository livroRepository;
    private final FuncionarioService funcionarioService;

    public LivroController(LivroService livroService, LivroRepository livroRepository, FuncionarioService funcionarioService) {
        this.livroService = livroService;
        this.livroRepository = livroRepository;
        this.funcionarioService = funcionarioService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO', 'GERENTE')")
    public ResponseEntity<?> cadastrarLivro(
            @RequestPart("livro") Livro livro,
            @RequestPart("file") MultipartFile file) {

        try {
            Livro livroSalvo = funcionarioService.cadastrarLivro(livro, file);
            return new ResponseEntity<>(livroSalvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo da capa.");
        }
    }

    @PutMapping("/{isbn}")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO', 'GERENTE')")
    public ResponseEntity<?> editarLivro (
            @PathVariable String isbn,
            @RequestPart Livro dadosLivro,
            @RequestPart(value = "file", required = false)  MultipartFile file) {
        try {
            Livro livroAtualizado = funcionarioService.editarLivro(isbn, dadosLivro, file);
            return ResponseEntity.ok(livroAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar a nova capa.");
        }
    }
    
    @GetMapping("/{isbn}")
    public ResponseEntity<Livro> exibir(@PathVariable String isbn){
        Optional<Livro> livroOpt = livroService.mostrarLivro(isbn);

        if(livroOpt.isPresent()){
            return ResponseEntity.ok(livroOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pesquisa")
    public ResponseEntity<List<Livro>> pesquisarLivros(@RequestParam("input") String input) {
        List<Livro> livrosPesquisados = livroService.pesquisarLivros(input);

        if(livrosPesquisados.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(livrosPesquisados);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Livro>> exibirLivros(@RequestParam(name = "categoria", required = false) String categoria){
        List<Livro> livros = livroService.exibirLivros(categoria);

        System.out.println("Processo exibir: " + livros);

        if(livros.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(livros);
        }
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
        Livro livro = livroRepository.findByIsbn(isbn).orElse(null);
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
