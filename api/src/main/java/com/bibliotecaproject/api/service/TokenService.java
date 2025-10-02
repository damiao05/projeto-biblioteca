package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-ms}")
    private long expirationMs;

    public String gerarToken(Usuario usuario){
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + expirationMs);

        SecretKey chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setIssuer("API LitHub")
                .setSubject(usuario.getLogin().getEmail())
                .claim("role", usuario.getRole().name())
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(chave, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String tokenJWT){
        try {
            SecretKey chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(chave)
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

}
