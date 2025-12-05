package com.morangosdoamor.WebCursos.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.application.service.EmailService;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.EmailNotificationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener para envio de notificações por email.
 * 
 * Este componente consome mensagens da fila curso.concluido.email-notification
 * que são publicadas pelo AiRecommendationListener após processar a recomendação.
 * 
 * Fluxo:
 * 1. Recebe EmailNotificationEvent (publicado pelo AI Listener)
 * 2. Envia email via EmailService
 * 
 * Este design garante:
 * - Emails são enviados apenas após processamento de IA
 * - Retry independente para falhas de email
 * - Sem duplicação de emails
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationListener {

    private final EmailService emailService;

    /**
     * Processa eventos de notificação para envio de email.
     * 
     * @param event Evento de notificação contendo dados do email a ser enviado
     */
    @RabbitListener(queues = "${webcursos.rabbitmq.queue.email-notification}")
    public void processEmailNotification(EmailNotificationEvent event) {
        log.info("=== Email Notification Listener ===");
        log.info("Recebido evento de notificação para envio de email");
        log.info("Destinatário: {} <{}>", event.nomeDestinatario(), event.destinatario());
        log.info("Assunto: {}", event.assunto());
        log.info("AlunoId: {} | CursoId: {}", event.alunoId(), event.cursoId());

        try {
            emailService.sendEmail(
                    event.destinatario(),
                    event.assunto(),
                    event.corpo()
            );

            log.info("Email enviado com sucesso para: {}", event.destinatario());
        } catch (Exception e) {
            log.error("Erro ao enviar email para: {}", event.destinatario(), e);
            throw e; // Re-throw para que a mensagem vá para DLQ se necessário
        }

        log.info("=== Fim do processamento Email Notification ===");
    }
}
