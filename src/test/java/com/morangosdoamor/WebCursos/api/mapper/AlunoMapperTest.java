package com.morangosdoamor.WebCursos.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.morangosdoamor.WebCursos.api.dto.AlunoDetailResponse;
import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;

class AlunoMapperTest {

    private AlunoMapper mapper;

    @BeforeEach
    void setUp() {
        CursoMapper cursoMapper = new CursoMapper();
        MatriculaMapper matriculaMapper = new MatriculaMapper(cursoMapper);
        mapper = new AlunoMapper(matriculaMapper);
    }

    @Test
    void deveConverterRequestParaEntidade() {
        AlunoRequest request = new AlunoRequest("Rafa", "rafa@example.com", "MAT-7");
        Aluno aluno = mapper.toEntity(request);

        assertThat(aluno.getNome()).isEqualTo("Rafa");
        assertThat(aluno.getEmail().getValue()).isEqualTo("rafa@example.com");
        assertThat(aluno.getMatricula()).isEqualTo("MAT-7");
        assertThat(aluno.getCriadoEm()).isNotNull();
    }

    @Test
    void deveConverterAlunoSemEmail() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Sem Email")
            .matricula("MAT-8")
            .criadoEm(LocalDateTime.now())
            .build();

        assertThat(mapper.toResponse(aluno).email()).isNull();
    }

    @Test
    void deveConverterAlunoParaResponse() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("João")
            .email(new Email("joao@example.com"))
            .matricula("MAT-9")
            .criadoEm(LocalDateTime.now())
            .build();

        var response = mapper.toResponse(aluno);

        assertThat(response.id()).isEqualTo(aluno.getId());
        assertThat(response.nome()).isEqualTo("João");
        assertThat(response.email()).isEqualTo("joao@example.com");
        assertThat(response.matricula()).isEqualTo("MAT-9");
    }

    @Test
    void deveConverterAlunoParaDetailResponseComMatriculas() {
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

        Aluno aluno = Aluno.builder()
            .id(alunoId)
            .nome("Maria")
            .email(new Email("maria@example.com"))
            .matricula("MAT-10")
            .criadoEm(LocalDateTime.now())
            .matriculas(new HashSet<>(List.of(matricula)))
            .build();

        matricula.setAluno(aluno);

        AlunoDetailResponse detail = mapper.toDetailResponse(aluno);

        assertThat(detail.id()).isEqualTo(alunoId);
        assertThat(detail.nome()).isEqualTo("Maria");
        assertThat(detail.email()).isEqualTo("maria@example.com");
        assertThat(detail.matriculas()).hasSize(1);
    }

    @Test
    void deveConverterAlunoParaDetailResponseSemMatriculas() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Pedro")
            .email(new Email("pedro@example.com"))
            .matricula("MAT-11")
            .criadoEm(LocalDateTime.now())
            .matriculas(null)
            .build();

        AlunoDetailResponse detail = mapper.toDetailResponse(aluno);

        assertThat(detail.matriculas()).isEmpty();
    }

    @Test
    void deveAtualizarEntidadeComTodosOsCampos() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Antigo")
            .email(new Email("antigo@example.com"))
            .matricula("MAT-OLD")
            .criadoEm(LocalDateTime.now())
            .build();

        AlunoUpdateRequest dto = new AlunoUpdateRequest("Novo", "novo@example.com", "MAT-NEW");

        mapper.updateEntity(aluno, dto);

        assertThat(aluno.getNome()).isEqualTo("Novo");
        assertThat(aluno.getEmail().getValue()).isEqualTo("novo@example.com");
        assertThat(aluno.getMatricula()).isEqualTo("MAT-NEW");
    }

    @Test
    void deveAtualizarEntidadeComCamposParciais() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Original")
            .email(new Email("original@example.com"))
            .matricula("MAT-ORIG")
            .criadoEm(LocalDateTime.now())
            .build();

        AlunoUpdateRequest dto = new AlunoUpdateRequest("Atualizado", null, null);

        mapper.updateEntity(aluno, dto);

        assertThat(aluno.getNome()).isEqualTo("Atualizado");
        assertThat(aluno.getEmail().getValue()).isEqualTo("original@example.com");
        assertThat(aluno.getMatricula()).isEqualTo("MAT-ORIG");
    }

    @Test
    void deveIgnorarCamposVaziosNaAtualizacao() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Original")
            .email(new Email("original@example.com"))
            .matricula("MAT-ORIG")
            .criadoEm(LocalDateTime.now())
            .build();

        AlunoUpdateRequest dto = new AlunoUpdateRequest("", null, "");

        mapper.updateEntity(aluno, dto);

        assertThat(aluno.getNome()).isEqualTo("Original");
        assertThat(aluno.getEmail().getValue()).isEqualTo("original@example.com");
        assertThat(aluno.getMatricula()).isEqualTo("MAT-ORIG");
    }
}
