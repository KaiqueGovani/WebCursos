package com.morangosdoamor.WebCursos.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;

class AlunoEntityTest {

    @Test
    void deveCalcularTotalDeCursosAprovados() {
        Aluno aluno = Aluno.builder()
            .nome("Maria")
            .email(new Email("maria@example.com"))
            .matricula("MAT123")
            .criadoEm(LocalDateTime.now())
            .build();

        Matricula matriculaAprovada = Matricula.builder()
            .status(MatriculaStatus.CONCLUIDO)
            .notaFinal(9.0)
            .build();
        matriculaAprovada.concluir(9.0);

        Matricula matriculaReprovada = Matricula.builder()
            .status(MatriculaStatus.MATRICULADO)
            .notaFinal(5.0)
            .build();

        aluno.adicionarMatricula(matriculaAprovada);
        aluno.adicionarMatricula(matriculaReprovada);

        assertEquals(1, aluno.totalCursosAprovados());
    }

    @Test
    void deveRegistrarCriacaoQuandoDataNula() {
        Aluno aluno = Aluno.builder()
            .nome("João")
            .email(new Email("joao@example.com"))
            .matricula("MAT456")
            .build();

        aluno.registrarCriacaoSeNecessario();

        assertNotNull(aluno.getCriadoEm());
    }

    @Test
    void deveIndicarNaoAprovadoQuandoNotaNaoRegistrada() {
        Matricula matricula = Matricula.builder()
            .status(MatriculaStatus.MATRICULADO)
            .build();

        assertEquals(false, matricula.estaAprovado());
    }
}
