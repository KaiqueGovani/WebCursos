package com.morangosdoamor.WebCursos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest;
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

    @Test
    void deveListarTodosOsAlunos() {
        Aluno aluno1 = Aluno.builder().id(UUID.randomUUID()).nome("Ana").matricula("MAT-1").criadoEm(LocalDateTime.now()).build();
        Aluno aluno2 = Aluno.builder().id(UUID.randomUUID()).nome("Bruno").matricula("MAT-2").criadoEm(LocalDateTime.now()).build();

        when(alunoRepository.findAll(any(Sort.class))).thenReturn(List.of(aluno1, aluno2));

        List<Aluno> alunos = alunoService.listarTodos();

        assertThat(alunos).hasSize(2);
        assertThat(alunos).extracting(Aluno::getNome).containsExactly("Ana", "Bruno");
    }

    @Test
    void deveBuscarAlunoPorEmail() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Carla")
            .email(new Email("carla@example.com"))
            .matricula("MAT-3")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findByEmail("carla@example.com")).thenReturn(Optional.of(aluno));

        Aluno encontrado = alunoService.buscarPorEmail("carla@example.com");

        assertThat(encontrado.getEmail().getValue()).isEqualTo("carla@example.com");
    }

    @Test
    void deveLancarErroQuandoEmailNaoEncontrado() {
        when(alunoRepository.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.buscarPorEmail("inexistente@example.com"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Aluno não encontrado");
    }

    @Test
    void deveBuscarAlunoPorMatricula() {
        Aluno aluno = Aluno.builder()
            .id(UUID.randomUUID())
            .nome("Daniel")
            .email(new Email("daniel@example.com"))
            .matricula("MAT-4")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findByMatricula("MAT-4")).thenReturn(Optional.of(aluno));

        Aluno encontrado = alunoService.buscarPorMatricula("MAT-4");

        assertThat(encontrado.getMatricula()).isEqualTo("MAT-4");
    }

    @Test
    void deveLancarErroQuandoMatriculaNaoEncontrada() {
        when(alunoRepository.findByMatricula("MAT-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.buscarPorMatricula("MAT-999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Aluno não encontrado");
    }

    @Test
    void deveAtualizarAluno() {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder()
            .id(alunoId)
            .nome("Eduardo")
            .email(new Email("eduardo@example.com"))
            .matricula("MAT-5")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("novo@example.com")).thenReturn(Optional.empty());
        when(alunoRepository.findByMatricula("MAT-6")).thenReturn(Optional.empty());
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoUpdateRequest dto = new AlunoUpdateRequest("Eduardo Atualizado", "novo@example.com", "MAT-6");
        Aluno atualizado = alunoService.atualizar(alunoId, dto);

        assertThat(atualizado.getNome()).isEqualTo("Eduardo Atualizado");
        verify(alunoRepository).save(aluno);
    }

    @Test
    void deveLancarErroAoAtualizarComEmailDuplicado() {
        UUID alunoId = UUID.randomUUID();
        UUID outroId = UUID.randomUUID();
        Aluno aluno = Aluno.builder().id(alunoId).nome("Eduardo").matricula("MAT-5").criadoEm(LocalDateTime.now()).build();
        Aluno outro = Aluno.builder().id(outroId).nome("Outro").matricula("MAT-7").criadoEm(LocalDateTime.now()).build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("duplicado@example.com")).thenReturn(Optional.of(outro));

        AlunoUpdateRequest dto = new AlunoUpdateRequest(null, "duplicado@example.com", null);

        assertThatThrownBy(() -> alunoService.atualizar(alunoId, dto))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void devePermitirAtualizarComMesmoEmail() {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder()
            .id(alunoId)
            .nome("Eduardo")
            .email(new Email("eduardo@example.com"))
            .matricula("MAT-5")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("eduardo@example.com")).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoUpdateRequest dto = new AlunoUpdateRequest("Eduardo Novo", "eduardo@example.com", null);
        Aluno atualizado = alunoService.atualizar(alunoId, dto);

        assertThat(atualizado.getNome()).isEqualTo("Eduardo Novo");
        verify(alunoRepository).save(aluno);
    }

    @Test
    void deveLancarErroAoAtualizarComMatriculaDuplicada() {
        UUID alunoId = UUID.randomUUID();
        UUID outroId = UUID.randomUUID();
        Aluno aluno = Aluno.builder().id(alunoId).nome("Eduardo").matricula("MAT-5").criadoEm(LocalDateTime.now()).build();
        Aluno outro = Aluno.builder().id(outroId).nome("Outro").matricula("MAT-7").criadoEm(LocalDateTime.now()).build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByMatricula("MAT-7")).thenReturn(Optional.of(outro));

        AlunoUpdateRequest dto = new AlunoUpdateRequest(null, null, "MAT-7");

        assertThatThrownBy(() -> alunoService.atualizar(alunoId, dto))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Matrícula já cadastrada");
    }

    @Test
    void devePermitirAtualizarComMesmaMatricula() {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder()
            .id(alunoId)
            .nome("Eduardo")
            .email(new Email("eduardo@example.com"))
            .matricula("MAT-5")
            .criadoEm(LocalDateTime.now())
            .build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByMatricula("MAT-5")).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoUpdateRequest dto = new AlunoUpdateRequest("Eduardo Novo", null, "MAT-5");
        Aluno atualizado = alunoService.atualizar(alunoId, dto);

        assertThat(atualizado.getNome()).isEqualTo("Eduardo Novo");
        verify(alunoRepository).save(aluno);
    }

    @Test
    void deveExcluirAluno() {
        UUID alunoId = UUID.randomUUID();
        Aluno aluno = Aluno.builder().id(alunoId).nome("Fernando").matricula("MAT-8").criadoEm(LocalDateTime.now()).build();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        alunoService.excluir(alunoId);

        verify(alunoRepository).delete(aluno);
    }
}
