    package com.bibliotecaproject.api.domain.usuario;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.CreationTimestamp;
    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.Collection;
    import java.util.List;
    import java.util.UUID;

    @Table(name = "usuario")
    @Entity
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Usuario implements UserDetails {
        @Id
        @GeneratedValue
        private UUID id;

        private String nome;
        private String cpf;
        private LocalDate dataNascimento;

        @CreationTimestamp
        @Column(name = "data_hora_cadastro", updatable = false)
        private LocalDateTime dataHoraCadastro;

        @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private Login login;

        @Enumerated(EnumType.STRING)
        private Role role;

        public void setLogin(Login login) {
            this.login = login;
            if (login != null) {
                login.setUsuario(this);
            }
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        @Override
        public String getPassword() {
            return this.login.getSenha();
        }

        @Override
        public String getUsername() {
            return this.login.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
