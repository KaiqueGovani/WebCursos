package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;
import com.morangosdoamor.WebCursos.infrastructure.messaging.publisher.CursoConcluidoEventPublisher;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private CursoConcluidoEventPublisher eventPublisher;

    @InjectMocks
    private MatriculaService matriculaService;

    private Aluno aluno;
    private Curso curso;

    @BeforeEach
    void setUp() {
        aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Ana")
            .email(new Email("ana@test.com"))
            .matricula("MAT-1")
            .build();
        curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Cursos")
            .cargaHoraria(new CargaHoraria(40))
            .build();
    }

    @Test
    void deveMatricularAlunoEmCurso() {
        when(alunoRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(cursoRepository.findByCodigo("JAVA001")).thenReturn(Optional.of(curso));
        when(matriculaRepository.existsByAlunoIdAndCursoId(aluno.getId(), curso.getId())).thenReturn(false);

        ArgumentCaptor<Matricula> matriculaCaptor = ArgumentCaptor.forClass(Matricula.class);
        when(matriculaRepository.save(matriculaCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Matricula matricula = matriculaService.matricular(aluno.getId(), "JAVA001");

        assertThat(matricula.getAluno()).isEqualTo(aluno);
        assertThat(matricula.getCurso()).isEqualTo(curso);
        assertThat(matricula.getStatus()).isEqualTo(MatriculaStatus.MATRICULADO);
        assertThat(matricula.getDataMatricula()).isNotNull();
    }

    @Test
    void deveLancarErroAoMatricularAlunoJaMatriculado() {
        when(alunoRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(cursoRepository.findByCodigo("JAVA001")).thenReturn(Optional.of(curso));
        when(matriculaRepository.existsByAlunoIdAndCursoId(aluno.getId(), curso.getId())).thenReturn(true);

        assertThatThrownBy(() -> matriculaService.matricular(aluno.getId(), "JAVA001"))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("já matriculado");
    }

    @Test
    void deveLancarErroQuandoAlunoNaoEncontradoAoMatricular() {
        when(alunoRepository.findById(aluno.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.matricular(aluno.getId(), "JAVA001"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveLancarErroQuandoCursoNaoEncontrado() {
        when(alunoRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(cursoRepository.findByCodigo("JAVA001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.matricular(aluno.getId(), "JAVA001"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveConcluirCursoComNotaValida() {
        double nota = 8.5;
        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .build();

        when(matriculaRepository.findByIdAndAlunoId(matricula.getId(), aluno.getId())).thenReturn(Optional.of(matricula));

        Matricula concluida = matriculaService.concluir(aluno.getId(), matricula.getId(), nota);

        assertThat(concluida.getStatus()).isEqualTo(MatriculaStatus.CONCLUIDO);
        assertThat(concluida.getNotaFinal()).isEqualTo(nota);
        assertThat(concluida.getDataConclusao()).isNotNull();
    }

    @Test
    void devePublicarEventoQuandoCursoForConcluido() {
        double nota = 8.5;
        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .build();

        when(matriculaRepository.findByIdAndAlunoId(matricula.getId(), aluno.getId())).thenReturn(Optional.of(matricula));

        matriculaService.concluir(aluno.getId(), matricula.getId(), nota);

        ArgumentCaptor<CursoConcluidoEvent> eventCaptor = ArgumentCaptor.forClass(CursoConcluidoEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        CursoConcluidoEvent event = eventCaptor.getValue();
        assertThat(event.alunoId()).isEqualTo(aluno.getId());
        assertThat(event.alunoNome()).isEqualTo(aluno.getNome());
        assertThat(event.cursoId()).isEqualTo(curso.getId());
        assertThat(event.cursoCodigo()).isEqualTo(curso.getCodigo());
        assertThat(event.notaFinal()).isEqualTo(nota);
        assertThat(event.aprovado()).isTrue();
    }

    @Test
    void devePublicarEventoComAprovadoFalseQuandoNotaBaixa() {
        double nota = 5.0;
        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .build();

        when(matriculaRepository.findByIdAndAlunoId(matricula.getId(), aluno.getId())).thenReturn(Optional.of(matricula));

        matriculaService.concluir(aluno.getId(), matricula.getId(), nota);

        ArgumentCaptor<CursoConcluidoEvent> eventCaptor = ArgumentCaptor.forClass(CursoConcluidoEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        CursoConcluidoEvent event = eventCaptor.getValue();
        assertThat(event.notaFinal()).isEqualTo(nota);
        assertThat(event.aprovado()).isFalse();
    }

    @Test
    void deveLancarErroAoConcluirCursoJaConcluido() {
        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.CONCLUIDO)
            .notaFinal(8.0)
            .build();

        when(matriculaRepository.findByIdAndAlunoId(matricula.getId(), aluno.getId())).thenReturn(Optional.of(matricula));

        assertThatThrownBy(() -> matriculaService.concluir(aluno.getId(), matricula.getId(), 8.0))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("já está concluído");
    }

    @Test
    void deveLancarErroQuandoMatriculaNaoEncontrada() {
        when(matriculaRepository.findByIdAndAlunoId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.concluir(aluno.getId(), UUID.randomUUID(), 8.0))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Matrícula não encontrada");
    }

    @Test
    void deveRejeitarNotaForaDoIntervalo() {
        assertThatThrownBy(() -> matriculaService.concluir(aluno.getId(), UUID.randomUUID(), 11))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Nota final deve estar entre 0 e 10");
    }

    @Test
    void deveListarMatriculasPorAluno() {
        when(alunoRepository.existsById(aluno.getId())).thenReturn(true);
        when(matriculaRepository.findAllByAlunoId(aluno.getId())).thenReturn(List.of());

        assertThat(matriculaService.listarPorAluno(aluno.getId())).isEmpty();
    }

    @Test
    void deveLancarErroQuandoAlunoNaoExisteAoListarMatriculas() {
        when(alunoRepository.existsById(aluno.getId())).thenReturn(false);

        assertThatThrownBy(() -> matriculaService.listarPorAluno(aluno.getId()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveRetornarNotaFinalQuandoEncontrada() {
        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.CONCLUIDO)
            .notaFinal(9.0)
            .build();

        when(matriculaRepository.findByIdAndAlunoId(matricula.getId(), aluno.getId())).thenReturn(Optional.of(matricula));

        assertThat(matriculaService.buscarNotaFinal(aluno.getId(), matricula.getId())).isEqualTo(9.0);
    }

    @Test
    void deveRetornarNullQuandoNotaNaoEncontrada() {
        when(matriculaRepository.findByIdAndAlunoId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        assertThat(matriculaService.buscarNotaFinal(aluno.getId(), UUID.randomUUID())).isNull();
    }
}
