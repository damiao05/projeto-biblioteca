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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Reserva criarReserva(Usuario usuario, UUID id_livro, LocalDate dataReserva) {
        Livro livro = livroRepository.findById(id_livro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Reserva novaReserva =  new Reserva();

        novaReserva.setUsuario(usuario);
        novaReserva.setLivro(livro);
        novaReserva.setDtReserva(dataReserva);
        novaReserva.setStatus("ATIVA");
        livro.setQtd_disponivel(livro.getQtd_disponivel()-1);

        livroRepository.save(livro);
        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public void entrarEspera(Usuario usuarioLogado, UUID idLivro) {
        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Reserva espera = new Reserva();

        espera.setUsuario(usuarioLogado);
        espera.setLivro(livro);
        espera.setStatus("ESPERA");

        reservaRepository.save(espera);
    }

    @Transactional
    public void cancelarReserva(UUID idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva inexistente"));

        Livro livro = livroRepository.findById(reserva.getLivro().getId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        livro.setQtd_disponivel(livro.getQtd_disponivel()+1);

        livroRepository.save(livro);
        reserva.setStatus("CANCELADA");
        reservaRepository.save(reserva);
    }

    @Transactional
    public List<Reserva> listarReservas(Usuario usuarioLogado, UUID reserva) {
        Role cargo = usuarioLogado.getRole();
        UUID idUsuario = usuarioLogado.getId();

        if (cargo == Role.BIBLIOTECARIO || cargo == Role.GERENTE) {

            if (reserva != null) {
                return reservaRepository.findReservaById(reserva)
                        .map(List::of)
                        .orElseGet(ArrayList::new);
            } else {
                return reservaRepository.findByDtReservaIsNotNullOrderByDtReservaDesc();
            }
        } else if (cargo == Role.CLIENTE) {
            return reservaRepository.findAllByUsuarioId(idUsuario);

        }

        return new ArrayList<>();
    }

    @Transactional
    public Reserva atualizarStatus(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

}
