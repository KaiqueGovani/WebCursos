package com.morangosdoamor.WebCursos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import com.morangosdoamor.WebCursos.exception.BusinessException;
import com.morangosdoamor.WebCursos.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.service.CursoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Controller com @WebMvcTest e MockMvc
 * 
 * RESUMO - Por que usar diferentes tipos de testes:
 * 
 * 1. TESTES UNITÁRIOS (Value Objects, sem mocks):
 *    - Testam lógica pura, regras de negócio simples
 *    - Não dependem de frameworks
 *    - Extremamente rápidos (microsegundos)
 * 
 * 2. TESTES UNITÁRIOS COM MOCKS (Service):
 *    - Testam lógica de negócio complexa
 *    - Isolam camada de serviço
 *    - Rápidos (milissegundos)
 * 
 * 3. TESTES DE INTEGRAÇÃO (@DataJpaTest):
 *    - Testam camada de persistência
 *    - Validam queries SQL, mapeamento JPA
 *    - Médios (centenas de milissegundos)
 * 
 * 4. TESTES DE INTEGRAÇÃO (@WebMvcTest):
 *    - Testam camada web (REST API)
 *    - Validam serialização JSON, validações
 *    - Médios (centenas de milissegundos)
 * 
 * 5. TESTES E2E (@SpringBootTest):
 *    - Testam sistema completo
 *    - Validam integração entre camadas
 *    - Lentos (segundos)
 * 
 * ESTRATÉGIA: Muitos testes unitários, alguns de integração, poucos E2E
 */
@WebMvcTest(CursoController.class)
@DisplayName("CursoController - Testes com @WebMvcTest e MockMvc")
class CursoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private CursoService cursoService;
    
    @Test
    @DisplayName("Deve criar curso com sucesso - POST /api/cursos")
    void deveCriarCursoComSucesso() throws Exception {
        // Arrange
        CursoRequestDTO request = CursoRequestDTO.builder()
            .nome("Java Avançado")
            .descricao("Curso de Java com Spring Boot")
            .cargaHoraria(80)
            .prerequisitos(new String[]{"Java Básico", "POO"})
            .build();
        
        CursoResponseDTO response = CursoResponseDTO.builder()
            .id("id-123")
            .nome("Java Avançado")
            .descricao("Curso de Java com Spring Boot")
            .cargaHoraria(80)
            .build();
        
        when(cursoService.criar(any(CursoRequestDTO.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("id-123"))
            .andExpect(jsonPath("$.nome").value("Java Avançado"))
            .andExpect(jsonPath("$.cargaHoraria").value(80));
        
        verify(cursoService, times(1)).criar(any(CursoRequestDTO.class));
    }
    
    @Test
    @DisplayName("Deve retornar 400 ao criar curso com dados inválidos")
    void deveRetornar400AoCriarCursoComDadosInvalidos() throws Exception {
        // Arrange - nome vazio, carga horária inválida
        CursoRequestDTO request = CursoRequestDTO.builder()
            .nome("")
            .cargaHoraria(0)
            .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
        
        verify(cursoService, never()).criar(any(CursoRequestDTO.class));
    }
    
    @Test
    @DisplayName("Deve retornar 400 ao criar curso com nome duplicado")
    void deveRetornar400AoCriarCursoComNomeDuplicado() throws Exception {
        // Arrange
        CursoRequestDTO request = CursoRequestDTO.builder()
            .nome("Python Básico")
            .descricao("Introdução ao Python")
            .cargaHoraria(40)
            .build();
        
        when(cursoService.criar(any(CursoRequestDTO.class)))
            .thenThrow(new BusinessException("Já existe um curso com o nome"));
        
        // Act & Assert
        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve listar todos os cursos - GET /api/cursos")
    void deveListarTodosOsCursos() throws Exception {
        // Arrange
        List<CursoResponseDTO> cursos = Arrays.asList(
            CursoResponseDTO.builder().id("1").nome("Curso A").cargaHoraria(30).build(),
            CursoResponseDTO.builder().id("2").nome("Curso B").cargaHoraria(40).build()
        );
        
        when(cursoService.listarTodos()).thenReturn(cursos);
        
        // Act & Assert
        mockMvc.perform(get("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome").value("Curso A"))
            .andExpect(jsonPath("$[1].nome").value("Curso B"));
        
        verify(cursoService, times(1)).listarTodos();
    }
    
    @Test
    @DisplayName("Deve buscar curso por ID - GET /api/cursos/{id}")
    void deveBuscarCursoPorId() throws Exception {
        // Arrange
        String id = "id-456";
        CursoDetailResponseDTO response = CursoDetailResponseDTO.builder()
            .id(id)
            .nome("React Native")
            .descricao("Desenvolvimento mobile")
            .cargaHoraria(50)
            .cargaHorariaEmDias(7.0)
            .cargaHorariaEmSemanas(2.0)
            .build();
        
        when(cursoService.buscarPorId(id)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/cursos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.nome").value("React Native"))
            .andExpect(jsonPath("$.cargaHoraria").value(50))
            .andExpect(jsonPath("$.cargaHorariaEmDias").value(7.0))
            .andExpect(jsonPath("$.cargaHorariaEmSemanas").value(2.0));
        
        verify(cursoService, times(1)).buscarPorId(id);
    }
    
    @Test
    @DisplayName("Deve retornar 404 ao buscar curso inexistente")
    void deveRetornar404AoBuscarCursoInexistente() throws Exception {
        // Arrange
        String id = "id-inexistente";
        when(cursoService.buscarPorId(id))
            .thenThrow(new ResourceNotFoundException("Curso", id));
        
        // Act & Assert
        mockMvc.perform(get("/api/cursos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Deve buscar cursos por carga horária mínima - GET /api/cursos/carga-horaria/minima")
    void deveBuscarCursosPorCargaHorariaMinima() throws Exception {
        // Arrange
        List<CursoResponseDTO> cursos = Arrays.asList(
            CursoResponseDTO.builder().id("1").nome("Curso Médio").cargaHoraria(50).build(),
            CursoResponseDTO.builder().id("2").nome("Curso Longo").cargaHoraria(100).build()
        );
        
        when(cursoService.buscarPorCargaHorariaMinima(50)).thenReturn(cursos);
        
        // Act & Assert
        mockMvc.perform(get("/api/cursos/carga-horaria/minima")
                .param("horas", "50")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].cargaHoraria", greaterThanOrEqualTo(50)));
        
        verify(cursoService, times(1)).buscarPorCargaHorariaMinima(50);
    }
    
    @Test
    @DisplayName("Deve buscar cursos por carga horária máxima - GET /api/cursos/carga-horaria/maxima")
    void deveBuscarCursosPorCargaHorariaMaxima() throws Exception {
        // Arrange
        List<CursoResponseDTO> cursos = Arrays.asList(
            CursoResponseDTO.builder().id("1").nome("Curso Curto").cargaHoraria(20).build(),
            CursoResponseDTO.builder().id("2").nome("Curso Médio").cargaHoraria(60).build()
        );
        
        when(cursoService.buscarPorCargaHorariaMaxima(60)).thenReturn(cursos);
        
        // Act & Assert
        mockMvc.perform(get("/api/cursos/carga-horaria/maxima")
                .param("horas", "60")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].cargaHoraria", lessThanOrEqualTo(60)));
        
        verify(cursoService, times(1)).buscarPorCargaHorariaMaxima(60);
    }
    
    @Test
    @DisplayName("Deve atualizar curso - PATCH /api/cursos/{id}")
    void deveAtualizarCurso() throws Exception {
        // Arrange
        String id = "id-update";
        CursoUpdateDTO request = CursoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .cargaHoraria(100)
            .build();
        
        CursoResponseDTO response = CursoResponseDTO.builder()
            .id(id)
            .nome("Nome Atualizado")
            .descricao("Descrição")
            .cargaHoraria(100)
            .build();
        
        when(cursoService.atualizar(eq(id), any(CursoUpdateDTO.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(patch("/api/cursos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Nome Atualizado"))
            .andExpect(jsonPath("$.cargaHoraria").value(100));
        
        verify(cursoService, times(1)).atualizar(eq(id), any(CursoUpdateDTO.class));
    }
    
    @Test
    @DisplayName("Deve deletar curso - DELETE /api/cursos/{id}")
    void deveDeletarCurso() throws Exception {
        // Arrange
        String id = "id-delete";
        doNothing().when(cursoService).excluir(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/cursos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        
        verify(cursoService, times(1)).excluir(id);
    }
    
    @Test
    @DisplayName("Deve retornar 404 ao deletar curso inexistente")
    void deveRetornar404AoDeletarCursoInexistente() throws Exception {
        // Arrange
        String id = "id-inexistente";
        doThrow(new ResourceNotFoundException("Curso", id))
            .when(cursoService).excluir(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/cursos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
