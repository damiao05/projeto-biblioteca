package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
public class FuncionarioService implements OperacoesGerente {

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    //private final EmprestimoRespository emprestimoRepository;
    private final EmailService emailService;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    private final Path uploadDir;
    private static final String[] ALLOWED_MIMES = {"image/jpeg", "image/jpg", "image/png", "image/webp"};

    @Autowired
    public FuncionarioService(UsuarioRepository usuarioRepository, LivroRepository livroRepository, @Value("${file.upload-dir}") String uploadDir, EmailService emailService, S3Service s3Service, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.s3Service = s3Service;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();

    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de uploads.", e);
        }
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public void deletarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }

//    public String salvarArquivoCapa(MultipartFile file, String isbn)  throws IOException {
//
//        return s3Service.uploadCapa(file, isbn);
//    }

    public String salvarArquivoCapa(MultipartFile file) throws IOException {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
        if(file.getSize() == 0) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        boolean ok = false;
        if (contentType != null) {

            for(String allowed : ALLOWED_MIMES) {
                if(allowed.equalsIgnoreCase(contentType)) {ok = true; break;}
            }
        }
        if(!ok) {
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

        String original = file.getOriginalFilename();
        String ext = "";
        if(original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String generatedFilename = UUID.randomUUID().toString() + ext;

        Path target = this.uploadDir.resolve(generatedFilename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e) {
            throw new IOException("Falha ao salvar arquivo", e);
        }

        return generatedFilename;
    }

    @Override
    public Livro cadastrarLivro(Livro livro, MultipartFile file) throws IOException {

        //if(livro.getCapaFilename() != null && !livro.getCapaFilename().isBlank()) {
          //  Path antigo = this.uploadDir.resolve(livro.getCapaFilename());
            //try {
              //  Files.deleteIfExists(antigo);
            //} catch(IOException ex) {
              //  ex.printStackTrace();
            //}
        //}

        livro.setCapaFilename(salvarArquivoCapa(file));
        return livroRepository.save(livro);

    }

    @Override
    public Livro editarLivro(String isbn, Livro livroAtualizado, MultipartFile file) throws IOException {
        Livro livroExistente = livroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado"));

        livroExistente.setTitulo(livroAtualizado.getTitulo());
        livroExistente.setAutor(livroAtualizado.getAutor());
        livroExistente.setCategoria(livroAtualizado.getCategoria());
        livroExistente.setEditora(livroAtualizado.getEditora());
        livroExistente.setSinopse(livroAtualizado.getSinopse());
        livroExistente.setQtd_total(livroAtualizado.getQtd_total());

        if(file != null && !file.isEmpty()) {
            String capaAntiga = livroAtualizado.getCapaFilename();
            String novaCapaFilename = salvarArquivoCapa(file);

            livroExistente.setCapaFilename(novaCapaFilename);

            if(capaAntiga != null && !capaAntiga.isBlank()) {
                Path antigoPath = this.uploadDir.resolve(capaAntiga);
                Files.deleteIfExists(antigoPath);
            }
        }

        return livroRepository.save(livroExistente);
    }

    @Override
    public Usuario criarFuncionario(Usuario novoFuncionario) {
        String senhaTemporaria = gerarSenhaAleatoria(10);

        if(novoFuncionario.getEmail() == null || novoFuncionario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Dados de login (e-mail) obrigatórios");
        }

        String senhaCriptografada = passwordEncoder.encode(senhaTemporaria);
        novoFuncionario.setSenha(senhaCriptografada);

        Usuario usuarioSalvo = usuarioRepository.save(novoFuncionario);

        try {
            emailService.enviarSenhaTemporaria(
                    usuarioSalvo.getEmail(),
                    senhaTemporaria
            );
        } catch (Exception e) {
            System.out.println("--------------");
            System.err.println("Erro ao enviar e-mail. Por favor,verifique as credenciais");
            System.err.println("ERRO: " + e.getMessage());
            System.out.println("--------------");
        }

        return usuarioSalvo;
    }

    @Override
    public List<Usuario> listarFuncionarios() {
        return null;
    }

    private String gerarSenhaAleatoria(int length) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();

        StringBuilder senha = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(caracteres.length());

            senha.append(caracteres.charAt(randomIndex));
        }

        return senha.toString();
    }

}
