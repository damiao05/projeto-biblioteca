    package com.bibliotecaproject.api.domain.usuario;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.util.UUID;

    @Table(name = "usuario")
    @Entity
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Usuario {
        @Id
        @GeneratedValue
        private UUID id;

        private String nome;
        private String cpf;
        private LocalDate dataNascimento;

        @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private Login login;

        public void setLogin(Login login) {
            this.login = login;
            if (login != null) {
                login.setUsuario(this);
            }
        }
    }
