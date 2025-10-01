package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Login;
import com.bibliotecaproject.api.repository.LoginRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(LoginRepository loginRepository, PasswordEncoder passwordEncoder) {

        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Login> autenticar(String email, String senha) {
        Optional<Login> loginOpt = loginRepository.findByEmail(email);

        if(loginOpt.isPresent()) {
            boolean senhasIguais = passwordEncoder.matches(senha, loginOpt.get().getSenha());

            if(senhasIguais) {
                return loginOpt;
            }
        }

        return Optional.empty();
    }

}
