package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    void deveCriarAlunoQuandoMatriculaNaoExistir() {
        Aluno aluno = Aluno.builder()
            .nome("Laura")
            .email(new Email("laura@example.com"))
            .matricula("MAT-99")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findByMatricula("MAT-99")).thenReturn(Optional.empty());
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno salvo = alunoService.criar(aluno);

        assertThat(salvo.getNome()).isEqualTo("Laura");
        verify(alunoRepository).save(aluno);
    }

    @Test
    void deveLancarErroQuandoMatriculaJaExistir() {
        Aluno existente = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Marcos")
            .email(new Email("marcos@example.com"))
            .matricula("MAT-10")
            .build();

        when(alunoRepository.findByMatricula("MAT-10")).thenReturn(Optional.of(existente));

        Aluno novo = Aluno.builder()
            .nome("Outro")
            .email(new Email("outro@example.com"))
            .matricula("MAT-10")
            .build();

        assertThatThrownBy(() -> alunoService.criar(novo))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Matrícula já cadastrada");
    }

    @Test
    void deveBuscarAlunoPorId() {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder()
            .id(alunoId)
            .nome("Pedro")
            .email(new Email("pedro@example.com"))
            .matricula("MAT-11")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        Aluno encontrado = alunoService.buscarPorId(alunoId);

        assertThat(encontrado.getId()).isEqualTo(alunoId);
    }

    @Test
    void deveLancarErroQuandoAlunoNaoExiste() {
        UUID alunoId = UUID.randomUUID();
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.buscarPorId(alunoId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Aluno não encontrado");
    }
}
