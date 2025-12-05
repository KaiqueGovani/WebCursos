package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@webcursos.com");
    }

    @Nested
    @DisplayName("sendEmail")
    class SendEmail {

        @Test
        @DisplayName("deve enviar email com sucesso")
        void deveEnviarEmailComSucesso() {
            // Act
            emailService.sendEmail("aluno@email.com", "Assunto", "Corpo do email");

            // Assert
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage message = messageCaptor.getValue();
            assertThat(message.getFrom()).isEqualTo("noreply@webcursos.com");
            assertThat(message.getTo()).containsExactly("aluno@email.com");
            assertThat(message.getSubject()).isEqualTo("Assunto");
            assertThat(message.getText()).isEqualTo("Corpo do email");
        }

        @Test
        @DisplayName("deve propagar exceção quando envio falha")
        void devePropragarExcecaoQuandoEnvioFalha() {
            // Arrange
            doThrow(new MailSendException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

            // Act & Assert
            assertThatThrownBy(() -> 
                emailService.sendEmail("aluno@email.com", "Assunto", "Corpo")
            ).isInstanceOf(MailSendException.class);
        }

        @Test
        @DisplayName("deve enviar email com caracteres especiais")
        void deveEnviarEmailComCaracteresEspeciais() {
            // Act
            emailService.sendEmail(
                "user+tag@sub.domain.com",
                "Assunto com ação & símbolos",
                "Corpo com émojis 🎉"
            );

            // Assert
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage message = messageCaptor.getValue();
            assertThat(message.getTo()).containsExactly("user+tag@sub.domain.com");
            assertThat(message.getSubject()).isEqualTo("Assunto com ação & símbolos");
            assertThat(message.getText()).isEqualTo("Corpo com émojis 🎉");
        }

        @Test
        @DisplayName("deve enviar email com corpo longo")
        void deveEnviarEmailComCorpoLongo() {
            // Arrange
            String corpoLongo = "A".repeat(5000);

            // Act
            emailService.sendEmail("test@email.com", "Assunto", corpoLongo);

            // Assert
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage message = messageCaptor.getValue();
            assertThat(message.getText()).isEqualTo(corpoLongo);
        }
    }
}

