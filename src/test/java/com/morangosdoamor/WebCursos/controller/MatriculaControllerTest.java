package com.morangosdoamor.WebCursos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
import com.morangosdoamor.WebCursos.service.CursoService;

@WebMvcTest(MatriculaController.class)
class MatriculaControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private CursoService cursoService;
	@MockBean
	private AlunoRepository alunoRepository;
	@MockBean
	private CursoRepository cursoRepository;
	
	@Test
	void shouldMatricularAluno() throws Exception {
		Aluno aluno = new Aluno("User", "user@example.com", "123");
		Curso curso = new Curso("JAVA001", "Java", "", 40, new String[]{});
		doReturn(Optional.of(aluno)).when(alunoRepository).findById(aluno.getId());
		doNothing().when(cursoService).adicionarCurso(any(Aluno.class), any(String.class));
		
		var payload = objectMapper.writeValueAsString(new Payload(aluno.getId(), "JAVA001"));
		mockMvc.perform(post("/matriculas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isOk());
	}
	
	static class Payload {
		public String alunoId;
		public String cursoId;
		Payload(String a, String c) { this.alunoId = a; this.cursoId = c; }
	}
}
