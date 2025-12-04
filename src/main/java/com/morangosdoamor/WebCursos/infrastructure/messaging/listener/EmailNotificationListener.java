package com.morangosdoamor.WebCursos.infrastructure.messaging.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;

/**
 * Listener para envio de notificações por email.
 * 
 * Este componente consome mensagens da fila curso.concluido.email-notification
 * e processa eventos de conclusão de curso para enviar emails de notificação.
 * 
 * NOTA: Esta é uma implementação skeleton para ser completada no Workstream 2.
 * O envio real de emails deve ser implementado posteriormente.
 * 
 * Responsabilidades futuras (Workstream 2):
 * - Enviar email de parabéns para alunos aprovados
 * - Enviar email de incentivo para alunos reprovados
 * - Notificar sobre novos cursos liberados
 * - Enviar certificado de conclusão (se aprovado)
 */
@Component
public class EmailNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);

    /**
     * Processa eventos de conclusão de curso para envio de notificações por email.
     * 
     * @param event Evento de conclusão de curso contendo dados do aluno e curso
     */
    @RabbitListener(queues = "${webcursos.rabbitmq.queue.email-notification}")
    public void processEmailNotification(CursoConcluidoEvent event) {
        log.info("=== Email Notification Listener ===");
        log.info("Recebido evento de conclusão de curso para notificação por email");
        log.info("Destinatário: {} <{}>", event.alunoNome(), event.alunoEmail());
        log.info("Curso: {} - {}", event.cursoCodigo(), event.cursoNome());
        log.info("Nota Final: {} | Aprovado: {}", event.notaFinal(), event.aprovado());
        log.info("Data Conclusão: {}", event.dataConclusao());
        
        // TODO: Implementar lógica de envio de email (Workstream 2)
        // 1. Selecionar template de email apropriado
        // 2. Preencher template com dados do evento
        // 3. Enviar email via serviço de email (SMTP, SendGrid, etc.)
        // 4. Registrar envio no histórico de notificações
        
        if (event.aprovado()) {
            log.info("Preparando email de PARABÉNS para o aluno...");
            // TODO: Enviar email de parabéns com:
            // - Certificado de conclusão
            // - Lista de cursos liberados (3 novos cursos)
            // - Recomendações de próximos passos
        } else {
            log.info("Preparando email de INCENTIVO para o aluno...");
            // TODO: Enviar email de incentivo com:
            // - Mensagem motivacional
            // - Sugestões de revisão
            // - Opção de refazer o curso
        }
        
        log.info("=== Fim do processamento Email Notification ===");
    }
}
