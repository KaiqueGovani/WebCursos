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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest;
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
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;

@WebMvcTest(controllers = AlunoController.class)
@Import({AlunoMapper.class, CursoMapper.class, MatriculaMapper.class})
@ActiveProfiles("test")
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlunoService alunoService;

    @MockitoBean
    private MatriculaService matriculaService;

    @MockitoBean
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

        mockMvc.perform(post("/api/v1/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.nome", is("Carla")))
            .andExpect(jsonPath("$.email", is("carla@example.com")));
    }

    @Test
    void deveListarCursosLiberados() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        when(cursoService.buscarCursosLiberados(any(UUID.class))).thenReturn(List.of(curso));

        mockMvc.perform(get("/api/v1/alunos/{alunoId}/cursos/liberados", UUID.randomUUID()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].codigo", is("JAVA001")));
    }

    @Test
    void deveListarMatriculasDoAluno() throws Exception {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .dataMatricula(LocalDateTime.now())
            .build();

        when(matriculaService.listarPorAluno(any(UUID.class))).thenReturn(List.of(matricula));

        mockMvc.perform(get("/api/v1/alunos/{alunoId}/matriculas", UUID.randomUUID()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status", is("MATRICULADO")));
    }

    @Test
    void deveMatricularAlunoEmCurso() throws Exception {
        UUID alunoId = UUID.randomUUID();
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso")
            .cargaHoraria(new CargaHoraria(40))
            .build();

        Matricula matricula = Matricula.builder()
            .id(UUID.randomUUID())
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .dataMatricula(LocalDateTime.now())
            .build();

        when(matriculaService.matricular(alunoId, "JAVA001")).thenReturn(matricula);

        mockMvc.perform(post("/api/v1/alunos/{alunoId}/matriculas", alunoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new com.morangosdoamor.WebCursos.api.dto.MatriculaRequest("JAVA001"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.curso.codigo", is("JAVA001")));
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

        mockMvc.perform(get("/api/v1/alunos/{alunoId}", alunoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matricula", is("MAT-33")));
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
            .cargaHoraria(new CargaHoraria(40))
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

        mockMvc.perform(post("/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/conclusao", alunoId, matriculaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new com.morangosdoamor.WebCursos.api.dto.ConclusaoRequest(9.0))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.notaFinal", is(9.0)));
    }

    @Test
    void deveRetornarNotaQuandoDisponivel() throws Exception {
        UUID alunoId = UUID.randomUUID();
        UUID matriculaId = UUID.randomUUID();
        when(matriculaService.buscarNotaFinal(alunoId, matriculaId)).thenReturn(8.0);

        mockMvc.perform(get("/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/nota", alunoId, matriculaId))
            .andExpect(status().isOk())
            .andExpect(content().string("8.0"));
    }

    @Test
    void deveRetornarNoContentQuandoNotaNaoEncontrada() throws Exception {
        UUID alunoId = UUID.randomUUID();
        UUID matriculaId = UUID.randomUUID();
        when(matriculaService.buscarNotaFinal(alunoId, matriculaId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/alunos/{alunoId}/matriculas/{matriculaId}/nota", alunoId, matriculaId))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveListarTodosOsAlunos() throws Exception {
        Aluno aluno1 = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Ana")
            .email(new Email("ana@example.com"))
            .matricula("MAT-1")
            .criadoEm(LocalDateTime.now())
            .build();

        Aluno aluno2 = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Bruno")
            .email(new Email("bruno@example.com"))
            .matricula("MAT-2")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoService.listarTodos()).thenReturn(List.of(aluno1, aluno2));

        mockMvc.perform(get("/api/v1/alunos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", is("Ana")))
            .andExpect(jsonPath("$[1].nome", is("Bruno")));
    }

    @Test
    void deveBuscarAlunoPorEmail() throws Exception {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Carla")
            .email(new Email("carla@example.com"))
            .matricula("MAT-3")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoService.buscarPorEmail("carla@example.com")).thenReturn(aluno);

        mockMvc.perform(get("/api/v1/alunos/email/carla@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("carla@example.com")));
    }

    @Test
    void deveBuscarAlunoPorMatricula() throws Exception {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Daniel")
            .email(new Email("daniel@example.com"))
            .matricula("MAT-4")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoService.buscarPorMatricula("MAT-4")).thenReturn(aluno);

        mockMvc.perform(get("/api/v1/alunos/matricula/MAT-4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matricula", is("MAT-4")));
    }

    @Test
    void deveAtualizarAluno() throws Exception {
        UUID alunoId = UUID.randomUUID();
        Aluno alunoAtualizado = Aluno.builder()
            .id(alunoId)
            .nome("Eduardo Atualizado")
            .email(new Email("novo@example.com"))
            .matricula("MAT-5")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoService.atualizar(any(UUID.class), any(AlunoUpdateRequest.class))).thenReturn(alunoAtualizado);

        AlunoUpdateRequest dto = new AlunoUpdateRequest("Eduardo Atualizado", "novo@example.com", "MAT-5");

        mockMvc.perform(patch("/api/v1/alunos/{id}", alunoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Eduardo Atualizado")));
    }

    @Test
    void deveExcluirAluno() throws Exception {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder().id(alunoId).nome("Fernando").matricula("MAT-6").criadoEm(LocalDateTime.now()).build();
        when(alunoService.buscarPorId(alunoId)).thenReturn(aluno);

        mockMvc.perform(delete("/api/v1/alunos/{id}", alunoId))
            .andExpect(status().isNoContent());
    }
}
