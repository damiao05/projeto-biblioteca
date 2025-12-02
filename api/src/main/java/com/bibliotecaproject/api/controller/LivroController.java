package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.service.FuncionarioService;
import com.bibliotecaproject.api.service.LivroService;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.service.S3Service;
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
    private final S3Service s3Service;

    public LivroController(LivroService livroService, LivroRepository livroRepository, FuncionarioService funcionarioService, S3Service s3Service) {
        this.livroService = livroService;
        this.livroRepository = livroRepository;
        this.funcionarioService = funcionarioService;
        this.s3Service = s3Service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO', 'GERENTE')")
    public ResponseEntity<?> cadastrarLivro(
            @RequestPart("livro") Livro livro,
            @RequestPart(value = "capa_data", required = false) MultipartFile file,
            @RequestParam(value = "capa_url", required = false) String capaUrl) {

        try {
            Livro livroSalvo = funcionarioService.cadastrarLivro(livro, file, capaUrl);
            return new ResponseEntity<>(livroSalvo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo da capa.");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO', 'GERENTE')")
    public ResponseEntity<?> editarLivro (
            @PathVariable UUID id,
            @RequestPart Livro dadosLivro,
            @RequestPart(value = "file", required = false)  MultipartFile file) {
        try {
            Livro livroAtualizado = funcionarioService.editarLivro(id, dadosLivro, file);
            return ResponseEntity.ok(livroAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar a nova capa.");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Livro> exibir(@PathVariable UUID id){
        Optional<Livro> livroOpt = livroService.mostrarLivro(id);

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

    @PostMapping("/{id}/capa")
    public ResponseEntity<?> uploadCapa(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = s3Service.uploadCapa(file, id);

            Livro atualizado = livroService.salvarCapa(id, imageUrl);

            return ResponseEntity.ok(imageUrl);
        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem");
        }
    }

    @GetMapping("/{id}/capa")
    public ResponseEntity<Resource> serveCapa(@PathVariable UUID id) {
        Livro livro = livroRepository.findById(id).orElse(null);
        if(livro == null || livro.getCapaFilename() == null) {
            return ResponseEntity.notFound().build();
        }

        String capaFilename = livro.getCapaFilename();
        Resource resource;
        String contentType;
        Path filePath = null;

        try {

            if(capaFilename.toLowerCase().startsWith("http://") || capaFilename.toLowerCase().startsWith("https://")) {
                resource = new UrlResource(capaFilename);
                contentType = "image/jpeg";

            } else {
                filePath = livroService.getCaminhoCapa(livro);
                resource = new UrlResource(filePath.toUri());

                if(!resource.exists() || !resource.isReadable()) {
                    return ResponseEntity.notFound().build();
                }

                contentType = Files.probeContentType(filePath);
                if(contentType == null) {
                    contentType = "application/octet-stream";
                }
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO', 'GERENTE')")
    public void deletarLivro(@PathVariable UUID id) {
        funcionarioService.deletarLivro(id);
    }

}
