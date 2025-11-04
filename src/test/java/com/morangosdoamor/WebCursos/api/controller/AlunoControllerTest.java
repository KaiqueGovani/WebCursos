package com.morangosdoamor.WebCursos.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.mapper.AlunoMapper;
import com.morangosdoamor.WebCursos.api.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.api.mapper.MatriculaMapper;
import com.morangosdoamor.WebCursos.application.service.AlunoService;
import com.morangosdoamor.WebCursos.application.service.CursoService;
import com.morangosdoamor.WebCursos.application.service.MatriculaService;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;

@WebMvcTest(controllers = AlunoController.class)
@Import({AlunoMapper.class, CursoMapper.class, MatriculaMapper.class})
@ActiveProfiles("test")
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlunoService alunoService;

    @MockBean
    private MatriculaService matriculaService;

    @MockBean
    private CursoService cursoService;

    @Test
    void deveCriarAluno() throws Exception {
        Aluno alunoSalvo = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Carla")
            .email(new Email("carla@example.com"))
            .matricula("MAT-2024")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoService.criar(any(Aluno.class))).thenReturn(alunoSalvo);

        AlunoRequest request = new AlunoRequest("Carla", "carla@example.com", "MAT-2024");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().exists("Location"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nome", is("Carla")))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.email", is("carla@example.com")));
    }

    @Test
    void deveListarCursosLiberados() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(40)
            .build();

        when(cursoService.buscarCursosLiberados(any(UUID.class))).thenReturn(List.of(curso));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/alunos/{alunoId}/cursos/liberados", UUID.randomUUID()))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$", hasSize(1)))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].codigo", is("JAVA001")));
    }

    @Test
    void deveListarMatriculasDoAluno() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(40)
            .build();

        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .dataMatricula(LocalDateTime.now())
            .build();

        when(matriculaService.listarPorAluno(any(UUID.class))).thenReturn(List.of(matricula));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/alunos/{alunoId}/matriculas", UUID.randomUUID()))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$", hasSize(1)))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].status", is("MATRICULADO")));
    }

    @Test
    void deveMatricularAlunoEmCurso() throws Exception {
        UUID alunoId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(40)
            .build();

        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .dataMatricula(LocalDateTime.now())
            .build();

        when(matriculaService.matricular(alunoId, "JAVA001")).thenReturn(matricula);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/alunos/{alunoId}/matriculas", alunoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new com.morangosdoamor.WebCursos.api.dto.MatriculaRequest("JAVA001"))))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.curso.codigo", is("JAVA001")));
    }

    @Test
    void deveBuscarAlunoPorId() throws Exception {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder()
            .id(alunoId)
            .nome("Beatriz")
            .email(new Email("bia@example.com"))
            .matricula("MAT-33")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoService.buscarPorId(alunoId)).thenReturn(aluno);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/alunos/{alunoId}", alunoId))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.matricula", is("MAT-33")));
    }

    @Test
    void deveConcluirMatriculaDoAluno() throws Exception {
        UUID alunoId = UUID.randomUUID();
        UUID matriculaId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(40)
            .build();

        Matricula matricula = Matricula.builder()
            .id(matriculaId)
            .curso(curso)
            .status(MatriculaStatus.CONCLUIDO)
            .notaFinal(9.0)
            .dataMatricula(LocalDateTime.now())
            .dataConclusao(LocalDateTime.now())
            .build();

        when(matriculaService.concluir(alunoId, matriculaId, 9.0)).thenReturn(matricula);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/conclusao", alunoId, matriculaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new com.morangosdoamor.WebCursos.api.dto.ConclusaoRequest(9.0))))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.notaFinal", is(9.0)));
    }

    @Test
    void deveRetornarNotaQuandoDisponivel() throws Exception {
        UUID alunoId = UUID.randomUUID();
        UUID matriculaId = UUID.randomUUID();
        when(matriculaService.buscarNotaFinal(alunoId, matriculaId)).thenReturn(8.0);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/nota", alunoId, matriculaId))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("8.0"));
    }

    @Test
    void deveRetornarNoContentQuandoNotaNaoEncontrada() throws Exception {
        UUID alunoId = UUID.randomUUID();
        UUID matriculaId = UUID.randomUUID();
        when(matriculaService.buscarNotaFinal(alunoId, matriculaId)).thenReturn(null);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/nota", alunoId, matriculaId))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent());
    }
}
