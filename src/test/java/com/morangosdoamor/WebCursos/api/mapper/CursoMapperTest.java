package com.morangosdoamor.WebCursos.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CursoMapperTest {

    private final CursoMapper mapper = new CursoMapper();

    @Test
    void deveRetornarNuloQuandoCursoAusente() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
