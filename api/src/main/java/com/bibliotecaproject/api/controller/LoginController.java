package com.bibliotecaproject.api.controller;

import com.bibliotecaproject.api.domain.dto.AuthenticationDTO;
import com.bibliotecaproject.api.domain.dto.TokenDTO;
import com.bibliotecaproject.api.domain.usuario.Login;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.service.LoginService;
import com.bibliotecaproject.api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequestMapping("/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String senha = loginData.get("senha");

        var loginOpt = loginService.autenticar(email, senha);

        if (loginOpt.isPresent()) {
            return ResponseEntity.ok(loginOpt.get().getUsuario());
        } else {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }

}


