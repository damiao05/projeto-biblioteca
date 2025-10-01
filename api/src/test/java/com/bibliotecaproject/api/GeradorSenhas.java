package com.bibliotecaproject.api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GeradorSenhas {

    @Test
    void  gerarSenha(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String textoSenha = "senha123";

        String hash = passwordEncoder.encode(textoSenha);

        System.out.println("==========");
        System.out.println("Senha em texto: " + textoSenha);
        System.out.println("Senha BCRYPT: " + hash);
        System.out.println("==========");
    }

}
