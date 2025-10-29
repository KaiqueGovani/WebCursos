package com.morangosdoamor.WebCursos.service;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import com.morangosdoamor.WebCursos.exception.BusinessException;
import com.morangosdoamor.WebCursos.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.repository.AlunoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes Unitários de Service com @Mock e @InjectMocks
 * 
 * @ExtendWith(MockitoExtension.class) - Habilita Mockito
 * @Mock - Cria mock do Repository (não usa banco real)
 * @InjectMocks - Injeta mocks no Service automaticamente
 * 
 * POR QUE USAR MOCKS EM TESTES DE SERVICE:
 * 1. ISOLAMENTO: Testa APENAS lógica de negócio do Service
 * 2. VELOCIDADE: Não precisa de banco de dados (milissegundos)
 * 3. CONTROLE: Simula cenários específicos (ex: banco fora do ar)
 * 4. FOCO: Testa regras de negócio, validações, orquestração
 * 
 * DIFERENÇA: Teste Unitário vs Teste de Integração
 * - Unitário (Mock): Service isolado, Repository mockado
 * - Integração (@DataJpaTest): Repository + Banco H2 real
 * - Integração (@SpringBootTest): Toda aplicação + Banco
 * 
 * QUANDO USAR CADA UM:
 * - Unitário: Validações, regras de negócio, fluxos
 * - @DataJpaTest: Queries customizadas, mapeamento JPA
 * - @SpringBootTest: Testes end-to-end, integração completa
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlunoService - Testes Unitários com Mocks")
class AlunoServiceTest {
    
    @Mock
    private AlunoRepository alunoRepository;
    
    @InjectMocks
    private AlunoService alunoService;
    
    @Test
    @DisplayName("Deve criar aluno com sucesso")
    void deveCriarAlunoComSucesso() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("João Silva")
            .email("joao@exemplo.com")
            .matricula("MAT12345")
            .build();
        
        Aluno alunoSalvo = new Aluno(
            "id-123",
            "João Silva",
            new Email("joao@exemplo.com"),
            new Matricula("MAT12345")
        );
        
        // Configurar comportamento do mock
        when(alunoRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(alunoRepository.existsByMatricula(any(Matricula.class))).thenReturn(false);
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoSalvo);
        
        // Act
        AlunoResponseDTO resultado = alunoService.criar(dto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@exemplo.com", resultado.getEmail());
        
        // Verificar que métodos foram chamados
        verify(alunoRepository, times(1)).existsByEmail(any(Email.class));
        verify(alunoRepository, times(1)).existsByMatricula(any(Matricula.class));
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar aluno com email duplicado")
    void deveLancarExcecaoAoCriarAlunoComEmailDuplicado() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("Maria Santos")
            .email("maria@exemplo.com")
            .matricula("MAT54321")
            .build();
        
        when(alunoRepository.existsByEmail(any(Email.class))).thenReturn(true);
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> alunoService.criar(dto)
        );
        
        assertTrue(exception.getMessage().contains("Email já cadastrado"));
        
        // Verificar que save nunca foi chamado
        verify(alunoRepository, never()).save(any(Aluno.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar aluno com matrícula duplicada")
    void deveLancarExcecaoAoCriarAlunoComMatriculaDuplicada() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("Pedro Oliveira")
            .email("pedro@exemplo.com")
            .matricula("MAT99999")
            .build();
        
        when(alunoRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(alunoRepository.existsByMatricula(any(Matricula.class))).thenReturn(true);
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> alunoService.criar(dto)
        );
        
        assertTrue(exception.getMessage().contains("Matrícula já cadastrada"));
        verify(alunoRepository, never()).save(any(Aluno.class));
    }
    
    @Test
    @DisplayName("Deve buscar aluno por ID")
    void deveBuscarAlunoPorId() {
        // Arrange
        String id = "id-123";
        Aluno aluno = new Aluno(
            id,
            "Ana Costa",
            new Email("ana@exemplo.com"),
            new Matricula("MAT11111")
        );
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(aluno));
        
        // Act
        AlunoDetailResponseDTO resultado = alunoService.buscarPorId(id);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Ana Costa", resultado.getNome());
        assertEquals("ana@exemplo.com", resultado.getEmail());
        
        verify(alunoRepository, times(1)).findById(id);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno inexistente")
    void deveLancarExcecaoAoBuscarAlunoInexistente() {
        // Arrange
        String id = "id-inexistente";
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> alunoService.buscarPorId(id)
        );
        
        assertTrue(exception.getMessage().contains("Aluno"));
        verify(alunoRepository, times(1)).findById(id);
    }
    
    @Test
    @DisplayName("Deve buscar aluno por email")
    void deveBuscarAlunoPorEmail() {
        // Arrange
        String email = "carlos@exemplo.com";
        Aluno aluno = new Aluno(
            "id-456",
            "Carlos Lima",
            new Email(email),
            new Matricula("MAT22222")
        );
        
        when(alunoRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(aluno));
        
        // Act
        AlunoResponseDTO resultado = alunoService.buscarPorEmail(email);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Carlos Lima", resultado.getNome());
        
        verify(alunoRepository, times(1)).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Deve buscar aluno por matrícula")
    void deveBuscarAlunoPorMatricula() {
        // Arrange
        String matricula = "MAT12345";
        Aluno aluno = new Aluno(
            "id-789",
            "Paulo Souza",
            new Email("paulo@exemplo.com"),
            new Matricula(matricula)
        );
        
        when(alunoRepository.findByMatricula(any(Matricula.class))).thenReturn(Optional.of(aluno));
        
        // Act
        AlunoResponseDTO resultado = alunoService.buscarPorMatricula(matricula);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Paulo Souza", resultado.getNome());
        assertEquals(matricula, resultado.getMatricula());
        
        verify(alunoRepository, times(1)).findByMatricula(any(Matricula.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno por matrícula inexistente")
    void deveLancarExcecaoAoBuscarAlunoPorMatriculaInexistente() {
        // Arrange
        String matricula = "MATINEXISTENTE";
        when(alunoRepository.findByMatricula(any(Matricula.class))).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> alunoService.buscarPorMatricula(matricula)
        );
        
        assertTrue(exception.getMessage().contains("matrícula"));
        verify(alunoRepository, times(1)).findByMatricula(any(Matricula.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno por email inexistente")
    void deveLancarExcecaoAoBuscarAlunoPorEmailInexistente() {
        // Arrange
        String email = "inexistente@exemplo.com";
        when(alunoRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> alunoService.buscarPorEmail(email)
        );
        
        assertTrue(exception.getMessage().contains("email"));
        verify(alunoRepository, times(1)).findByEmail(any(Email.class));
    }
    
    @Test
    @DisplayName("Deve listar todos os alunos")
    void deveListarTodosOsAlunos() {
        // Arrange
        List<Aluno> alunos = Arrays.asList(
            new Aluno("1", "Aluno 1", new Email("a1@exemplo.com"), new Matricula("MAT00001")),
            new Aluno("2", "Aluno 2", new Email("a2@exemplo.com"), new Matricula("MAT00002")),
            new Aluno("3", "Aluno 3", new Email("a3@exemplo.com"), new Matricula("MAT00003"))
        );
        
        when(alunoRepository.findAll()).thenReturn(alunos);
        
        // Act
        List<AlunoResponseDTO> resultado = alunoService.listarTodos();
        
        // Assert
        assertEquals(3, resultado.size());
        verify(alunoRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar aluno inexistente")
    void deveLancarExcecaoAoTentarAtualizarAlunoInexistente() {
        // Arrange
        String id = "id-inexistente";
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> alunoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Aluno"));
        assertTrue(exception.getMessage().contains(id));
        verify(alunoRepository, times(1)).findById(id);
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve atualizar aluno")
    void deveAtualizarAluno() {
        // Arrange
        String id = "id-789";
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .build();
        
        Aluno alunoExistente = new Aluno(
            id,
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT88888")
        );
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoExistente);
        
        // Act
        AlunoResponseDTO resultado = alunoService.atualizar(id, dto);
        
        // Assert
        assertNotNull(resultado);
        verify(alunoRepository, times(1)).findById(id);
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }
    
    @Test
    @DisplayName("Deve validar email único ao atualizar")
    void deveValidarEmailUnicoAoAtualizar() {
        // Arrange
        String id = "id-111";
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .email("novo@exemplo.com")
            .build();
        
        Aluno alunoExistente = new Aluno(
            id,
            "Aluno Teste",
            new Email("antigo@exemplo.com"),
            new Matricula("MAT77777")
        );
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.existsByEmail(any(Email.class))).thenReturn(true);
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> alunoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Email já cadastrado"));
    }
    
    @Test
    @DisplayName("Deve excluir aluno existente")
    void deveExcluirAlunoExistente() {
        // Arrange
        String id = "id-delete";
        when(alunoRepository.existsById(id)).thenReturn(true);
        doNothing().when(alunoRepository).deleteById(id);
        
        // Act
        alunoService.excluir(id);
        
        // Assert
        verify(alunoRepository, times(1)).existsById(id);
        verify(alunoRepository, times(1)).deleteById(id);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao excluir aluno inexistente")
    void deveLancarExcecaoAoExcluirAlunoInexistente() {
        // Arrange
        String id = "id-inexistente";
        when(alunoRepository.existsById(id)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> alunoService.excluir(id)
        );
        
        assertTrue(exception.getMessage().contains("Aluno"));
        verify(alunoRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Deve atualizar aluno sem alterar email e matrícula")
    void deveAtualizarAlunoSemAlterarEmailEMatricula() {
        // Arrange
        String id = "id-update-same";
        Aluno alunoExistente = new Aluno(
            id,
            "Nome Antigo",
            new Email("mesmo@exemplo.com"),
            new Matricula("MAT99999")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .nome("Nome Novo")
            .email("mesmo@exemplo.com") // Mesmo email
            .matricula("MAT99999") // Mesma matrícula
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoExistente);
        
        // Act
        AlunoResponseDTO resultado = alunoService.atualizar(id, dto);
        
        // Assert
        assertNotNull(resultado);
        // Não deve validar email/matrícula únicos se não mudaram
        verify(alunoRepository, never()).existsByEmail(any());
        verify(alunoRepository, never()).existsByMatricula(any());
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas nome mantendo email e matrícula inalterados")
    void deveAtualizarApenasNome() {
        // Arrange
        String id = "id-nome-only";
        Aluno alunoExistente = new Aluno(
            id,
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MATORIGINAL")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .nome("Nome Atualizado")
            // email e matricula não fornecidos (null)
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoExistente);
        
        // Act
        alunoService.atualizar(id, dto);
        
        // Assert
        // Não deve validar email/matrícula se não foram fornecidos
        verify(alunoRepository, never()).existsByEmail(any());
        verify(alunoRepository, never()).existsByMatricula(any());
    }

    @Test
    @DisplayName("Deve atualizar aluno alterando email para um novo email único")
    void deveAtualizarAlunoAlterandoEmailParaNovoEmailUnico() {
        // Arrange
        String id = "id-change-email";
        Aluno alunoExistente = new Aluno(
            id,
            "João Silva",
            new Email("antigo@exemplo.com"),
            new Matricula("MAT12345")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .email("novo@exemplo.com") // Email diferente
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.existsByEmail(any(Email.class))).thenReturn(false); // Email disponível
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoExistente);
        
        // Act
        alunoService.atualizar(id, dto);
        
        // Assert
        verify(alunoRepository, times(1)).existsByEmail(any(Email.class)); // Deve validar
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar email para um já existente")
    void deveLancarExcecaoAoTentarAtualizarEmailParaJaExistente() {
        // Arrange
        String id = "id-email-exists";
        Aluno alunoExistente = new Aluno(
            id,
            "Maria Santos",
            new Email("maria@exemplo.com"),
            new Matricula("MAT99999")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .email("duplicado@exemplo.com")
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.existsByEmail(any(Email.class))).thenReturn(true); // Email já existe
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> alunoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Email já cadastrado"));
        verify(alunoRepository, times(1)).existsByEmail(any(Email.class));
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve atualizar aluno alterando matrícula para uma nova matrícula única")
    void deveAtualizarAlunoAlterandoMatriculaParaNovaMatriculaUnica() {
        // Arrange
        String id = "id-change-mat";
        Aluno alunoExistente = new Aluno(
            id,
            "Pedro Oliveira",
            new Email("pedro@exemplo.com"),
            new Matricula("MATANTIGA")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .matricula("MATNOVA") // Matrícula diferente
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.existsByMatricula(any(Matricula.class))).thenReturn(false); // Disponível
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoExistente);
        
        // Act
        alunoService.atualizar(id, dto);
        
        // Assert
        verify(alunoRepository, times(1)).existsByMatricula(any(Matricula.class)); // Deve validar
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar matrícula para uma já existente")
    void deveLancarExcecaoAoTentarAtualizarMatriculaParaJaExistente() {
        // Arrange
        String id = "id-mat-exists";
        Aluno alunoExistente = new Aluno(
            id,
            "Ana Costa",
            new Email("ana@exemplo.com"),
            new Matricula("MAT11111")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .matricula("MATDUPLICADA")
            .build();
        
        when(alunoRepository.findById(id)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.existsByMatricula(any(Matricula.class))).thenReturn(true); // Já existe
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> alunoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Matrícula já cadastrada"));
        verify(alunoRepository, times(1)).existsByMatricula(any(Matricula.class));
        verify(alunoRepository, never()).save(any(Aluno.class));
    }
}
