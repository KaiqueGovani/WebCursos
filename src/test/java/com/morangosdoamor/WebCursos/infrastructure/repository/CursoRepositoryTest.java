package com.morangosdoamor.WebCursos.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.morangosdoamor.WebCursos.domain.entity.Curso;

@DataJpaTest
@ActiveProfiles("test")
class CursoRepositoryTest {

    @Autowired
    private CursoRepository cursoRepository;

    @Test
    @DisplayName("Deve localizar curso pelo código")
    void deveLocalizarCursoPorCodigo() {
        Optional<Curso> curso = cursoRepository.findByCodigo("JAVA001");
        assertThat(curso).isPresent();
        assertThat(curso.get().getNome()).isEqualTo("Programação Java");
    }

    @Test
    @DisplayName("Deve retornar vazio quando curso não existe")
    void deveRetornarVazioQuandoCursoNaoExiste() {
        Optional<Curso> curso = cursoRepository.findByCodigo("INEXISTENTE");
        assertThat(curso).isNotPresent();
    }
}
