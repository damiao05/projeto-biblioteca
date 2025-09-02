package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Login;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.LoginRepository;
import com.bibliotecaproject.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final LoginRepository loginRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, LoginRepository loginRepository) {
        this.usuarioRepository = usuarioRepository;
        this.loginRepository = loginRepository;
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {

        if (usuario.getLogin() != null) {
            usuario.setLogin(usuario.getLogin());
        }
        return usuarioRepository.save(usuario);

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
}
