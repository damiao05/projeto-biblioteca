package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.EmprestimoRepository;
import com.bibliotecaproject.api.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    public Optional<Livro> mostrarLivro(UUID id){
        return livroRepository.findById(id);
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

    public List<Livro> pesquisarLivros(String input) {
        return livroRepository.findByInput(input);

    }

    public void deletarLivro(UUID id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        boolean emprestimosAtivos = emprestimoRepository.existsByLivroAndDtDevolucaoRealIsNull(livro);

        if(emprestimosAtivos) {
            throw new RuntimeException("Não é possível deletar um usuário que possui empréstimos ativos!");

        }

        livroRepository.deleteById(id);

    }

    public Livro salvarCapa(UUID id, String imageUrl)  throws IOException {

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        livro.setCapaFilename(imageUrl);
        return livroRepository.save(livro);
    }

    public Path getCaminhoCapa(Livro livro) {
        if(livro.getCapaFilename() == null) return null;
        return this.uploadDir.resolve(livro.getCapaFilename());
    }

}
