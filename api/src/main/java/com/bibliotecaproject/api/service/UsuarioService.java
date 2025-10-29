package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.dto.AtualizarSenhaDTO;
import com.bibliotecaproject.api.domain.usuario.Role;
import com.bibliotecaproject.api.domain.usuario.Usuario;
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
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getRole() == null) {
            usuario.setRole(Role.CLIENTE);
        }

        String senhaComHash = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaComHash);

        return usuarioRepository.save(usuario);

    }

    @Transactional
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

    @Transactional
    public Usuario atualizar(Usuario usuarioLogado, Usuario usuarioAtualizado) {
        usuarioLogado.setNome(usuarioAtualizado.getNome());
        usuarioLogado.setCpf(usuarioAtualizado.getCpf());
        usuarioLogado.setDataNascimento(usuarioAtualizado.getDataNascimento());
        return usuarioRepository.save(usuarioLogado);
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

    @Transactional
    public void alterarSenha(Usuario usuarioLogado, AtualizarSenhaDTO dados) {
        if(!passwordEncoder.matches(dados.senhaAtual(), usuarioLogado.getPassword())) {
            throw new SecurityException("Senha atual incorreta!");
        }

        if(!dados.senhaNova().equals(dados.confirmacaoNovaSenha())) {
            throw new IllegalArgumentException("As senhas estão divergentes!");
        }

        String novoHash = passwordEncoder.encode(dados.senhaNova());

        usuarioLogado.setSenha(novoHash);

        usuarioRepository.save(usuarioLogado);
    }
}


