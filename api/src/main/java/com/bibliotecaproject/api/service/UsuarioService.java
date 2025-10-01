package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Login;
import com.bibliotecaproject.api.domain.usuario.Role;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.LoginRepository;
import com.bibliotecaproject.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final LoginRepository loginRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, LoginRepository loginRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {

        if (usuario.getRole() == null) {
            usuario.setRole(Role.CLIENTE);
        }

        if (usuario.getLogin() != null) {
            String senhaComHash = passwordEncoder.encode(usuario.getLogin().getSenha());
            usuario.getLogin().setSenha(senhaComHash);

            usuario.setLogin(usuario.getLogin());
        }
        return usuarioRepository.save(usuario);

    }

    @Transactional
    public Usuario criarFuncionario(Usuario novoFuncionario) {

        String senhaTemporaria = gerarSenhaAleatoria(10);

        if(novoFuncionario.getLogin() == null || novoFuncionario.getLogin().getEmail() == null) {
            throw new IllegalArgumentException("Dados de login (e-mail) obrigatórios");
        }

        String senhaCriptografada = passwordEncoder.encode(senhaTemporaria);
        novoFuncionario.getLogin().setSenha(senhaCriptografada);

        novoFuncionario.setLogin(novoFuncionario.getLogin());

        Usuario usuarioSalvo = usuarioRepository.save(novoFuncionario);

        try {
            emailService.enviarSenhaTemporaria(
                    usuarioSalvo.getLogin().getEmail(),
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

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void deletar(UUID id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario atualizar(UUID id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id);
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        usuarioExistente.setDataNascimento(usuarioAtualizado.getDataNascimento());
        return usuarioRepository.save(usuarioExistente);
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


