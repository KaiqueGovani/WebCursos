package com.morangosdoamor.WebCursos.infrastructure.messaging.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EmailNotificationEvent")
class EmailNotificationEventTest {

    @Nested
    @DisplayName("Construtor principal")
    class ConstrutorPrincipal {

        @Test
        @DisplayName("deve criar evento com todos os campos")
        void deveCriarEventoComTodosOsCampos() {
            // Arrange
            UUID alunoId = UUID.randomUUID();
            UUID cursoId = UUID.randomUUID();

            // Act
            EmailNotificationEvent event = new EmailNotificationEvent(
                "joao@email.com",
                "João Silva",
                "Parabéns!",
                "Corpo do email",
                alunoId,
                cursoId
            );

            // Assert
            assertThat(event.destinatario()).isEqualTo("joao@email.com");
            assertThat(event.nomeDestinatario()).isEqualTo("João Silva");
            assertThat(event.assunto()).isEqualTo("Parabéns!");
            assertThat(event.corpo()).isEqualTo("Corpo do email");
            assertThat(event.alunoId()).isEqualTo(alunoId);
            assertThat(event.cursoId()).isEqualTo(cursoId);
        }
    }

    @Nested
    @DisplayName("Factory method forCourseCompletion")
    class ForCourseCompletion {

        @Test
        @DisplayName("deve criar evento com assunto formatado para conclusão de curso")
        void deveCriarEventoComAssuntoFormatado() {
            // Arrange
            UUID alunoId = UUID.randomUUID();
            UUID cursoId = UUID.randomUUID();

            // Act
            EmailNotificationEvent event = EmailNotificationEvent.forCourseCompletion(
                "maria@email.com",
                "Maria Santos",
                "Java Básico",
                "Mensagem de recomendação",
                alunoId,
                cursoId
            );

            // Assert
            assertThat(event.destinatario()).isEqualTo("maria@email.com");
            assertThat(event.nomeDestinatario()).isEqualTo("Maria Santos");
            assertThat(event.assunto()).isEqualTo("Parabéns pela conclusão do curso Java Básico!");
            assertThat(event.corpo()).isEqualTo("Mensagem de recomendação");
            assertThat(event.alunoId()).isEqualTo(alunoId);
            assertThat(event.cursoId()).isEqualTo(cursoId);
        }

        @Test
        @DisplayName("deve formatar assunto com curso que contém caracteres especiais")
        void deveFormatarAssuntoComCaracteresEspeciais() {
            // Act
            EmailNotificationEvent event = EmailNotificationEvent.forCourseCompletion(
                "test@email.com",
                "Test User",
                "C++ & Algorithms",
                "Corpo",
                UUID.randomUUID(),
                UUID.randomUUID()
            );

            // Assert
            assertThat(event.assunto()).isEqualTo("Parabéns pela conclusão do curso C++ & Algorithms!");
        }
    }

    @Nested
    @DisplayName("Equals e HashCode (record automático)")
    class EqualsHashCode {

        @Test
        @DisplayName("dois eventos com mesmos valores devem ser iguais")
        void eventosComMesmosValoresDevemSerIguais() {
            // Arrange
            UUID alunoId = UUID.randomUUID();
            UUID cursoId = UUID.randomUUID();

            EmailNotificationEvent event1 = new EmailNotificationEvent(
                "email@test.com", "Nome", "Assunto", "Corpo", alunoId, cursoId
            );
            EmailNotificationEvent event2 = new EmailNotificationEvent(
                "email@test.com", "Nome", "Assunto", "Corpo", alunoId, cursoId
            );

            // Assert
            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }

        @Test
        @DisplayName("dois eventos com valores diferentes não devem ser iguais")
        void eventosComValoresDiferentesNaoDevemSerIguais() {
            // Arrange
            EmailNotificationEvent event1 = new EmailNotificationEvent(
                "email1@test.com", "Nome1", "Assunto1", "Corpo1", 
                UUID.randomUUID(), UUID.randomUUID()
            );
            EmailNotificationEvent event2 = new EmailNotificationEvent(
                "email2@test.com", "Nome2", "Assunto2", "Corpo2", 
                UUID.randomUUID(), UUID.randomUUID()
            );

            // Assert
            assertThat(event1).isNotEqualTo(event2);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("deve conter todos os campos no toString")
        void deveConterTodosCamposNoToString() {
            // Arrange
            UUID alunoId = UUID.randomUUID();
            UUID cursoId = UUID.randomUUID();

            EmailNotificationEvent event = new EmailNotificationEvent(
                "test@email.com", "Test", "Subject", "Body", alunoId, cursoId
            );

            // Assert
            String str = event.toString();
            assertThat(str).contains("test@email.com");
            assertThat(str).contains("Test");
            assertThat(str).contains("Subject");
            assertThat(str).contains("Body");
        }
    }
}

