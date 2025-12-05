package com.morangosdoamor.WebCursos.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

import com.morangosdoamor.WebCursos.application.dto.CursoCompletoDTO;
import com.morangosdoamor.WebCursos.application.dto.CursoDisponivelDTO;
import com.morangosdoamor.WebCursos.application.service.AiRecommendationService;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CursoConcluidoProcessor")
class CursoConcluidoProcessorTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private AiRecommendationService aiRecommendationService;

    @Captor
    private ArgumentCaptor<List<CursoCompletoDTO>> cursosCompletosCaptor;

    @Captor
    private ArgumentCaptor<List<CursoDisponivelDTO>> cursosDisponiveisCaptor;

    private CursoConcluidoProcessor processor;

    private static final UUID ALUNO_ID = UUID.randomUUID();
    private static final UUID CURSO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        processor = new CursoConcluidoProcessor(
            matriculaRepository, cursoRepository, aiRecommendationService
        );
    }

    private CursoConcluidoEvent criarEvento() {
        return new CursoConcluidoEvent(
            ALUNO_ID,
            "João Silva",
            "joao@email.com",
            CURSO_ID,
            "Java Básico",
            "JAVA001",
            8.5,
            true,
            LocalDateTime.now()
        );
    }

    private Curso criarCurso(String codigo, String nome, int cargaHoraria) {
        return Curso.builder()
            .id(UUID.randomUUID())
            .codigo(codigo)
            .nome(nome)
            .descricao("Descrição do curso " + nome)
            .cargaHoraria(new CargaHoraria(cargaHoraria))
            .build();
    }

    private Matricula criarMatricula(Curso curso, Double nota) {
        return Matricula.builder()
            .id(UUID.randomUUID())
            .curso(curso)
            .status(MatriculaStatus.CONCLUIDO)
            .notaFinal(nota)
            .dataConclusao(LocalDateTime.now())
            .build();
    }

    @Nested
    @DisplayName("process")
    class Process {

        @Test
        @DisplayName("deve buscar últimos cursos concluídos e cursos disponíveis")
        void deveBuscarCursosEGerarRecomendacao() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            Curso cursoJava = criarCurso("JAVA001", "Java Básico", 40);
            Curso cursoPython = criarCurso("PYTHON001", "Python", 45);
            Matricula matriculaJava = criarMatricula(cursoJava, 9.0);
            Matricula matriculaPython = criarMatricula(cursoPython, 8.0);

            Curso cursoSpring = criarCurso("SPRING001", "Spring Boot", 60);
            Curso cursoDjango = criarCurso("DJANGO001", "Django", 50);

            when(matriculaRepository.findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                eq(ALUNO_ID), eq(MatriculaStatus.CONCLUIDO)
            )).thenReturn(List.of(matriculaJava, matriculaPython));

            when(cursoRepository.findCursosNotEnrolledByAluno(ALUNO_ID))
                .thenReturn(List.of(cursoSpring, cursoDjango));

            when(aiRecommendationService.generateRecommendation(
                anyString(), anyString(), any(), anyList(), anyList()
            )).thenReturn("Recomendação gerada pela IA");

            // Act
            String resultado = processor.process(evento);

            // Assert
            assertThat(resultado).isEqualTo("Recomendação gerada pela IA");

            // Verify repository calls
            verify(matriculaRepository).findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                ALUNO_ID, MatriculaStatus.CONCLUIDO
            );
            verify(cursoRepository).findCursosNotEnrolledByAluno(ALUNO_ID);
        }

        @Test
        @DisplayName("deve converter matrículas para CursoCompletoDTO corretamente")
        void deveConverterMatriculasParaDTO() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            Curso cursoJava = criarCurso("JAVA001", "Java Básico", 40);
            Matricula matricula = criarMatricula(cursoJava, 9.5);

            when(matriculaRepository.findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                any(), any()
            )).thenReturn(List.of(matricula));

            when(cursoRepository.findCursosNotEnrolledByAluno(any()))
                .thenReturn(Collections.emptyList());

            when(aiRecommendationService.generateRecommendation(
                anyString(), anyString(), any(), cursosCompletosCaptor.capture(), anyList()
            )).thenReturn("Resultado");

            // Act
            processor.process(evento);

            // Assert
            List<CursoCompletoDTO> cursosConcluidos = cursosCompletosCaptor.getValue();
            assertThat(cursosConcluidos).hasSize(1);
            assertThat(cursosConcluidos.get(0).nome()).isEqualTo("Java Básico");
            assertThat(cursosConcluidos.get(0).codigo()).isEqualTo("JAVA001");
            assertThat(cursosConcluidos.get(0).nota()).isEqualTo(9.5);
        }

        @Test
        @DisplayName("deve converter cursos disponíveis para CursoDisponivelDTO corretamente")
        void deveConverterCursosDisponiveisParaDTO() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            Curso cursoSpring = criarCurso("SPRING001", "Spring Boot", 60);

            when(matriculaRepository.findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                any(), any()
            )).thenReturn(Collections.emptyList());

            when(cursoRepository.findCursosNotEnrolledByAluno(any()))
                .thenReturn(List.of(cursoSpring));

            when(aiRecommendationService.generateRecommendation(
                anyString(), anyString(), any(), anyList(), cursosDisponiveisCaptor.capture()
            )).thenReturn("Resultado");

            // Act
            processor.process(evento);

            // Assert
            List<CursoDisponivelDTO> cursosDisponiveis = cursosDisponiveisCaptor.getValue();
            assertThat(cursosDisponiveis).hasSize(1);
            assertThat(cursosDisponiveis.get(0).nome()).isEqualTo("Spring Boot");
            assertThat(cursosDisponiveis.get(0).codigo()).isEqualTo("SPRING001");
            assertThat(cursosDisponiveis.get(0).cargaHoraria()).isEqualTo(60);
        }

        @Test
        @DisplayName("deve passar dados corretos do evento para o serviço de IA")
        void devePassarDadosCorretosParaIA() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            when(matriculaRepository.findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                any(), any()
            )).thenReturn(Collections.emptyList());

            when(cursoRepository.findCursosNotEnrolledByAluno(any()))
                .thenReturn(Collections.emptyList());

            when(aiRecommendationService.generateRecommendation(
                anyString(), anyString(), any(), anyList(), anyList()
            )).thenReturn("Resultado");

            // Act
            processor.process(evento);

            // Assert
            verify(aiRecommendationService).generateRecommendation(
                eq("João Silva"),
                eq("Java Básico"),
                eq(8.5),
                anyList(),
                anyList()
            );
        }

        @Test
        @DisplayName("deve funcionar quando não há cursos concluídos nem disponíveis")
        void deveFuncionarSemCursos() {
            // Arrange
            CursoConcluidoEvent evento = criarEvento();

            when(matriculaRepository.findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(
                any(), any()
            )).thenReturn(Collections.emptyList());

            when(cursoRepository.findCursosNotEnrolledByAluno(any()))
                .thenReturn(Collections.emptyList());

            when(aiRecommendationService.generateRecommendation(
                anyString(), anyString(), any(), anyList(), anyList()
            )).thenReturn("Parabéns por completar todos os cursos!");

            // Act
            String resultado = processor.process(evento);

            // Assert
            assertThat(resultado).isEqualTo("Parabéns por completar todos os cursos!");
        }
    }
}

