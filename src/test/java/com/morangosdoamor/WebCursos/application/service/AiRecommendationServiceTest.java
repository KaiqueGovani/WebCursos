package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.morangosdoamor.WebCursos.application.dto.CursoCompletoDTO;
import com.morangosdoamor.WebCursos.application.dto.CursoDisponivelDTO;

import dev.langchain4j.model.chat.ChatLanguageModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiRecommendationService")
class AiRecommendationServiceTest {

    @Mock
    private ChatLanguageModel mockChatModel;

    @Nested
    @DisplayName("Quando modelo de IA está disponível")
    class ComModeloIA {

        @Test
        @DisplayName("deve gerar recomendação usando IA quando modelo retorna resposta")
        void deveGerarRecomendacaoComIA() {
            // Arrange
            when(mockChatModel.generate(anyString()))
                .thenReturn("Parabéns! Recomendo o curso de Spring Boot.");

            AiRecommendationService service = new AiRecommendationService(mockChatModel);

            List<CursoCompletoDTO> ultimosCursos = List.of(
                new CursoCompletoDTO("Java", "JAVA001", 8.5),
                new CursoCompletoDTO("Python", "PYTHON001", 9.0)
            );

            List<CursoDisponivelDTO> cursosDisponiveis = List.of(
                new CursoDisponivelDTO("Spring Boot", "SPRING001", "APIs com Spring", 60),
                new CursoDisponivelDTO("Django", "DJANGO001", "Web com Python", 50)
            );

            // Act
            String resultado = service.generateRecommendation(
                "João", "React.js", 8.0, ultimosCursos, cursosDisponiveis
            );

            // Assert
            assertThat(resultado).isEqualTo("Parabéns! Recomendo o curso de Spring Boot.");
        }

        @Test
        @DisplayName("deve usar fallback quando modelo de IA lança exceção")
        void deveUsarFallbackQuandoIAFalha() {
            // Arrange
            when(mockChatModel.generate(anyString()))
                .thenThrow(new RuntimeException("API indisponível"));

            AiRecommendationService service = new AiRecommendationService(mockChatModel);

            List<CursoDisponivelDTO> cursosDisponiveis = List.of(
                new CursoDisponivelDTO("Spring Boot", "SPRING001", "APIs com Spring", 60)
            );

            // Act
            String resultado = service.generateRecommendation(
                "Maria", "Java", 9.0, Collections.emptyList(), cursosDisponiveis
            );

            // Assert
            assertThat(resultado)
                .contains("Olá, Maria!")
                .contains("Java")
                .contains("Spring Boot");
        }
    }

    @Nested
    @DisplayName("Quando modelo de IA não está disponível (null)")
    class SemModeloIA {

        private final AiRecommendationService service = new AiRecommendationService(null);

        @Test
        @DisplayName("deve gerar mensagem de fallback para aluno aprovado com nota excelente")
        void deveGerarFallbackParaNotaExcelente() {
            // Arrange
            List<CursoDisponivelDTO> cursosDisponiveis = List.of(
                new CursoDisponivelDTO("Spring Boot", "SPRING001", "APIs com Spring Boot", 60)
            );

            // Act
            String resultado = service.generateRecommendation(
                "João", "Java", 9.5, Collections.emptyList(), cursosDisponiveis
            );

            // Assert
            assertThat(resultado)
                .contains("Olá, João!")
                .contains("Java")
                .contains("excelente")
                .contains("Spring Boot");
        }

        @Test
        @DisplayName("deve gerar mensagem de fallback para aluno aprovado com nota boa")
        void deveGerarFallbackParaNotaBoa() {
            // Arrange
            List<CursoDisponivelDTO> cursosDisponiveis = List.of(
                new CursoDisponivelDTO("Python", "PYTHON001", "Fundamentos Python", 45)
            );

            // Act
            String resultado = service.generateRecommendation(
                "Maria", "Web Dev", 7.5, Collections.emptyList(), cursosDisponiveis
            );

            // Assert
            assertThat(resultado)
                .contains("Olá, Maria!")
                .contains("Web Dev")
                .contains("Ótimo trabalho")
                .contains("Python");
        }

        @Test
        @DisplayName("deve gerar mensagem de fallback para aluno reprovado")
        void deveGerarFallbackParaReprovado() {
            // Arrange
            List<CursoDisponivelDTO> cursosDisponiveis = List.of(
                new CursoDisponivelDTO("React", "REACT001", "Frontend React", 45)
            );

            // Act
            String resultado = service.generateRecommendation(
                "Pedro", "Angular", 5.0, Collections.emptyList(), cursosDisponiveis
            );

            // Assert
            assertThat(resultado)
                .contains("Olá, Pedro!")
                .contains("Angular")
                .contains("Continue se dedicando");
        }

        @Test
        @DisplayName("deve gerar mensagem especial quando não há cursos disponíveis")
        void deveGerarMensagemQuandoNaoHaCursosDisponiveis() {
            // Act
            String resultado = service.generateRecommendation(
                "Ana", "Último Curso", 10.0, Collections.emptyList(), Collections.emptyList()
            );

            // Assert
            assertThat(resultado)
                .contains("Olá, Ana!")
                .contains("completou todos os cursos")
                .contains("Parabéns");
        }

        @Test
        @DisplayName("deve truncar descrição longa do curso na mensagem")
        void deveTruncarDescricaoLonga() {
            // Arrange
            String descricaoLonga = "A".repeat(200);
            List<CursoDisponivelDTO> cursosDisponiveis = List.of(
                new CursoDisponivelDTO("Curso", "CURSO001", descricaoLonga, 40)
            );

            // Act
            String resultado = service.generateRecommendation(
                "Test", "Test", 8.0, Collections.emptyList(), cursosDisponiveis
            );

            // Assert
            assertThat(resultado).contains("...");
        }
    }
}
