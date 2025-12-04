package com.morangosdoamor.WebCursos.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoResponse;
import com.morangosdoamor.WebCursos.api.dto.ConclusaoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoResponse;
import com.morangosdoamor.WebCursos.api.dto.MatriculaRequest;
import com.morangosdoamor.WebCursos.api.dto.MatriculaResponse;
import com.morangosdoamor.WebCursos.config.TestRabbitMQConfig;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

import java.util.List;

/**
 * Teste de integração completo que valida o fluxo end-to-end da aplicação.
 * Utiliza @SpringBootTest para carregar o contexto completo do Spring.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestRabbitMQConfig.class)
@DisplayName("Testes de Integração - Fluxo Completo de Cursos")
class CursoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @BeforeEach
    void setUp() {
        // Limpa os dados antes de cada teste para garantir isolamento
        matriculaRepository.deleteAll();
        alunoRepository.deleteAll();
        // Não limpa cursos pois vêm de data-test.sql
    }

    @Test
    @DisplayName("Deve listar cursos do catálogo")
    void deveListarCursosDoCatalogo() {
        ResponseEntity<List<CursoResponse>> response = restTemplate.exchange(
            "/api/v1/cursos",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CursoResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).anyMatch(c -> c.codigo().equals("JAVA001"));
    }

    @Test
    @DisplayName("Deve criar novo curso via API")
    void deveCriarNovoCurso() {
        CursoRequest novoCurso = new CursoRequest(
            "DOCKER001",
            "Docker Fundamentals",
            "Curso completo de Docker",
            30,
            Set.of()
        );

        ResponseEntity<CursoResponse> response = restTemplate.postForEntity(
            "/api/v1/cursos",
            novoCurso,
            CursoResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("DOCKER001");
        assertThat(response.getBody().nome()).isEqualTo("Docker Fundamentals");
        
        // Verifica se foi persistido no banco
        assertThat(cursoRepository.findByCodigo("DOCKER001")).isPresent();
    }

    @Test
    @DisplayName("Deve criar aluno e matricular em curso")
    void deveCriarAlunoEMatricularEmCurso() {
        // 1. Cria um aluno
        AlunoRequest alunoRequest = new AlunoRequest(
            "João Silva",
            "joao.silva@example.com",
            "MAT-2025-001"
        );

        ResponseEntity<AlunoResponse> alunoResponse = restTemplate.postForEntity(
            "/api/v1/alunos",
            alunoRequest,
            AlunoResponse.class
        );

        assertThat(alunoResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(alunoResponse.getBody()).isNotNull();
        String alunoId = alunoResponse.getBody().id().toString();

        // 2. Matricula o aluno em um curso
        MatriculaRequest matriculaRequest = new MatriculaRequest("JAVA001");

        ResponseEntity<MatriculaResponse> matriculaResponse = restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas",
            matriculaRequest,
            MatriculaResponse.class
        );

        assertThat(matriculaResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(matriculaResponse.getBody()).isNotNull();
        assertThat(matriculaResponse.getBody().curso().codigo()).isEqualTo("JAVA001");
        assertThat(matriculaResponse.getBody().status()).isEqualTo("MATRICULADO");

        // Verifica persistência
        assertThat(matriculaRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Deve executar fluxo completo: criar aluno, matricular, concluir e liberar novos cursos")
    void deveExecutarFluxoCompletoComLiberacaoDeCursos() {
        // 1. Cria um aluno
        AlunoRequest alunoRequest = new AlunoRequest(
            "Maria Santos",
            "maria.santos@example.com",
            "MAT-2025-002"
        );

        ResponseEntity<AlunoResponse> alunoResponse = restTemplate.postForEntity(
            "/api/v1/alunos",
            alunoRequest,
            AlunoResponse.class
        );

        assertThat(alunoResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String alunoId = alunoResponse.getBody().id().toString();

        // 2. Verifica cursos liberados antes da conclusão (deve estar vazio)
        ResponseEntity<List<CursoResponse>> cursosLiberadosAntes = restTemplate.exchange(
            "/api/v1/alunos/" + alunoId + "/cursos/liberados",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CursoResponse>>() {}
        );

        assertThat(cursosLiberadosAntes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cursosLiberadosAntes.getBody()).isEmpty();

        // 3. Matricula em um curso
        MatriculaRequest matriculaRequest = new MatriculaRequest("JAVA001");
        ResponseEntity<MatriculaResponse> matriculaResponse = restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas",
            matriculaRequest,
            MatriculaResponse.class
        );

        assertThat(matriculaResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String matriculaId = matriculaResponse.getBody().id().toString();

        // 4. Conclui o curso com nota >= 7.0
        ConclusaoRequest conclusaoRequest = new ConclusaoRequest(8.5);
        ResponseEntity<MatriculaResponse> conclusaoResponse = restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas/" + matriculaId + "/conclusao",
            conclusaoRequest,
            MatriculaResponse.class
        );

        assertThat(conclusaoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(conclusaoResponse.getBody().status()).isEqualTo("CONCLUIDO");
        assertThat(conclusaoResponse.getBody().notaFinal()).isEqualTo(8.5);

        // 5. Verifica cursos liberados após conclusão (deve ter 2 cursos - total 3, menos 1 concluído)
        ResponseEntity<List<CursoResponse>> cursosLiberadosDepois = restTemplate.exchange(
            "/api/v1/alunos/" + alunoId + "/cursos/liberados",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CursoResponse>>() {}
        );

        assertThat(cursosLiberadosDepois.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cursosLiberadosDepois.getBody()).hasSize(2);
        
        // Verifica que JAVA001 não está nos cursos liberados (já foi concluído)
        assertThat(cursosLiberadosDepois.getBody())
            .noneMatch(c -> c.codigo().equals("JAVA001"));
    }

    @Test
    @DisplayName("Deve validar que nota abaixo de 7.0 não libera novos cursos")
    void deveValidarQueNotaBaixaNaoLiberaCursos() {
        // 1. Cria aluno
        AlunoRequest alunoRequest = new AlunoRequest(
            "Pedro Costa",
            "pedro.costa@example.com",
            "MAT-2025-003"
        );

        ResponseEntity<AlunoResponse> alunoResponse = restTemplate.postForEntity(
            "/api/v1/alunos",
            alunoRequest,
            AlunoResponse.class
        );

        String alunoId = alunoResponse.getBody().id().toString();

        // 2. Matricula
        MatriculaRequest matriculaRequest = new MatriculaRequest("JAVA001");
        ResponseEntity<MatriculaResponse> matriculaResponse = restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas",
            matriculaRequest,
            MatriculaResponse.class
        );

        String matriculaId = matriculaResponse.getBody().id().toString();

        // 3. Conclui com nota < 7.0
        ConclusaoRequest conclusaoRequest = new ConclusaoRequest(5.5);
        ResponseEntity<MatriculaResponse> conclusaoResponse = restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas/" + matriculaId + "/conclusao",
            conclusaoRequest,
            MatriculaResponse.class
        );

        assertThat(conclusaoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(conclusaoResponse.getBody().notaFinal()).isEqualTo(5.5);

        // 4. Verifica que nenhum curso foi liberado
        ResponseEntity<List<CursoResponse>> cursosLiberados = restTemplate.exchange(
            "/api/v1/alunos/" + alunoId + "/cursos/liberados",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CursoResponse>>() {}
        );

        assertThat(cursosLiberados.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cursosLiberados.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar nota final de matrícula concluída")
    void deveBuscarNotaFinalDeMatriculaConcluida() {
        // 1. Cria aluno e matricula
        AlunoRequest alunoRequest = new AlunoRequest(
            "Ana Paula",
            "ana.paula@example.com",
            "MAT-2025-004"
        );

        ResponseEntity<AlunoResponse> alunoResponse = restTemplate.postForEntity(
            "/api/v1/alunos",
            alunoRequest,
            AlunoResponse.class
        );

        String alunoId = alunoResponse.getBody().id().toString();

        MatriculaRequest matriculaRequest = new MatriculaRequest("JAVA001");
        ResponseEntity<MatriculaResponse> matriculaResponse = restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas",
            matriculaRequest,
            MatriculaResponse.class
        );

        String matriculaId = matriculaResponse.getBody().id().toString();

        // 2. Conclui
        ConclusaoRequest conclusaoRequest = new ConclusaoRequest(9.5);
        restTemplate.postForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas/" + matriculaId + "/conclusao",
            conclusaoRequest,
            MatriculaResponse.class
        );

        // 3. Busca nota final
        ResponseEntity<Double> notaResponse = restTemplate.getForEntity(
            "/api/v1/alunos/" + alunoId + "/matriculas/" + matriculaId + "/nota",
            Double.class
        );

        assertThat(notaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notaResponse.getBody()).isEqualTo(9.5);
    }

    @Test
    @DisplayName("Deve buscar curso por ID")
    void deveBuscarCursoPorId() {
        // Primeiro, busca a lista de cursos para obter um ID válido
        ResponseEntity<List<CursoResponse>> listResponse = restTemplate.exchange(
            "/api/v1/cursos",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CursoResponse>>() {}
        );

        assertThat(listResponse.getBody()).isNotEmpty();
        String cursoId = listResponse.getBody().get(0).id().toString();

        // Busca o curso específico
        ResponseEntity<CursoResponse> response = restTemplate.getForEntity(
            "/api/v1/cursos/" + cursoId,
            CursoResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id().toString()).isEqualTo(cursoId);
    }

    @Test
    @DisplayName("Deve atualizar dados do aluno")
    void deveAtualizarDadosDoAluno() {
        // 1. Cria aluno
        AlunoRequest alunoRequest = new AlunoRequest(
            "Carlos Eduardo",
            "carlos.eduardo@example.com",
            "MAT-2025-005"
        );

        ResponseEntity<AlunoResponse> createResponse = restTemplate.postForEntity(
            "/api/v1/alunos",
            alunoRequest,
            AlunoResponse.class
        );

        String alunoId = createResponse.getBody().id().toString();

        // 2. Atualiza o nome do aluno
        com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest updateRequest = 
            new com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest(
                "Carlos Eduardo Silva",
                null,
                null
            );

        HttpEntity<com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest> entity = 
            new HttpEntity<>(updateRequest);

        ResponseEntity<AlunoResponse> updateResponse = restTemplate.exchange(
            "/api/v1/alunos/" + alunoId,
            HttpMethod.PATCH,
            entity,
            AlunoResponse.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().nome()).isEqualTo("Carlos Eduardo Silva");
        assertThat(updateResponse.getBody().email()).isEqualTo("carlos.eduardo@example.com");
    }

    @Test
    @DisplayName("Deve excluir aluno")
    void deveExcluirAluno() {
        // 1. Cria aluno
        AlunoRequest alunoRequest = new AlunoRequest(
            "Teste Delete",
            "teste.delete@example.com",
            "MAT-DELETE"
        );

        ResponseEntity<AlunoResponse> createResponse = restTemplate.postForEntity(
            "/api/v1/alunos",
            alunoRequest,
            AlunoResponse.class
        );

        String alunoId = createResponse.getBody().id().toString();

        // 2. Exclui o aluno
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            "/api/v1/alunos/" + alunoId,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 3. Verifica que foi removido do banco
        assertThat(alunoRepository.findById(java.util.UUID.fromString(alunoId))).isEmpty();
    }
}
