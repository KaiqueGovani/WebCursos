package com.morangosdoamor.WebCursos.controller;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.service.CursoService;

@WebMvcTest(CursoController.class)
class CursoControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CursoService cursoService;
	
	@Test
	void shouldListCursos() throws Exception {
		doReturn(List.of(new Curso("JAVA001", "Java", "", 40, new String[]{}))).when(cursoService).getAllCursos();
		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk());
	}
}
