package com.morangosdoamor.WebCursos.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.application.processor.CursoConcluidoProcessor;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.EmailNotificationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener para processamento de recomendações de cursos por IA.
 * 
 * Este componente consome mensagens da fila curso.concluido.ai-recommendation
 * e processa eventos de conclusão de curso para gerar recomendações personalizadas
 * usando Google Gemini AI.
 * 
 * Fluxo:
 * 1. Recebe evento de conclusão de curso (CursoConcluidoEvent)
 * 2. Delega para CursoConcluidoProcessor (gera recomendação via IA)
 * 3. Publica EmailNotificationEvent na fila de email para envio
 * 
 * Responsabilidades:
 * - Analisar histórico do aluno
 * - Gerar recomendações personalizadas de novos cursos
 * - Encaminhar para fila de email
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationListener {

    private final CursoConcluidoProcessor cursoConcluidoProcessor;
    private final RabbitTemplate rabbitTemplate;

    @Value("${webcursos.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${webcursos.rabbitmq.routing-key.email:curso.concluido.email}")
    private String emailRoutingKey;

    /**
     * Processa eventos de conclusão de curso para geração de recomendações.
     * Após gerar a recomendação, publica na fila de email para envio.
     * 
     * @param event Evento de conclusão de curso contendo dados do aluno e curso
     */
    @RabbitListener(queues = "${webcursos.rabbitmq.queue.ai-recommendation}")
    public void processAiRecommendation(CursoConcluidoEvent event) {
        log.info("=== AI Recommendation Listener ===");
        log.info("Recebido evento de conclusão de curso para processamento de IA");
        log.info("Aluno: {} (ID: {})", event.alunoNome(), event.alunoId());
        log.info("Curso: {} - {} (ID: {})", event.cursoCodigo(), event.cursoNome(), event.cursoId());
        log.info("Nota Final: {} | Aprovado: {}", event.notaFinal(), event.aprovado());
        log.info("Data Conclusão: {}", event.dataConclusao());

        try {
            // 1. Gerar recomendação via IA
            String mensagemRecomendacao = cursoConcluidoProcessor.process(event);
            log.debug("Mensagem de recomendação gerada: {}", mensagemRecomendacao);

            // 2. Criar evento de email e publicar na fila de email
            EmailNotificationEvent emailEvent = EmailNotificationEvent.forCourseCompletion(
                    event.alunoEmail(),
                    event.alunoNome(),
                    event.cursoNome(),
                    mensagemRecomendacao,
                    event.alunoId(),
                    event.cursoId()
            );

            rabbitTemplate.convertAndSend(exchangeName, emailRoutingKey, emailEvent);
            log.info("Evento de email publicado na fila de notificação");

            log.info("Processamento de IA concluído com sucesso");
        } catch (Exception e) {
            log.error("Erro ao processar recomendação de IA para aluno: {}", event.alunoNome(), e);
            throw e; // Re-throw para que a mensagem vá para DLQ se necessário
        }

        log.info("=== Fim do processamento AI Recommendation ===");
    }
}
