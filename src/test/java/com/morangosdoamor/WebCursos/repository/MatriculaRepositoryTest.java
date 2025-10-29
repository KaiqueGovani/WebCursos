package com.morangosdoamor.WebCursos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.model.entity.Matricula;
import com.morangosdoamor.WebCursos.model.entity.Matricula.StatusMatricula;

@DataJpaTest
class MatriculaRepositoryTest {
	
	@Autowired
	private AlunoRepository alunoRepository;
	@Autowired
	private CursoRepository cursoRepository;
	@Autowired
	private MatriculaRepository matriculaRepository;
	
	@Test
	void shouldHandleCustomQueries() {
		Aluno aluno = new Aluno("Repo User", "repo@example.com", "123");
		alunoRepository.save(aluno);
		Curso curso = new Curso("TEST1", "T", "D", 1, new String[]{});
		cursoRepository.save(curso);
		Matricula m = new Matricula(aluno, curso);
		matriculaRepository.save(m);
		
		assertTrue(matriculaRepository.existsByAlunoAndCurso(aluno, curso));
		assertEquals(0, matriculaRepository.countByAlunoAndStatus(aluno, StatusMatricula.CONCLUIDO));
		
		m.setStatus(StatusMatricula.CONCLUIDO);
		matriculaRepository.save(m);
		assertEquals(1, matriculaRepository.countByAlunoAndStatus(aluno, StatusMatricula.CONCLUIDO));
	}
}
