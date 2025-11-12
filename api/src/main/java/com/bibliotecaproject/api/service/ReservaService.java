package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Reserva;
import com.bibliotecaproject.api.domain.usuario.Role;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;

    public ReservaService(ReservaRepository reservaRepository, LivroRepository livroRepository) {
        this.reservaRepository = reservaRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public Reserva criarReserva(Usuario usuario, UUID id_livro) {
        Livro livro = livroRepository.findById(id_livro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Reserva novaReserva =  new Reserva();

        novaReserva.setUsuario(usuario);
        novaReserva.setLivro(livro);
        novaReserva.setStatus("ATIVA");

        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public List<Reserva> listarReservas(Usuario usuarioLogado, UUID reserva) {
        Role cargo = usuarioLogado.getRole();

        System.out.println("O cargo é: " + cargo);

        if (cargo == Role.BIBLIOTECARIO || cargo == Role.GERENTE) {

            if (reserva != null) {
                return reservaRepository.findReservaById(reserva)
                        .map(List::of)
                        .orElseGet(ArrayList::new);
            } else {
                return reservaRepository.findByDtReservaIsNotNullOrderByDtReservaDesc();
            }
        }

        return new ArrayList<>();
    }

    @Transactional
    public Reserva atualizarStatus(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

}
