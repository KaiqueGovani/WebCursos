package com.morangosdoamor.WebCursos.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;

class AlunoMapperTest {

    private final AlunoMapper mapper = new AlunoMapper();

    @Test
    void deveConverterRequestParaEntidade() {
        AlunoRequest request = new AlunoRequest("Rafa", "rafa@example.com", "MAT-7");
        Aluno aluno = mapper.toEntity(request);

        assertThat(aluno.getNome()).isEqualTo("Rafa");
        assertThat(aluno.getEmail().getValue()).isEqualTo("rafa@example.com");
    }

    @Test
    void deveConverterAlunoSemEmail() {
        Aluno aluno = Aluno.builder()
            .id(java.util.UUID.randomUUID())
            .nome("Sem Email")
            .matricula("MAT-8")
            .criadoEm(LocalDateTime.now())
            .build();

        assertThat(mapper.toResponse(aluno).email()).isNull();
    }
}
