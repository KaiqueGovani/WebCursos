package com.morangosdoamor.WebCursos.service;

import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import com.morangosdoamor.WebCursos.exception.BusinessException;
import com.morangosdoamor.WebCursos.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
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
 * Testes Unitários de CursoService com Mocks
 * 
 * IMPORTÂNCIA DE TRANSFORMAR TESTES DE INTEGRAÇÃO EM UNITÁRIOS:
 * 
 * 1. PIRÂMIDE DE TESTES:
 *    - Base (maioria): Testes Unitários - rápidos, focados
 *    - Meio: Testes de Integração - médios, validam interações
 *    - Topo (minoria): Testes E2E - lentos, validam sistema completo
 * 
 * 2. FEEDBACK RÁPIDO:
 *    - Unitário: ~10ms por teste
 *    - Integração (@DataJpaTest): ~500ms por teste
 *    - E2E (@SpringBootTest): ~2-5s por teste
 * 
 * 3. MANUTENÇÃO:
 *    - Unitário: Isola falhas precisamente
 *    - Integração: Falha pode ser banco, JPA, SQL, etc
 * 
 * 4. CI/CD:
 *    - Unitários rodam em segundos no pipeline
 *    - Integração pode levar minutos
 * 
 * 5. TDD (Test-Driven Development):
 *    - Unitários permitem desenvolver sem infraestrutura
 *    - Red -> Green -> Refactor ciclo rápido
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CursoService - Testes Unitários com Mocks")
class CursoServiceTest {
    
    @Mock
    private CursoRepository cursoRepository;
    
    @InjectMocks
    private CursoService cursoService;
    
    @Test
    @DisplayName("Deve criar curso com sucesso")
    void deveCriarCursoComSucesso() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("Java Avançado")
            .descricao("Curso de Java com Spring Boot")
            .cargaHoraria(80)
            .prerequisitos(new String[]{"Java Básico", "POO"})
            .build();
        
        Curso cursoSalvo = new Curso(
            "id-123",
            "Java Avançado",
            "Curso de Java com Spring Boot",
            new CargaHoraria(80),
            new String[]{"Java Básico", "POO"}
        );
        
        when(cursoRepository.existsByNome("Java Avançado")).thenReturn(false);
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoSalvo);
        
        // Act
        CursoResponseDTO resultado = cursoService.criar(dto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Java Avançado", resultado.getNome());
        assertEquals(80, resultado.getCargaHoraria());
        
        verify(cursoRepository, times(1)).existsByNome("Java Avançado");
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar curso com nome duplicado")
    void deveLancarExcecaoAoCriarCursoComNomeDuplicado() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("Python Básico")
            .descricao("Introdução ao Python")
            .cargaHoraria(40)
            .build();
        
        when(cursoRepository.existsByNome("Python Básico")).thenReturn(true);
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.criar(dto)
        );
        
        assertTrue(exception.getMessage().contains("Já existe um curso com o nome"));
        verify(cursoRepository, never()).save(any(Curso.class));
    }
    
    @Test
    @DisplayName("Deve buscar curso por ID")
    void deveBuscarCursoPorId() {
        // Arrange
        String id = "id-456";
        Curso curso = new Curso(
            id,
            "React Native",
            "Desenvolvimento mobile",
            new CargaHoraria(50),
            null
        );
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(curso));
        
        // Act
        CursoDetailResponseDTO resultado = cursoService.buscarPorId(id);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("React Native", resultado.getNome());
        assertEquals(50, resultado.getCargaHoraria());
        
        // Verificar conversões de carga horária
        assertNotNull(resultado.getCargaHorariaEmDias());
        assertNotNull(resultado.getCargaHorariaEmSemanas());
        
        verify(cursoRepository, times(1)).findById(id);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar curso inexistente")
    void deveLancarExcecaoAoBuscarCursoInexistente() {
        // Arrange
        String id = "id-inexistente";
        when(cursoRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cursoService.buscarPorId(id)
        );
        
        assertTrue(exception.getMessage().contains("Curso"));
        verify(cursoRepository, times(1)).findById(id);
    }
    
    @Test
    @DisplayName("Deve listar todos os cursos")
    void deveListarTodosOsCursos() {
        // Arrange
        List<Curso> cursos = Arrays.asList(
            new Curso("1", "Curso A", null, new CargaHoraria(30), null),
            new Curso("2", "Curso B", null, new CargaHoraria(40), null),
            new Curso("3", "Curso C", null, new CargaHoraria(50), null)
        );
        
        when(cursoRepository.findAll()).thenReturn(cursos);
        
        // Act
        List<CursoResponseDTO> resultado = cursoService.listarTodos();
        
        // Assert
        assertEquals(3, resultado.size());
        verify(cursoRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Deve buscar cursos por carga horária mínima")
    void deveBuscarCursosPorCargaHorariaMinima() {
        // Arrange
        List<Curso> cursos = Arrays.asList(
            new Curso("1", "Curso Médio", null, new CargaHoraria(50), null),
            new Curso("2", "Curso Longo", null, new CargaHoraria(100), null)
        );
        
        when(cursoRepository.findByCargaHorariaMinima(50)).thenReturn(cursos);
        
        // Act
        List<CursoResponseDTO> resultado = cursoService.buscarPorCargaHorariaMinima(50);
        
        // Assert
        assertEquals(2, resultado.size());
        verify(cursoRepository, times(1)).findByCargaHorariaMinima(50);
    }
    
    @Test
    @DisplayName("Deve lançar exceção para carga horária mínima nula")
    void deveLancarExcecaoParaCargaHorariaMinimaNull() {
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.buscarPorCargaHorariaMinima(null)
        );
        
        assertTrue(exception.getMessage().contains("Carga horária mínima"));
        verify(cursoRepository, never()).findByCargaHorariaMinima(anyInt());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para carga horária mínima negativa")
    void deveLancarExcecaoParaCargaHorariaMinimaNegativa() {
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.buscarPorCargaHorariaMinima(-10)
        );
        
        assertTrue(exception.getMessage().contains("maior ou igual a zero"));
    }
    
    @Test
    @DisplayName("Deve buscar cursos por carga horária máxima")
    void deveBuscarCursosPorCargaHorariaMaxima() {
        // Arrange
        List<Curso> cursos = Arrays.asList(
            new Curso("1", "Curso Curto", null, new CargaHoraria(20), null),
            new Curso("2", "Curso Médio", null, new CargaHoraria(60), null)
        );
        
        when(cursoRepository.findByCargaHorariaMaxima(60)).thenReturn(cursos);
        
        // Act
        List<CursoResponseDTO> resultado = cursoService.buscarPorCargaHorariaMaxima(60);
        
        // Assert
        assertEquals(2, resultado.size());
        verify(cursoRepository, times(1)).findByCargaHorariaMaxima(60);
    }
    
    @Test
    @DisplayName("Deve lançar exceção para carga horária máxima nula")
    void deveLancarExcecaoParaCargaHorariaMaximaNull() {
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.buscarPorCargaHorariaMaxima(null)
        );
        
        assertTrue(exception.getMessage().contains("Carga horária máxima"));
    }

    @Test
    @DisplayName("Deve lançar exceção para carga horária máxima negativa")
    void deveLancarExcecaoParaCargaHorariaMaximaNegativa() {
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.buscarPorCargaHorariaMaxima(-10)
        );
        
        assertTrue(exception.getMessage().contains("maior ou igual a zero"));
        verify(cursoRepository, never()).findByCargaHorariaMaxima(anyInt());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar curso inexistente")
    void deveLancarExcecaoAoTentarAtualizarCursoInexistente() {
        // Arrange
        String id = "id-inexistente";
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .build();
        
        when(cursoRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cursoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Curso"));
        assertTrue(exception.getMessage().contains(id));
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository, never()).save(any(Curso.class));
    }
    
    @Test
    @DisplayName("Deve atualizar curso")
    void deveAtualizarCurso() {
        // Arrange
        String id = "id-789";
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .cargaHoraria(100)
            .build();
        
        Curso cursoExistente = new Curso(
            id,
            "Nome Original",
            "Descrição",
            new CargaHoraria(50),
            null
        );
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente);
        
        // Act
        CursoResponseDTO resultado = cursoService.atualizar(id, dto);
        
        // Assert
        assertNotNull(resultado);
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }
    
    @Test
    @DisplayName("Deve validar nome único ao atualizar")
    void deveValidarNomeUnicoAoAtualizar() {
        // Arrange
        String id = "id-111";
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Novo Nome")
            .build();
        
        Curso cursoExistente = new Curso(
            id,
            "Nome Antigo",
            "Descrição",
            new CargaHoraria(40),
            null
        );
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.existsByNome("Novo Nome")).thenReturn(true);
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Já existe um curso com o nome"));
    }
    
    @Test
    @DisplayName("Deve excluir curso existente")
    void deveExcluirCursoExistente() {
        // Arrange
        String id = "id-delete";
        when(cursoRepository.existsById(id)).thenReturn(true);
        doNothing().when(cursoRepository).deleteById(id);
        
        // Act
        cursoService.excluir(id);
        
        // Assert
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository, times(1)).deleteById(id);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao excluir curso inexistente")
    void deveLancarExcecaoAoExcluirCursoInexistente() {
        // Arrange
        String id = "id-inexistente";
        when(cursoRepository.existsById(id)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cursoService.excluir(id)
        );
        
        assertTrue(exception.getMessage().contains("Curso"));
        verify(cursoRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Deve atualizar curso sem alterar nome")
    void deveAtualizarCursoSemAlterarNome() {
        // Arrange
        String id = "id-update-same";
        Curso cursoExistente = new Curso(
            id,
            "Curso Java",
            "Descrição antiga",
            new CargaHoraria(40),
            new String[]{"Python"}
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Curso Java") // Mesmo nome
            .descricao("Descrição nova")
            .cargaHoraria(60)
            .build();
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente);
        
        // Act
        CursoResponseDTO resultado = cursoService.atualizar(id, dto);
        
        // Assert
        assertNotNull(resultado);
        // Não deve validar nome duplicado se não mudou
        verify(cursoRepository, never()).existsByNome(anyString());
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas descrição mantendo nome inalterado")
    void deveAtualizarApenasDescricao() {
        // Arrange
        String id = "id-desc-only";
        Curso cursoExistente = new Curso(
            id,
            "Curso Original",
            "Descrição Original",
            new CargaHoraria(30),
            null
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .descricao("Nova Descrição")
            // nome não fornecido (null)
            .build();
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente);
        
        // Act
        cursoService.atualizar(id, dto);
        
        // Assert
        // Não deve validar nome se não foi fornecido
        verify(cursoRepository, never()).existsByNome(anyString());
    }

    @Test
    @DisplayName("Deve atualizar curso alterando nome para um novo nome único")
    void deveAtualizarCursoAlterandoNomeParaNovoNomeUnico() {
        // Arrange
        String id = "id-change-name";
        Curso cursoExistente = new Curso(
            id,
            "Curso Antigo",
            "Descrição",
            new CargaHoraria(40),
            null
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Curso Novo") // Nome diferente
            .build();
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.existsByNome("Curso Novo")).thenReturn(false); // Nome disponível
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente);
        
        // Act
        cursoService.atualizar(id, dto);
        
        // Assert
        verify(cursoRepository, times(1)).existsByNome("Curso Novo"); // Deve validar
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar nome para um já existente")
    void deveLancarExcecaoAoTentarAtualizarNomeParaJaExistente() {
        // Arrange
        String id = "id-name-exists";
        Curso cursoExistente = new Curso(
            id,
            "Curso Original",
            "Descrição",
            new CargaHoraria(50),
            null
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Curso Duplicado")
            .build();
        
        when(cursoRepository.findById(id)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.existsByNome("Curso Duplicado")).thenReturn(true); // Nome já existe
        
        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> cursoService.atualizar(id, dto)
        );
        
        assertTrue(exception.getMessage().contains("Já existe um curso com o nome"));
        verify(cursoRepository, times(1)).existsByNome("Curso Duplicado");
        verify(cursoRepository, never()).save(any(Curso.class));
    }
}
