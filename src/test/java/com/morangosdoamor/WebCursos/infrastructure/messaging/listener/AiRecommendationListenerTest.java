package com.morangosdoamor.WebCursos.infrastructure.messaging.listener;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.morangosdoamor.WebCursos.application.processor.CursoConcluidoProcessor;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.EmailNotificationEvent;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiRecommendationListener")
class AiRecommendationListenerTest {

    @Mock
    private CursoConcluidoProcessor cursoConcluidoProcessor;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Captor
    private ArgumentCaptor<EmailNotificationEvent> emailEventCaptor;

    private AiRecommendationListener listener;

    private static final String EXCHANGE_NAME = "webcursos.exchange";
    private static final String EMAIL_ROUTING_KEY = "curso.concluido.email";

    @BeforeEach
    void setUp() {
        listener = new AiRecommendationListener(cursoConcluidoProcessor, rabbitTemplate);
        ReflectionTestUtils.setField(listener, "exchangeName", EXCHANGE_NAME);
        ReflectionTestUtils.setField(listener, "emailRoutingKey", EMAIL_ROUTING_KEY);
    }

    private CursoConcluidoEvent criarEvento() {
        return new CursoConcluidoEvent(
            UUID.randomUUID(),
            "João Silva",
            "joao@email.com",
            UUID.randomUUID(),
            "Java Básico",
            "JAVA001",
            8.5,
            true,
            LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("processAiRecommendation")
    class ProcessAiRecommendation {

        @Test
        @DisplayName("deve processar evento e publicar EmailNotificationEvent na fila de email")
        void deveProcessarEventoEPublicarEmailEvent() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();
            String mensagemGerada = "Parabéns! Recomendo o curso Spring Boot.";

            when(cursoConcluidoProcessor.process(evento)).thenReturn(mensagemGerada);

            // Act
            listener.processAiRecommendation(evento);

            // Assert
            verify(cursoConcluidoProcessor).process(evento);
            verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq(EMAIL_ROUTING_KEY),
                emailEventCaptor.capture()
            );

            EmailNotificationEvent emailEvent = emailEventCaptor.getValue();
            assertThat(emailEvent.destinatario()).isEqualTo("joao@email.com");
            assertThat(emailEvent.nomeDestinatario()).isEqualTo("João Silva");
            assertThat(emailEvent.assunto()).contains("Java Básico");
            assertThat(emailEvent.corpo()).isEqualTo(mensagemGerada);
        }

        @Test
        @DisplayName("deve propagar exceção quando processor falha")
        void devePropragarExcecaoQuandoProcessorFalha() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            when(cursoConcluidoProcessor.process(evento))
                .thenThrow(new RuntimeException("Erro no processamento"));

            // Act & Assert
            assertThatThrownBy(() -> listener.processAiRecommendation(evento))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro no processamento");
        }

        @Test
        @DisplayName("deve propagar exceção quando publicação na fila falha")
        void devePropragarExcecaoQuandoPublicacaoFalha() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            when(cursoConcluidoProcessor.process(evento)).thenReturn("Mensagem");
            doThrow(new RuntimeException("Erro ao publicar"))
                .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(EmailNotificationEvent.class));

            // Act & Assert
            assertThatThrownBy(() -> listener.processAiRecommendation(evento))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("deve criar EmailNotificationEvent com dados corretos do aluno e curso")
        void deveCriarEmailEventComDadosCorretos() {
            // Arrange
            UUID alunoId = UUID.randomUUID();
            UUID cursoId = UUID.randomUUID();
            CursoConcluidoEvent evento = new CursoConcluidoEvent(
                alunoId,
                "Maria Santos",
                "maria@test.com",
                cursoId,
                "Python Avançado",
                "PYTHON002",
                9.5,
                true,
                LocalDateTime.now()
            );

            when(cursoConcluidoProcessor.process(evento)).thenReturn("Recomendação personalizada");

            // Act
            listener.processAiRecommendation(evento);

            // Assert
            verify(rabbitTemplate).convertAndSend(
                eq(EXCHANGE_NAME),
                eq(EMAIL_ROUTING_KEY),
                emailEventCaptor.capture()
            );

            EmailNotificationEvent emailEvent = emailEventCaptor.getValue();
            assertThat(emailEvent.alunoId()).isEqualTo(alunoId);
            assertThat(emailEvent.cursoId()).isEqualTo(cursoId);
            assertThat(emailEvent.destinatario()).isEqualTo("maria@test.com");
            assertThat(emailEvent.nomeDestinatario()).isEqualTo("Maria Santos");
            assertThat(emailEvent.assunto()).isEqualTo("Parabéns pela conclusão do curso Python Avançado!");
        }
    }
}

