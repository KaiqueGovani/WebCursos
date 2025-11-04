package com.morangosdoamor.WebCursos.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.morangosdoamor.WebCursos.api.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.application.service.CursoService;
import com.morangosdoamor.WebCursos.domain.entity.Curso;

@WebMvcTest(controllers = CursoController.class)
@Import(CursoMapper.class)
@ActiveProfiles("test")
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoService cursoService;

    @Test
    void deveRetornarCatalogoDeCursos() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(40)
            .build();

        when(cursoService.listarTodos()).thenReturn(List.of(curso));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/cursos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$", hasSize(1)))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].codigo", is("JAVA001")));
    }
}
