package com.morangosdoamor.WebCursos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.model.entity.Matricula;
import com.morangosdoamor.WebCursos.model.entity.Matricula.StatusMatricula;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
import com.morangosdoamor.WebCursos.repository.MatriculaRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {
	
	@Mock
	private CursoRepository cursoRepository;
	@Mock
	private MatriculaRepository matriculaRepository;
	
	@InjectMocks
	private CursoService service;
	
	private Aluno aluno;
	private Curso curso;
	
	@BeforeEach
	void init() {
		aluno = new Aluno("Mock User", "mock@example.com", "123");
		curso = new Curso("JAVA001", "Java", "Desc", 40, new String[]{});
	}
	
	@Test
	void adicionarCurso_deveMatricular() {
		doReturn(Optional.of(curso)).when(cursoRepository).findById("JAVA001");
		doReturn(false).when(matriculaRepository).existsByAlunoAndCurso(aluno, curso);
		service.adicionarCurso(aluno, "JAVA001");
		verify(matriculaRepository, times(1)).save(any(Matricula.class));
	}
	
	@Test
	void adicionarCurso_deveFalharSeCursoNaoExiste() {
		doReturn(Optional.empty()).when(cursoRepository).findById("X");
		assertThrows(IllegalArgumentException.class, () -> service.adicionarCurso(aluno, "X"));
	}
	
	@Test
	void finalizarCurso_deveAprovarOuReprovar() {
		Matricula m = new Matricula(aluno, curso);
		doReturn(Optional.of(m)).when(matriculaRepository).findByAlunoAndCurso(aluno, curso);
		service.finalizarCurso(aluno, curso, 8f);
		assertEquals(StatusMatricula.CONCLUIDO, m.getStatus());
	}
	
	@Test
	void findLiberadosByAluno_deveRetornarPorMultiploDeTres() {
		doReturn(2L).when(matriculaRepository).countByAlunoAndStatus(aluno, StatusMatricula.CONCLUIDO);
		// Ocupados pelo aluno
		doReturn(List.of()).when(matriculaRepository).findByAluno(aluno);
		// Disponíveis
		Curso c1 = new Curso("C1", "C1", "", 1, new String[]{});
		Curso c2 = new Curso("C2", "C2", "", 1, new String[]{});
		Curso c3 = new Curso("C3", "C3", "", 1, new String[]{});
		Curso c4 = new Curso("C4", "C4", "", 1, new String[]{});
		Curso c5 = new Curso("C5", "C5", "", 1, new String[]{});
		Curso c6 = new Curso("C6", "C6", "", 1, new String[]{});
		doReturn(List.of(c1, c2, c3, c4, c5, c6)).when(cursoRepository).findAll();
		var liberados = service.findLiberadosByAluno(aluno);
		assertEquals(6, liberados.size());
	}
}
