package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Login;
import com.bibliotecaproject.api.repository.LoginRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final LoginRepository loginRepository;

    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public Optional<Login> autenticar(String email, String senha) {
        return loginRepository.findByEmailAndSenha(email, senha);
    }

}
