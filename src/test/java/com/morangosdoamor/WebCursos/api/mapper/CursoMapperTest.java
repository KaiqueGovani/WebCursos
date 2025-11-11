package com.morangosdoamor.WebCursos.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.morangosdoamor.WebCursos.api.dto.CursoDetailResponse;
import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoUpdateRequest;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;

class CursoMapperTest {

    private final CursoMapper mapper = new CursoMapper();

    @Test
    void deveRetornarNuloQuandoCursoAusente() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void deveConverterCursoParaResponse() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso de Java")
            .cargaHoraria(new CargaHoraria(40))
            .prerequisitos(Set.of("WEB001"))
            .build();

        var response = mapper.toResponse(curso);

        assertThat(response.id()).isEqualTo(curso.getId());
        assertThat(response.codigo()).isEqualTo("JAVA001");
        assertThat(response.nome()).isEqualTo("Java");
        assertThat(response.descricao()).isEqualTo("Curso de Java");
        assertThat(response.cargaHoraria()).isEqualTo(40);
        assertThat(response.prerequisitos()).contains("WEB001");
    }

    @Test
    void deveConverterCursoSemCargaHorariaParaResponse() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("PYTHON001")
            .nome("Python")
            .descricao("Curso de Python")
            .cargaHoraria(null)
            .build();

        var response = mapper.toResponse(curso);

        assertThat(response.cargaHoraria()).isEqualTo(0);
    }

    @Test
    void deveRetornarNuloQuandoCursoAusenteParaDetailResponse() {
        assertThat(mapper.toDetailResponse(null)).isNull();
    }

    @Test
    void deveConverterCursoParaDetailResponse() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Curso de Java")
            .cargaHoraria(new CargaHoraria(40))
            .prerequisitos(Set.of("WEB001"))
            .build();

        CursoDetailResponse detail = mapper.toDetailResponse(curso);

        assertThat(detail.id()).isEqualTo(curso.getId());
        assertThat(detail.codigo()).isEqualTo("JAVA001");
        assertThat(detail.nome()).isEqualTo("Java");
        assertThat(detail.cargaHoraria()).isEqualTo(40);
        assertThat(detail.cargaHorariaEmDias()).isEqualTo(5); // 40 / 8 = 5
        assertThat(detail.cargaHorariaEmSemanas()).isEqualTo(1); // 40 / 40 = 1
        assertThat(detail.prerequisitos()).contains("WEB001");
    }

    @Test
    void deveConverterCursoSemCargaHorariaParaDetailResponse() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("PYTHON001")
            .nome("Python")
            .descricao("Curso de Python")
            .cargaHoraria(null)
            .build();

        CursoDetailResponse detail = mapper.toDetailResponse(curso);

        assertThat(detail.cargaHoraria()).isEqualTo(0);
        assertThat(detail.cargaHorariaEmDias()).isEqualTo(0);
        assertThat(detail.cargaHorariaEmSemanas()).isEqualTo(0);
    }

    @Test
    void deveConverterRequestDTOParaEntidade() {
        CursoRequest dto = new CursoRequest(
            "PYTHON001",
            "Python",
            "Curso de Python",
            60,
            Set.of("JAVA001")
        );

        Curso curso = mapper.toEntity(dto);

        assertThat(curso.getCodigo()).isEqualTo("PYTHON001");
        assertThat(curso.getNome()).isEqualTo("Python");
        assertThat(curso.getDescricao()).isEqualTo("Curso de Python");
        assertThat(curso.getCargaHoraria().getHoras()).isEqualTo(60);
        assertThat(curso.getPrerequisitos()).contains("JAVA001");
    }

    @Test
    void deveConverterRequestDTOSemPrerequisitosParaEntidade() {
        CursoRequest dto = new CursoRequest(
            "WEB001",
            "Web",
            "Curso de Web",
            50,
            null
        );

        Curso curso = mapper.toEntity(dto);

        assertThat(curso.getPrerequisitos()).isEmpty();
    }

    @Test
    void deveAtualizarEntidadeComTodosOsCampos() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java Antigo")
            .descricao("Descrição antiga")
            .cargaHoraria(new CargaHoraria(40))
            .prerequisitos(new HashSet<>())
            .build();

        CursoUpdateRequest dto = new CursoUpdateRequest(
            "JAVA002",
            "Java Novo",
            "Nova descrição",
            60,
            Set.of("WEB001")
        );

        mapper.updateEntity(curso, dto);

        assertThat(curso.getCodigo()).isEqualTo("JAVA002");
        assertThat(curso.getNome()).isEqualTo("Java Novo");
        assertThat(curso.getDescricao()).isEqualTo("Nova descrição");
        assertThat(curso.getCargaHoraria().getHoras()).isEqualTo(60);
        assertThat(curso.getPrerequisitos()).contains("WEB001");
    }

    @Test
    void deveAtualizarEntidadeComCamposParciais() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Descrição original")
            .cargaHoraria(new CargaHoraria(40))
            .prerequisitos(new HashSet<>())
            .build();

        CursoUpdateRequest dto = new CursoUpdateRequest(
            null,
            "Java Atualizado",
            null,
            null,
            null
        );

        mapper.updateEntity(curso, dto);

        assertThat(curso.getCodigo()).isEqualTo("JAVA001");
        assertThat(curso.getNome()).isEqualTo("Java Atualizado");
        assertThat(curso.getDescricao()).isEqualTo("Descrição original");
        assertThat(curso.getCargaHoraria().getHoras()).isEqualTo(40);
    }

    @Test
    void deveIgnorarCamposVaziosNaAtualizacao() {
        Curso curso = Curso.builder()
            .id(UUID.randomUUID())
            .codigo("JAVA001")
            .nome("Java")
            .descricao("Descrição")
            .cargaHoraria(new CargaHoraria(40))
            .prerequisitos(new HashSet<>())
            .build();

        CursoUpdateRequest dto = new CursoUpdateRequest(
            "",
            null,
            "",
            null,
            null
        );

        mapper.updateEntity(curso, dto);

        assertThat(curso.getCodigo()).isEqualTo("JAVA001");
        assertThat(curso.getNome()).isEqualTo("Java");
        assertThat(curso.getDescricao()).isEqualTo("Descrição");
    }
}
