package com.morangosdoamor.WebCursos.dto;

import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para DTOs de Aluno
 * Valida Bean Validation e construção de objetos
 */
@DisplayName("Aluno DTOs - Testes de Validação")
class AlunoDTOTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("AlunoRequestDTO válido não deve ter violações")
    void alunoRequestDTOValidoNaoDeveTerViolacoes() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("João Silva")
            .email("joao@exemplo.com")
            .matricula("MAT12345")
            .build();
        
        // Act
        Set<ConstraintViolation<AlunoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlunoRequestDTO com nome vazio deve ter violação")
    void alunoRequestDTOComNomeVazioDeveTerViolacao() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("")
            .email("joao@exemplo.com")
            .matricula("MAT12345")
            .build();
        
        // Act
        Set<ConstraintViolation<AlunoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlunoRequestDTO com email inválido deve ter violação")
    void alunoRequestDTOComEmailInvalidoDeveTerViolacao() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("João Silva")
            .email("email-invalido")
            .matricula("MAT12345")
            .build();
        
        // Act
        Set<ConstraintViolation<AlunoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlunoRequestDTO com matrícula curta deve ter violação")
    void alunoRequestDTOComMatriculaCurtaDeveTerViolacao() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("João Silva")
            .email("joao@exemplo.com")
            .matricula("MAT")
            .build();
        
        // Act
        Set<ConstraintViolation<AlunoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlunoUpdateDTO com todos campos null não deve ter violações")
    void alunoUpdateDTOComCamposNullNaoDeveTerViolacoes() {
        // Arrange
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder().build();
        
        // Act
        Set<ConstraintViolation<AlunoUpdateDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlunoResponseDTO deve construir corretamente")
    void alunoResponseDTODeveConstruirCorretamente() {
        // Act
        AlunoResponseDTO dto = AlunoResponseDTO.builder()
            .id("id-123")
            .nome("Maria Santos")
            .email("maria@exemplo.com")
            .matricula("MAT54321")
            .build();
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-123", dto.getId());
        assertEquals("Maria Santos", dto.getNome());
        assertEquals("maria@exemplo.com", dto.getEmail());
        assertEquals("MAT54321", dto.getMatricula());
    }
    
    @Test
    @DisplayName("AlunoDetailResponseDTO deve construir corretamente")
    void alunoDetailResponseDTODeveConstruirCorretamente() {
        // Act
        AlunoDetailResponseDTO dto = AlunoDetailResponseDTO.builder()
            .id("id-456")
            .nome("Pedro Oliveira")
            .email("pedro@exemplo.com")
            .matricula("MAT99999")
            .build();
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-456", dto.getId());
        assertEquals("Pedro Oliveira", dto.getNome());
        assertEquals("pedro@exemplo.com", dto.getEmail());
        assertEquals("MAT99999", dto.getMatricula());
    }
}
