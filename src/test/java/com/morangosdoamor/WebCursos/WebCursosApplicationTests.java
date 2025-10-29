package com.morangosdoamor.WebCursos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
import com.morangosdoamor.WebCursos.repository.MatriculaRepository;
import com.morangosdoamor.WebCursos.service.CursoService;

@SpringBootTest
@Transactional
class WebCursosApplicationTests {
	
	@Autowired
	private CursoService cursoService;

	@Autowired
	private AlunoRepository alunoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private MatriculaRepository matriculaRepository;

	private Aluno aluno;

	@BeforeEach
	void setUp() {
		// Limpa os dados antes de cada teste
		matriculaRepository.deleteAll();
		alunoRepository.deleteAll();
		cursoRepository.deleteAll();

		// Cria um aluno e cursos de teste
		aluno = new Aluno("Aluno Teste", "teste@email.com", "123");
		alunoRepository.save(aluno);

		cursoRepository.save(new Curso("JAVA001", "Programação Java", "Curso básico de Java", 40, new String[]{}));
		cursoRepository.save(new Curso("WEB001", "Desenvolvimento Web", "HTML, CSS, JavaScript", 50, new String[]{}));
		cursoRepository.save(new Curso("SPRING001", "Spring Framework", "Curso de Spring Boot", 60, new String[]{}));
        cursoRepository.save(new Curso("REACT001", "React.js", "Desenvolvimento com React", 45, new String[]{}));
        cursoRepository.save(new Curso("PYTHON001", "Programação Python", "Curso básico de Python", 45, new String[]{}));
        cursoRepository.save(new Curso("DJANGO001", "Django Framework", "Desenvolvimento web com Django", 55, new String[]{}));
		cursoRepository.save(new Curso("NODE001", "Node.js", "Desenvolvimento backend com Node.js", 50, new String[]{}));
        cursoRepository.save(new Curso("ANGULAR001", "Angular", "Framework Angular para frontend", 60, new String[]{}));
	}

	/**
	 * Teste para verificar se o aluno com média 7 ou mais libera 3 cursos
	 */
	@Test
	void testeMedia7ouMaisAlunoLiberaMaisCursos() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");
		Curso curso = cursoRepository.findById("JAVA001").get();
		float nota = 10;

		// Act
		cursoService.finalizarCurso(aluno, curso, nota);
		List<Curso> cursosLiberados = cursoService.findLiberadosByAluno(aluno);

		// Assert
		assertEquals(3, cursosLiberados.size());
	}

	@Test
	void testeMedia7ouMaisAlunoLiberaMaisCursosAcumulados() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");
		cursoService.adicionarCurso(aluno, "WEB001");
		Curso curso1 = cursoRepository.findById("JAVA001").get();
		Curso curso2 = cursoRepository.findById("WEB001").get();
		float nota1 = 10;
		float nota2 = 7;

		// Act
		cursoService.finalizarCurso(aluno, curso1, nota1);
		cursoService.finalizarCurso(aluno, curso2, nota2);
		List<Curso> cursosLiberados = cursoService.findLiberadosByAluno(aluno);

		// Assert
		assertEquals(6, cursosLiberados.size());
	}

	@Test
	void testeMediaMenor7AlunoNaoLiberaMaisCursos() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");
		Curso curso = cursoRepository.findById("JAVA001").get();
		float nota = 6.9f;

		// Act
		cursoService.finalizarCurso(aluno, curso, nota);
		List<Curso> cursosLiberados = cursoService.findLiberadosByAluno(aluno);

		// Assert
		assertEquals(0, cursosLiberados.size());
	}

	// Novos testes adaptados para a camada de persistência

	@Test
	void testeGetNotaComAlunoECursoValidos() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");
		Curso curso = cursoRepository.findById("JAVA001").get();
		float nota = 8.5f;

		// Act
		cursoService.finalizarCurso(aluno, curso, nota);
		var notaObtida = cursoService.getNota(aluno, curso);

		// Assert
		assertEquals(8.5f, notaObtida.getValue());
	}

	@Test
	void testeIsCursoFinalizadoComCursoFinalizado() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");
		Curso curso = cursoRepository.findById("JAVA001").get();

		// Act
		cursoService.finalizarCurso(aluno, curso, 8.0f);
		boolean finalizado = cursoService.isCursoFinalizado(aluno, curso);

		// Assert
		assertEquals(true, finalizado);
	}

	@Test
	void testeGetAllCursos() {
		// Act
		var todosCursos = cursoService.getAllCursos();

		// Assert
		assertEquals(8, todosCursos.size()); // 8 cursos inicializados no setUp
	}

	@Test
	void testeAdicionarCursoInexistente() {
		// Act & Assert
		try {
			cursoService.adicionarCurso(aluno, "CURSO_INEXISTENTE");
			assertEquals(true, false, "Deveria ter lançado exceção");
		} catch (IllegalArgumentException e) {
			assertEquals("Curso não encontrado: CURSO_INEXISTENTE", e.getMessage());
		}
	}

	@Test
	void testeAdicionarCursoJaMatriculado() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");

		// Act & Assert
		try {
			cursoService.adicionarCurso(aluno, "JAVA001");
			assertEquals(true, false, "Deveria ter lançado exceção");
		} catch (IllegalStateException e) {
			assertEquals("Aluno já está matriculado neste curso", e.getMessage());
		}
	}

	@Test
	void testeFinalizarCursoNaoMatriculado() {
		// Arrange
		Curso curso = cursoRepository.findById("JAVA001").get();

		// Act & Assert
		try {
			cursoService.finalizarCurso(aluno, curso, 8.0f);
			assertEquals(true, false, "Deveria ter lançado exceção");
		} catch (IllegalStateException e) {
			assertEquals("Aluno não está matriculado neste curso", e.getMessage());
		}
	}

	@Test
	void testeFinalizarCursoJaFinalizado() {
		// Arrange
		cursoService.adicionarCurso(aluno, "JAVA001");
		Curso curso = cursoRepository.findById("JAVA001").get();
		cursoService.finalizarCurso(aluno, curso, 8.0f);

		// Act & Assert
		try {
			cursoService.finalizarCurso(aluno, curso, 9.0f);
			assertEquals(true, false, "Deveria ter lançado exceção");
		} catch (IllegalStateException e) {
			assertEquals("Curso não está em andamento", e.getMessage());
		}
	}
}