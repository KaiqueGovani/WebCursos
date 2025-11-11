package com.morangosdoamor.WebCursos.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoUpdateRequest;
import com.morangosdoamor.WebCursos.api.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.application.service.CursoService;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;

@WebMvcTest(controllers = CursoController.class)
@Import(CursoMapper.class)
@ActiveProfiles("test")
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CursoService cursoService;

    @Test
    void deveRetornarCatalogoDeCursos() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        when(cursoService.listarTodos()).thenReturn(List.of(curso));

        mockMvc.perform(get("/api/v1/cursos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].codigo", is("JAVA001")));
    }

    @Test
    void deveCriarCurso() throws Exception {
        UUID cursoId = UUID.randomUUID();
        Curso cursoCriado = Curso.builder()
            .id(cursoId)
            .codigo("PYTHON001")
            .nome("Python")
            .descricao("Curso de Python")
            .cargaHoraria(new CargaHoraria(60))
            .build();

        when(cursoService.criar(any(CursoRequest.class))).thenReturn(cursoCriado);

        CursoRequest request = new CursoRequest("PYTHON001", "Python", "Curso de Python", 60, Set.of());

        mockMvc.perform(post("/api/v1/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.codigo", is("PYTHON001")));
    }

    @Test
    void deveBuscarCursoPorId() throws Exception {
        UUID cursoId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(cursoId)
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        when(cursoService.buscarPorId(cursoId)).thenReturn(curso);

        mockMvc.perform(get("/api/v1/cursos/{id}", cursoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigo", is("JAVA001")))
            .andExpect(jsonPath("$.cargaHoraria", is(40)))
            .andExpect(jsonPath("$.cargaHorariaEmDias").exists())
            .andExpect(jsonPath("$.cargaHorariaEmSemanas").exists());
    }

    @Test
    void deveBuscarCursosPorCargaHorariaMinima() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("CURSO1")
            .nome("Curso 1")
            .descricao("Desc")
            .cargaHoraria(new CargaHoraria(50))
            .build();

        when(cursoService.buscarPorCargaHorariaMinima(40)).thenReturn(List.of(curso));

        mockMvc.perform(get("/api/v1/cursos/carga-horaria/minima")
                .param("horas", "40"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].codigo", is("CURSO1")));
    }

    @Test
    void deveBuscarCursosPorCargaHorariaMaxima() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("CURSO2")
            .nome("Curso 2")
            .descricao("Desc")
            .cargaHoraria(new CargaHoraria(30))
            .build();

        when(cursoService.buscarPorCargaHorariaMaxima(50)).thenReturn(List.of(curso));

        mockMvc.perform(get("/api/v1/cursos/carga-horaria/maxima")
                .param("horas", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].codigo", is("CURSO2")));
    }

    @Test
    void deveAtualizarCurso() throws Exception {
        UUID cursoId = UUID.randomUUID();
        Curso cursoAtualizado = Curso.builder()
            .id(cursoId)
            .codigo("JAVA001")
            .nome("Java Atualizado")
            .descricao("Nova descrição")
            .cargaHoraria(new CargaHoraria(50))
            .build();

        when(cursoService.atualizar(any(UUID.class), any(CursoUpdateRequest.class))).thenReturn(cursoAtualizado);

        CursoUpdateRequest dto = new CursoUpdateRequest(null, "Java Atualizado", "Nova descrição", 50, null);

        mockMvc.perform(patch("/api/v1/cursos/{id}", cursoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Java Atualizado")));
    }

    @Test
    void deveExcluirCurso() throws Exception {
        UUID cursoId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(cursoId)
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Desc")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        when(cursoService.buscarPorId(cursoId)).thenReturn(curso);

        mockMvc.perform(delete("/api/v1/cursos/{id}", cursoId))
            .andExpect(status().isNoContent());
    }
}
