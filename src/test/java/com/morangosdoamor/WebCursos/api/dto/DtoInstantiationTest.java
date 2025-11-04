package com.morangosdoamor.WebCursos.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class DtoInstantiationTest {

    @Test
    void deveConstruirDtosSemErros() {
        ConclusaoRequest conclusaoRequest = new ConclusaoRequest(8.0);
        MatriculaRequest matriculaRequest = new MatriculaRequest("JAVA001");
        CursoResponse cursoResponse = new CursoResponse(UUID.randomUUID(), "JAVA001", "Java", "Curso", 40, Set.of());
        AlunoResponse alunoResponse = new AlunoResponse(UUID.randomUUID(), "Ana", "ana@example.com", "MAT-1", LocalDateTime.now());
        MatriculaResponse matriculaResponse = new MatriculaResponse(UUID.randomUUID(), "MATRICULADO", 9.0, LocalDateTime.now(), null, cursoResponse);
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", "erro", "/api");

        assertThat(conclusaoRequest.notaFinal()).isEqualTo(8.0);
        assertThat(matriculaRequest.codigoCurso()).isEqualTo("JAVA001");
        assertThat(alunoResponse.nome()).isEqualTo("Ana");
        assertThat(matriculaResponse.curso().codigo()).isEqualTo("JAVA001");
        assertThat(errorResponse.status()).isEqualTo(400);
    }
}
