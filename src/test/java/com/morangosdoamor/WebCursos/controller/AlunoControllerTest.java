package com.morangosdoamor.WebCursos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import com.morangosdoamor.WebCursos.exception.BusinessException;
import com.morangosdoamor.WebCursos.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.service.AlunoService;
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
 * @WebMvcTest - Características e Importância:
 * - Teste de INTEGRAÇÃO focado na camada WEB (Controllers)
 * - Carrega APENAS beans MVC (@Controller, @RestController, @ControllerAdvice)
 * - NÃO carrega @Service, @Repository, @Component
 * - Configura MockMvc automaticamente para simular requisições HTTP
 * - Testa: serialização JSON, validações Bean Validation, status HTTP
 * - Mais rápido que @SpringBootTest (não sobe servidor Tomcat)
 * 
 * MockMvc - Simula requisições HTTP sem servidor real:
 * - perform(): Executa requisição (GET, POST, PUT, DELETE)
 * - andExpect(): Valida resposta (status, JSON, headers)
 * - andDo(): Actions adicionais (print, log)
 * 
 * @MockitoBean - Mock do Service:
 * - Service é mockado (não executa lógica real)
 * - Foca em testar: mapeamento de rotas, serialização, validação
 * 
 * DIFERENÇA: @WebMvcTest vs @SpringBootTest
 * - @WebMvcTest: Apenas camada web, Service mockado
 * - @SpringBootTest: Toda aplicação, Service real, banco real
 */
@WebMvcTest(AlunoController.class)
@DisplayName("AlunoController - Testes com @WebMvcTest e MockMvc")
class AlunoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private AlunoService alunoService;
    
    @Test
    @DisplayName("Deve criar aluno com sucesso - POST /api/alunos")
    void deveCriarAlunoComSucesso() throws Exception {
        // Arrange
        AlunoRequestDTO request = AlunoRequestDTO.builder()
            .nome("João Silva")
            .email("joao@exemplo.com")
            .matricula("MAT12345")
            .build();
        
        AlunoResponseDTO response = AlunoResponseDTO.builder()
            .id("id-123")
            .nome("João Silva")
            .email("joao@exemplo.com")
            .matricula("MAT12345")
            .build();
        
        when(alunoService.criar(any(AlunoRequestDTO.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("id-123"))
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.email").value("joao@exemplo.com"))
            .andExpect(jsonPath("$.matricula").value("MAT12345"));
        
        verify(alunoService, times(1)).criar(any(AlunoRequestDTO.class));
    }
    
    @Test
    @DisplayName("Deve retornar 400 ao criar aluno com dados inválidos")
    void deveRetornar400AoCriarAlunoComDadosInvalidos() throws Exception {
        // Arrange - nome vazio, email inválido
        AlunoRequestDTO request = AlunoRequestDTO.builder()
            .nome("")
            .email("email-invalido")
            .matricula("MAT")
            .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
        
        verify(alunoService, never()).criar(any(AlunoRequestDTO.class));
    }
    
    @Test
    @DisplayName("Deve retornar 400 ao criar aluno com email duplicado")
    void deveRetornar400AoCriarAlunoComEmailDuplicado() throws Exception {
        // Arrange
        AlunoRequestDTO request = AlunoRequestDTO.builder()
            .nome("Maria Santos")
            .email("maria@exemplo.com")
            .matricula("MAT54321")
            .build();
        
        when(alunoService.criar(any(AlunoRequestDTO.class)))
            .thenThrow(new BusinessException("Email já cadastrado"));
        
        // Act & Assert
        mockMvc.perform(post("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve listar todos os alunos - GET /api/alunos")
    void deveListarTodosOsAlunos() throws Exception {
        // Arrange
        List<AlunoResponseDTO> alunos = Arrays.asList(
            AlunoResponseDTO.builder().id("1").nome("Aluno 1").email("a1@exemplo.com").matricula("MAT001").build(),
            AlunoResponseDTO.builder().id("2").nome("Aluno 2").email("a2@exemplo.com").matricula("MAT002").build()
        );
        
        when(alunoService.listarTodos()).thenReturn(alunos);
        
        // Act & Assert
        mockMvc.perform(get("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome").value("Aluno 1"))
            .andExpect(jsonPath("$[1].nome").value("Aluno 2"));
        
        verify(alunoService, times(1)).listarTodos();
    }
    
    @Test
    @DisplayName("Deve buscar aluno por ID - GET /api/alunos/{id}")
    void deveBuscarAlunoPorId() throws Exception {
        // Arrange
        String id = "id-456";
        AlunoDetailResponseDTO response = AlunoDetailResponseDTO.builder()
            .id(id)
            .nome("Ana Costa")
            .email("ana@exemplo.com")
            .matricula("MAT11111")
            .build();
        
        when(alunoService.buscarPorId(id)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/alunos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.nome").value("Ana Costa"))
            .andExpect(jsonPath("$.email").value("ana@exemplo.com"));
        
        verify(alunoService, times(1)).buscarPorId(id);
    }
    
    @Test
    @DisplayName("Deve retornar 404 ao buscar aluno inexistente")
    void deveRetornar404AoBuscarAlunoInexistente() throws Exception {
        // Arrange
        String id = "id-inexistente";
        when(alunoService.buscarPorId(id))
            .thenThrow(new ResourceNotFoundException("Aluno", id));
        
        // Act & Assert
        mockMvc.perform(get("/api/alunos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Deve buscar aluno por email - GET /api/alunos/email/{email}")
    void deveBuscarAlunoPorEmail() throws Exception {
        // Arrange
        String email = "carlos@exemplo.com";
        AlunoResponseDTO response = AlunoResponseDTO.builder()
            .id("id-789")
            .nome("Carlos Lima")
            .email(email)
            .matricula("MAT22222")
            .build();
        
        when(alunoService.buscarPorEmail(email)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/alunos/email/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.nome").value("Carlos Lima"));
        
        verify(alunoService, times(1)).buscarPorEmail(email);
    }
    
    @Test
    @DisplayName("Deve buscar aluno por matrícula - GET /api/alunos/matricula/{matricula}")
    void deveBuscarAlunoPorMatricula() throws Exception {
        // Arrange
        String matricula = "MAT99999";
        AlunoResponseDTO response = AlunoResponseDTO.builder()
            .id("id-abc")
            .nome("Pedro Oliveira")
            .email("pedro@exemplo.com")
            .matricula(matricula)
            .build();
        
        when(alunoService.buscarPorMatricula(matricula)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/alunos/matricula/{matricula}", matricula)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matricula").value(matricula))
            .andExpect(jsonPath("$.nome").value("Pedro Oliveira"));
        
        verify(alunoService, times(1)).buscarPorMatricula(matricula);
    }
    
    @Test
    @DisplayName("Deve atualizar aluno - PATCH /api/alunos/{id}")
    void deveAtualizarAluno() throws Exception {
        // Arrange
        String id = "id-update";
        AlunoUpdateDTO request = AlunoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .build();
        
        AlunoResponseDTO response = AlunoResponseDTO.builder()
            .id(id)
            .nome("Nome Atualizado")
            .email("original@exemplo.com")
            .matricula("MAT88888")
            .build();
        
        when(alunoService.atualizar(eq(id), any(AlunoUpdateDTO.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(patch("/api/alunos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Nome Atualizado"));
        
        verify(alunoService, times(1)).atualizar(eq(id), any(AlunoUpdateDTO.class));
    }
    
    @Test
    @DisplayName("Deve deletar aluno - DELETE /api/alunos/{id}")
    void deveDeletarAluno() throws Exception {
        // Arrange
        String id = "id-delete";
        doNothing().when(alunoService).excluir(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/alunos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        
        verify(alunoService, times(1)).excluir(id);
    }
    
    @Test
    @DisplayName("Deve retornar 404 ao deletar aluno inexistente")
    void deveRetornar404AoDeletarAlunoInexistente() throws Exception {
        // Arrange
        String id = "id-inexistente";
        doThrow(new ResourceNotFoundException("Aluno", id))
            .when(alunoService).excluir(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/alunos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
