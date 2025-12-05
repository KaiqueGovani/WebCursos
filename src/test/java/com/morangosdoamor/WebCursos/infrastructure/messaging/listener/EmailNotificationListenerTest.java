package com.morangosdoamor.WebCursos.infrastructure.messaging.listener;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import com.morangosdoamor.WebCursos.application.service.EmailService;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.EmailNotificationEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailNotificationListener")
class EmailNotificationListenerTest {

    @Mock
    private EmailService emailService;

    private EmailNotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new EmailNotificationListener(emailService);
    }

    private EmailNotificationEvent criarEvento() {
        return new EmailNotificationEvent(
            "joao@email.com",
            "João Silva",
            "Parabéns pela conclusão do curso Java!",
            "Corpo do email com recomendação",
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }

    @Nested
    @DisplayName("processEmailNotification")
    class ProcessEmailNotification {

        @Test
        @DisplayName("deve enviar email com dados do evento")
        void deveEnviarEmailComDadosDoEvento() {
            // Arrange
            EmailNotificationEvent evento = criarEvento();

            // Act
            listener.processEmailNotification(evento);

            // Assert
            verify(emailService).sendEmail(
                eq("joao@email.com"),
                eq("Parabéns pela conclusão do curso Java!"),
                eq("Corpo do email com recomendação")
            );
        }

        @Test
        @DisplayName("deve propagar exceção quando envio de email falha")
        void devePropragarExcecaoQuandoEnvioFalha() {
            // Arrange
            EmailNotificationEvent evento = criarEvento();

            doThrow(new MailSendException("SMTP error"))
                .when(emailService).sendEmail(
                    eq("joao@email.com"),
                    eq("Parabéns pela conclusão do curso Java!"),
                    eq("Corpo do email com recomendação")
                );

            // Act & Assert
            assertThatThrownBy(() -> listener.processEmailNotification(evento))
                .isInstanceOf(MailSendException.class);
        }

        @Test
        @DisplayName("deve processar evento com mensagem longa")
        void deveProcessarEventoComMensagemLonga() {
            // Arrange
            String mensagemLonga = "A".repeat(5000);
            EmailNotificationEvent evento = new EmailNotificationEvent(
                "test@email.com",
                "Test User",
                "Assunto",
                mensagemLonga,
                UUID.randomUUID(),
                UUID.randomUUID()
            );

            // Act
            listener.processEmailNotification(evento);

            // Assert
            verify(emailService).sendEmail(
                eq("test@email.com"),
                eq("Assunto"),
                eq(mensagemLonga)
            );
        }

        @Test
        @DisplayName("deve processar evento com caracteres especiais no email")
        void deveProcessarEventoComCaracteresEspeciais() {
            // Arrange
            EmailNotificationEvent evento = new EmailNotificationEvent(
                "user+tag@sub.domain.com",
                "Usuário Ação",
                "Assunto com ação & símbolos",
                "Corpo com émojis 🎉",
                UUID.randomUUID(),
                UUID.randomUUID()
            );

            // Act
            listener.processEmailNotification(evento);

            // Assert
            verify(emailService).sendEmail(
                eq("user+tag@sub.domain.com"),
                eq("Assunto com ação & símbolos"),
                eq("Corpo com émojis 🎉")
            );
        }
    }
}

