package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

//Regras de negócio de Livro
@Service
public class LivroService {

    private final LivroRepository livroRepository;

    // injeta a propriedade file.upload-dir
    private final Path uploadDir;

    // aceita apenas estes types
    private static final String[] ALLOWED_MIMES = {"image/jpeg", "image/jpg", "image/png", "image/webp"};

    public LivroService(LivroRepository livroRepository,
                        @Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.livroRepository = livroRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();

        // cria diretório se não existir
        Files.createDirectories(this.uploadDir);
    }

    public Optional<Livro> mostrarLivro(String isbn){
        return livroRepository.findByIsbn(isbn);
    }

    public List<Livro> exibirLivros(String categoria) {
        Sort sort = Sort.by(Sort.Direction.ASC, "titulo");

        if (categoria != null && !categoria.isBlank()) {
            return livroRepository.findByCategoria(categoria, sort);
        } else {
            return livroRepository.findAll(sort);
        }
    }

    //public Livro salvarLivro(Livro livro){
       // return livroRepository.save(livro);
    //}

    public List<Livro> pesquisarLivros(String input) {return livroRepository.findByInput(input);}

    public Livro salvarCapa(String isbn, MultipartFile file)  throws IOException {
        // buscar livro
        Livro livro = livroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        // validações
        if(file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
        if(file.getSize() == 0) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        // checar conteúdo de maneira segura
        String contentType = file.getContentType();
        boolean ok = false;
        if (contentType != null) {

            for(String allowed : ALLOWED_MIMES) {
                if(allowed.equalsIgnoreCase(contentType)) {ok = true; break;}
            }
        }
        if(!ok) {
            // tentar detecção por nome
            String detected = URLConnection.guessContentTypeFromName(file.getOriginalFilename());
            ok = false;
            if(detected != null) {
                for(String allowed : ALLOWED_MIMES) {
                    if(allowed.equalsIgnoreCase(detected)) {ok = true; break;}
                }
            }
        }

        if(!ok) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido! Apenas imagens (jpg/jpeg/png/webp).");
        }

        // gerar nome seguro: UUID + extensão original
        String original = file.getOriginalFilename();
        String ext = "";
        if(original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String generatedFilename = UUID.randomUUID().toString() + ext;

        // caminho final dentro da pasta
        Path target = this.uploadDir.resolve(generatedFilename);

        // salvar o arquivo (substitui se já existir)
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e) {
            throw new IOException("Falha ao salvar arquivo", e);
        }

        // remover arquivo antigo, se houver
        if(livro.getCapaFilename() != null && !livro.getCapaFilename().isBlank()) {
            Path antigo = this.uploadDir.resolve(livro.getCapaFilename());
            try {
                Files.deleteIfExists(antigo);
            } catch(IOException ex) {
                // logar e continuar
                ex.printStackTrace();
            }
        }

        // persistir apenas o nome do arquivo
        livro.setCapaFilename(generatedFilename);
        return livroRepository.save(livro);
    }

    public Path getCaminhoCapa(Livro livro) {
        if(livro.getCapaFilename() == null) return null;
        return this.uploadDir.resolve(livro.getCapaFilename());
    }

}
