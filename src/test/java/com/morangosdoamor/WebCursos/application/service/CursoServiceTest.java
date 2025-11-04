package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @InjectMocks
    private CursoService cursoService;

    @Test
    void deveRetornarCursosLiberados() {
        UUID alunoId = UUID.randomUUID();
        Curso curso1 = Curso.builder().id(UUID.randomUUID()).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(40).build();
        Curso curso2 = Curso.builder().id(UUID.randomUUID()).codigo("SPRING001").nome("Spring").descricao("Desc").cargaHoraria(60).build();
        Curso curso3 = Curso.builder().id(UUID.randomUUID()).codigo("WEB001").nome("Web").descricao("Desc").cargaHoraria(50).build();

        Matricula matricula = Matricula.builder()
            .curso(curso1)
            .status(MatriculaStatus.CONCLUIDO)
            .notaFinal(8.0)
            .build();

        when(matriculaRepository.countByAlunoIdAndStatusAndNotaFinalGreaterThanEqual(alunoId, MatriculaStatus.CONCLUIDO, 7.0))
            .thenReturn(1L);
        when(matriculaRepository.findAllByAlunoId(alunoId)).thenReturn(List.of(matricula));
        when(cursoRepository.findAll(any(Sort.class))).thenReturn(List.of(curso1, curso2, curso3));

        List<Curso> liberados = cursoService.buscarCursosLiberados(alunoId);

        assertThat(liberados).extracting(Curso::getCodigo).containsExactly("SPRING001", "WEB001");
    }

    @Test
    void deveRetornarListaVaziaSemCursosConcluidos() {
        UUID alunoId = UUID.randomUUID();
        when(matriculaRepository.countByAlunoIdAndStatusAndNotaFinalGreaterThanEqual(alunoId, MatriculaStatus.CONCLUIDO, 7.0))
            .thenReturn(0L);

        List<Curso> liberados = cursoService.buscarCursosLiberados(alunoId);

        assertThat(liberados).isEmpty();
    }

    @Test
    void deveBuscarCursoPorCodigo() {
        Curso curso = Curso.builder().id(UUID.randomUUID()).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(40).build();
        when(cursoRepository.findByCodigo("JAVA001")).thenReturn(Optional.of(curso));

        Curso encontrado = cursoService.buscarPorCodigo("JAVA001");
        assertThat(encontrado.getCodigo()).isEqualTo("JAVA001");
    }

    @Test
    void deveLancarErroQuandoCursoNaoEncontrado() {
        when(cursoRepository.findByCodigo("JAVA999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cursoService.buscarPorCodigo("JAVA999"))
            .hasMessageContaining("Curso não encontrado");
    }

    @Test
    void deveListarCursosOrdenados() {
        when(cursoRepository.findAll(any(Sort.class))).thenReturn(List.of());

        assertThat(cursoService.listarTodos()).isEmpty();
    }
}
