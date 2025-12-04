package com.morangosdoamor.WebCursos.infrastructure.messaging.publisher;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;

/**
 * Testes unitários para o publisher de eventos de conclusão de curso.
 * Valida publicação de mensagens e tratamento de erros.
 */
@ExtendWith(MockitoExtension.class)
class CursoConcluidoEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private CursoConcluidoEventPublisher publisher;

    private static final String EXCHANGE_NAME = "webcursos.exchange";
    private static final String ROUTING_KEY = "curso.concluido";

    @BeforeEach
    void setUp() {
        publisher = new CursoConcluidoEventPublisher(rabbitTemplate);
        ReflectionTestUtils.setField(publisher, "exchangeName", EXCHANGE_NAME);
        ReflectionTestUtils.setField(publisher, "routingKey", ROUTING_KEY);
    }

    @Test
    void devePublicarEventoComSucesso() {
        CursoConcluidoEvent event = createEvent(8.5, true);

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
            eq(EXCHANGE_NAME),
            eq(ROUTING_KEY),
            eq(event)
        );
    }

    @Test
    void devePublicarEventoDeAlunoReprovado() {
        CursoConcluidoEvent event = createEvent(5.0, false);

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
            eq(EXCHANGE_NAME),
            eq(ROUTING_KEY),
            eq(event)
        );
    }

    @Test
    void naoDevePropararExcecaoQuandoFalharPublicacao() {
        CursoConcluidoEvent event = createEvent(8.5, true);

        doThrow(new AmqpException("Connection refused"))
            .when(rabbitTemplate)
            .convertAndSend(eq(EXCHANGE_NAME), eq(ROUTING_KEY), eq(event));

        // Não deve lançar exceção
        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
            eq(EXCHANGE_NAME),
            eq(ROUTING_KEY),
            eq(event)
        );
    }

    private CursoConcluidoEvent createEvent(double nota, boolean aprovado) {
        return new CursoConcluidoEvent(
            UUID.randomUUID(),
            "João Silva",
            "joao@email.com",
            UUID.randomUUID(),
            "Programação Java",
            "JAVA001",
            nota,
            aprovado,
            LocalDateTime.now()
        );
    }
}
