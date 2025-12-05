package com.morangosdoamor.WebCursos.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável pelo envio de emails da plataforma.
 * 
 * Princípios aplicados:
 * - Clean Architecture: encapsula lógica de envio de email
 * - Single Responsibility: focado apenas em operações de email
 * - Logging estruturado para rastreabilidade
 * 
 * Responsabilidades:
 * - Enviar emails simples (text/plain)
 * - Configurar remetente padrão da plataforma
 * - Tratar erros de envio com logging apropriado
 * 
 * Em ambiente de desenvolvimento, usa MailHog como servidor SMTP sandbox.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@webcursos.com}")
    private String fromAddress;

    /**
     * Envia um email simples (texto plano).
     * 
     * @param to Endereço de email do destinatário
     * @param subject Assunto do email
     * @param body Corpo do email (texto plano)
     * @throws MailException se ocorrer erro no envio
     */
    public void sendEmail(String to, String subject, String body) {
        log.info("Preparando envio de email para: {} com assunto: {}", to, subject);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", to);
        } catch (MailException e) {
            log.error("Falha ao enviar email para: {}. Erro: {}", to, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Envia email de conclusão de curso com recomendação.
     * Método de conveniência que formata o assunto automaticamente.
     * 
     * @param to Endereço de email do destinatário
     * @param cursoNome Nome do curso concluído
     * @param mensagemRecomendacao Mensagem personalizada gerada pela IA
     */
    public void sendCourseCompletionEmail(String to, String cursoNome, String mensagemRecomendacao) {
        String subject = String.format("Parabéns pela conclusão do curso %s!", cursoNome);
        sendEmail(to, subject, mensagemRecomendacao);
    }
}


