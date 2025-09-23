package com.morangosdoamor.WebCursos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.service.CursoService;

@SpringBootTest
class WebCursosApplicationTests {
	
	/**
	 * Teste para verificar se o aluno com média 7 ou mais libera 3 cursos
	 * TODO: adicionar verificação das notificações com assert se a função de envio foi chamada
	 */
	@Test
	void testeMedia7ouMaisAlunoLiberaMaisCursos() {
		// Arrange
		Aluno aluno = new Aluno();
		CursoService cursoService = new CursoService();
		cursoService.adicionarCurso(aluno, "Curso 1");
		ArrayList<Curso> cursos = cursoService.getCursos(aluno);
		float nota = 10;

		// Act
		cursoService.finalizarCurso(aluno, cursos.get(0), nota);
		ArrayList<Curso> cursosLiberados = cursoService.findLiberadosByAluno(aluno);

		// Assert
		assertEquals(3, cursosLiberados.size());
	}

	@Test
	void testeMedia7ouMaisAlunoLiberaMaisCursosAcumulados() {
		// Arrange
		Aluno aluno = new Aluno();
		CursoService cursoService = new CursoService();
		cursoService.adicionarCurso(aluno, "Curso 1");
		cursoService.adicionarCurso(aluno, "Curso 2");
		ArrayList<Curso> cursos = cursoService.getCursos(aluno);
		float nota1 = 10;
		float nota2 = 7;

		// Act
		cursoService.finalizarCurso(aluno, cursos.get(0), nota1);
		cursoService.finalizarCurso(aluno, cursos.get(1), nota2);
		ArrayList<Curso> cursosLiberados = cursoService.findLiberadosByAluno(aluno);

		// Assert
		assertEquals(6, cursosLiberados.size());
	}

	@Test
	void testeMediaMenor7AlunoNaoLiberaMaisCursos() {
		// Arrange
		Aluno aluno = new Aluno();
		CursoService cursoService = new CursoService();
		cursoService.adicionarCurso(aluno, "Curso 1");
		ArrayList<Curso> cursos = cursoService.getCursos(aluno);
		float nota = 6.9f;

		// Act
		cursoService.finalizarCurso(aluno, cursos.get(0), nota);
		ArrayList<Curso> cursosLiberados = cursoService.findLiberadosByAluno(aluno);

		// Assert
		assertEquals(0, cursosLiberados.size());
	}
}