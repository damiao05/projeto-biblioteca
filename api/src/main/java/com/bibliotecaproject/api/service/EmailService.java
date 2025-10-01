package com.bibliotecaproject.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarSenhaTemporaria(String paraEmail, String senhaTemporaria) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(paraEmail);

        mensagem.setSubject("Sua conta na Biblioteca LitHub foi criada!");

        mensagem.setText(
                "Olá!\n\n" +
                "Sua conta foi criada em nosso sistema da biblioteca LitHub.\n\n" +
                "Seu e-mail de login é: " + paraEmail + "\n" +
                "Sua senha temporária é: " + senhaTemporaria + "\n\n" +
                "É altamente recomendável que você altere sua senha no primeiro acesso.\n\n" +
                "Atenciosamente,\n" +
                "Equipe LitHub."
        );

        mailSender.send(mensagem);

    }
}
