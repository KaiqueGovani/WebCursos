package com.morangosdoamor.WebCursos.infrastructure.messaging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;

import lombok.RequiredArgsConstructor;

/**
 * Publisher responsável por publicar eventos de conclusão de curso no RabbitMQ.
 * 
 * Publica mensagens no exchange webcursos.exchange com routing key curso.concluido,
 * permitindo que múltiplos consumidores (AI Recommendation, Email Notification)
 * processem o evento de forma assíncrona.
 * 
 * Tratamento de erros:
 * - Falhas na publicação são logadas mas não afetam a transação de conclusão
 * - Isso garante que a operação principal (conclusão do curso) não falhe por problemas de messaging
 */
@Component
@RequiredArgsConstructor
public class CursoConcluidoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CursoConcluidoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${webcursos.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${webcursos.rabbitmq.routing-key}")
    private String routingKey;

    /**
     * Publica um evento de conclusão de curso no RabbitMQ.
     * 
     * A mensagem é serializada para JSON e enviada ao exchange configurado.
     * Em caso de falha, o erro é logado mas não propaga exceção para não
     * afetar a transação de conclusão do curso.
     * 
     * @param event Evento de conclusão de curso a ser publicado
     */
    public void publish(CursoConcluidoEvent event) {
        try {
            log.info("Publicando evento de conclusão de curso: alunoId={}, cursoId={}, aprovado={}",
                    event.alunoId(), event.cursoId(), event.aprovado());

            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);

            log.debug("Evento publicado com sucesso no exchange '{}' com routing key '{}'",
                    exchangeName, routingKey);

        } catch (AmqpException e) {
            log.error("Falha ao publicar evento de conclusão de curso: alunoId={}, cursoId={}, erro={}",
                    event.alunoId(), event.cursoId(), e.getMessage(), e);
            // Não propaga a exceção para não afetar a transação de conclusão do curso
        }
    }
}
