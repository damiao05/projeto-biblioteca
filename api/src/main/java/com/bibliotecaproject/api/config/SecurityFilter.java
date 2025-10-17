package com.bibliotecaproject.api.config;

import com.bibliotecaproject.api.repository.UsuarioRepository;
import com.bibliotecaproject.api.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Lembre-se que esta classe NÃO deve ter a anotação @Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("\n--- [SECURITY FILTER] INICIANDO FILTRO PARA A REQUISIÇÃO: " + request.getRequestURI());

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            System.out.println("--- [SECURITY FILTER] Token encontrado no cabeçalho.");
            try {
                var subject = tokenService.getSubject(tokenJWT);
                System.out.println("--- [SECURITY FILTER] Token válido. Subject (email): " + subject);

                var optionalUsuario = usuarioRepository.findByEmail(subject);

                if (optionalUsuario.isPresent()) {
                    var usuario = optionalUsuario.get();
                    System.out.println("--- [SECURITY FILTER] Usuário encontrado no banco: " + usuario.getUsername());

                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("--- [SECURITY FILTER] Usuário autenticado com sucesso no contexto de segurança!");

                } else {
                    System.err.println("--- [SECURITY FILTER] ERRO: Token válido, mas usuário não encontrado no banco com o email: " + subject);
                }
            } catch (Exception e) {
                System.err.println("--- [SECURITY FILTER] ERRO na validação do token: " + e.getMessage());
            }

        } else {
            System.out.println("--- [SECURITY FILTER] Nenhum token JWT encontrado no cabeçalho Authorization.");
        }

        // Continua o fluxo da requisição
        filterChain.doFilter(request, response);
        System.out.println("--- [SECURITY FILTER] FINALIZANDO FILTRO PARA A REQUISIÇÃO: " + request.getRequestURI() + "\n");
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}