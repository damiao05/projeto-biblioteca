package com.bibliotecaproject.api.service;

import com.bibliotecaproject.api.domain.usuario.Livro;
import com.bibliotecaproject.api.domain.usuario.Reserva;
import com.bibliotecaproject.api.domain.usuario.Usuario;
import com.bibliotecaproject.api.repository.LivroRepository;
import com.bibliotecaproject.api.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public Reserva atualizarStatus(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

}
