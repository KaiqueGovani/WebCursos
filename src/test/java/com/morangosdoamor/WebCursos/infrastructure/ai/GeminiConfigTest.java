package com.morangosdoamor.WebCursos.infrastructure.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import dev.langchain4j.model.chat.ChatLanguageModel;

@DisplayName("GeminiConfig")
class GeminiConfigTest {

    @Nested
    @DisplayName("geminiChatModel")
    class GeminiChatModel {

        @Test
        @DisplayName("deve retornar null quando API key está vazia")
        void deveRetornarNullQuandoApiKeyVazia() {
            // Arrange
            GeminiConfig config = new GeminiConfig();
            ReflectionTestUtils.setField(config, "apiKey", "");
            ReflectionTestUtils.setField(config, "modelName", "gemini-1.5-flash");
            ReflectionTestUtils.setField(config, "temperature", 0.7);

            // Act
            ChatLanguageModel model = config.geminiChatModel();

            // Assert
            assertThat(model).isNull();
        }

        @Test
        @DisplayName("deve retornar null quando API key é null")
        void deveRetornarNullQuandoApiKeyNull() {
            // Arrange
            GeminiConfig config = new GeminiConfig();
            ReflectionTestUtils.setField(config, "apiKey", null);
            ReflectionTestUtils.setField(config, "modelName", "gemini-1.5-flash");
            ReflectionTestUtils.setField(config, "temperature", 0.7);

            // Act
            ChatLanguageModel model = config.geminiChatModel();

            // Assert
            assertThat(model).isNull();
        }

        @Test
        @DisplayName("deve retornar null quando API key contém apenas espaços")
        void deveRetornarNullQuandoApiKeyApenasEspacos() {
            // Arrange
            GeminiConfig config = new GeminiConfig();
            ReflectionTestUtils.setField(config, "apiKey", "   ");
            ReflectionTestUtils.setField(config, "modelName", "gemini-1.5-flash");
            ReflectionTestUtils.setField(config, "temperature", 0.7);

            // Act
            ChatLanguageModel model = config.geminiChatModel();

            // Assert
            assertThat(model).isNull();
        }

        // Nota: Não testamos o caso de API key válida pois isso requer uma chave real
        // e faria chamadas externas. Em um ambiente de CI/CD, isso seria um teste de integração.
    }
}

