package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoUpdateRequest;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
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
        Curso curso1 = Curso.builder().id(UUID.randomUUID()).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(new CargaHoraria(40)).build();
        Curso curso2 = Curso.builder().id(UUID.randomUUID()).codigo("SPRING001").nome("Spring").descricao("Desc").cargaHoraria(new CargaHoraria(60)).build();
        Curso curso3 = Curso.builder().id(UUID.randomUUID()).codigo("WEB001").nome("Web").descricao("Desc").cargaHoraria(new CargaHoraria(50)).build();

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
        Curso curso = Curso.builder().id(UUID.randomUUID()).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(new CargaHoraria(40)).build();
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

    @Test
    void deveCriarCurso() {
        CursoRequest dto = new CursoRequest("PYTHON001", "Python", "Curso de Python", 60, Set.of());
        Curso cursoSalvo = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("PYTHON001")
            .nome("Python")
            .descricao("Curso de Python")
            .cargaHoraria(new CargaHoraria(60))
            .build();

        when(cursoRepository.findByCodigo("PYTHON001")).thenReturn(Optional.empty());
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoSalvo);

        Curso criado = cursoService.criar(dto);

        assertThat(criado.getCodigo()).isEqualTo("PYTHON001");
        verify(cursoRepository).save(any(Curso.class));
    }

    @Test
    void deveLancarErroAoCriarCursoComCodigoDuplicado() {
        CursoRequest dto = new CursoRequest("JAVA001", "Java", "Desc", 40, Set.of());
        Curso existente = Curso.builder().id(UUID.randomUUID()).codigo("JAVA001").build();

        when(cursoRepository.findByCodigo("JAVA001")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> cursoService.criar(dto))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Código já cadastrado");
    }

    @Test
    void deveBuscarCursoPorId() {
        UUID cursoId = UUID.randomUUID();
        Curso curso = Curso.builder().id(cursoId).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(new CargaHoraria(40)).build();

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));

        Curso encontrado = cursoService.buscarPorId(cursoId);

        assertThat(encontrado.getId()).isEqualTo(cursoId);
    }

    @Test
    void deveLancarErroQuandoCursoNaoEncontradoPorId() {
        UUID cursoId = UUID.randomUUID();
        when(cursoRepository.findById(cursoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cursoService.buscarPorId(cursoId))
            .hasMessageContaining("Curso não encontrado");
    }

    @Test
    void deveBuscarCursosPorCargaHorariaMinima() {
        Curso curso1 = Curso.builder().id(UUID.randomUUID()).codigo("CURSO1").nome("Curso 1").descricao("Desc").cargaHoraria(new CargaHoraria(50)).build();
        Curso curso2 = Curso.builder().id(UUID.randomUUID()).codigo("CURSO2").nome("Curso 2").descricao("Desc").cargaHoraria(new CargaHoraria(60)).build();

        when(cursoRepository.findByCargaHorariaMinima(40)).thenReturn(List.of(curso1, curso2));

        List<Curso> cursos = cursoService.buscarPorCargaHorariaMinima(40);

        assertThat(cursos).hasSize(2);
    }

    @Test
    void deveBuscarCursosPorCargaHorariaMaxima() {
        Curso curso1 = Curso.builder().id(UUID.randomUUID()).codigo("CURSO1").nome("Curso 1").descricao("Desc").cargaHoraria(new CargaHoraria(30)).build();
        Curso curso2 = Curso.builder().id(UUID.randomUUID()).codigo("CURSO2").nome("Curso 2").descricao("Desc").cargaHoraria(new CargaHoraria(40)).build();

        when(cursoRepository.findByCargaHorariaMaxima(50)).thenReturn(List.of(curso1, curso2));

        List<Curso> cursos = cursoService.buscarPorCargaHorariaMaxima(50);

        assertThat(cursos).hasSize(2);
    }

    @Test
    void deveAtualizarCurso() {
        UUID cursoId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(cursoId)
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Descrição antiga")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        CursoUpdateRequest dto = new CursoUpdateRequest(null, "Java Atualizado", "Nova descrição", null, null);
        Curso atualizado = cursoService.atualizar(cursoId, dto);

        assertThat(atualizado.getNome()).isEqualTo("Java Atualizado");
        verify(cursoRepository).save(curso);
    }

    @Test
    void deveLancarErroAoAtualizarComCodigoDuplicado() {
        UUID cursoId = UUID.randomUUID();
        UUID outroId = UUID.randomUUID();
        Curso curso = Curso.builder().id(cursoId).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(new CargaHoraria(40)).build();
        Curso outro = Curso.builder().id(outroId).codigo("PYTHON001").nome("Python").descricao("Desc").cargaHoraria(new CargaHoraria(50)).build();

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));
        when(cursoRepository.findByCodigo("PYTHON001")).thenReturn(Optional.of(outro));

        CursoUpdateRequest dto = new CursoUpdateRequest("PYTHON001", null, null, null, null);

        assertThatThrownBy(() -> cursoService.atualizar(cursoId, dto))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Código já cadastrado");
    }

    @Test
    void devePermitirAtualizarComMesmoCodigo() {
        UUID cursoId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(cursoId)
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Descrição original")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));
        when(cursoRepository.findByCodigo("JAVA001")).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        CursoUpdateRequest dto = new CursoUpdateRequest("JAVA001", "Java Atualizado", null, null, null);
        Curso atualizado = cursoService.atualizar(cursoId, dto);

        assertThat(atualizado.getNome()).isEqualTo("Java Atualizado");
        verify(cursoRepository).save(curso);
    }

    @Test
    void deveExcluirCurso() {
        UUID cursoId = UUID.randomUUID();
        Curso curso = Curso.builder().id(cursoId).codigo("JAVA001").nome("Java").descricao("Desc").cargaHoraria(new CargaHoraria(40)).build();

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));

        cursoService.excluir(cursoId);

        verify(cursoRepository).delete(curso);
    }
}
