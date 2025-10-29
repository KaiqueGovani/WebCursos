package com.morangosdoamor.WebCursos.dto;

import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para DTOs de Curso
 * Valida Bean Validation e construção de objetos
 */
@DisplayName("Curso DTOs - Testes de Validação")
class CursoDTOTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("CursoRequestDTO válido não deve ter violações")
    void cursoRequestDTOValidoNaoDeveTerViolacoes() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("Java Avançado")
            .descricao("Curso de Java")
            .cargaHoraria(80)
            .prerequisitos(new String[]{"Java Básico"})
            .build();
        
        // Act
        Set<ConstraintViolation<CursoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("CursoRequestDTO com nome vazio deve ter violação")
    void cursoRequestDTOComNomeVazioDeveTerViolacao() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("")
            .cargaHoraria(80)
            .build();
        
        // Act
        Set<ConstraintViolation<CursoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("CursoRequestDTO com carga horária zero deve ter violação")
    void cursoRequestDTOComCargaHorariaZeroDeveTerViolacao() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("Java Avançado")
            .cargaHoraria(0)
            .build();
        
        // Act
        Set<ConstraintViolation<CursoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("CursoRequestDTO com carga horária acima do máximo deve ter violação")
    void cursoRequestDTOComCargaHorariaAcimaDoMaximoDeveTerViolacao() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("Java Avançado")
            .cargaHoraria(1001)
            .build();
        
        // Act
        Set<ConstraintViolation<CursoRequestDTO>> violations = validator.validate(dto);
        
        // Assert
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("CursoUpdateDTO com todos campos null não deve ter violações")
    void cursoUpdateDTOComCamposNullNaoDeveTerViolacoes() {
        // Arrange
        CursoUpdateDTO dto = CursoUpdateDTO.builder().build();
        
        // Act
        Set<ConstraintViolation<CursoUpdateDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("CursoResponseDTO deve construir corretamente")
    void cursoResponseDTODeveConstruirCorretamente() {
        // Act
        CursoResponseDTO dto = CursoResponseDTO.builder()
            .id("id-123")
            .nome("Python Básico")
            .descricao("Introdução ao Python")
            .cargaHoraria(40)
            .build();
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-123", dto.getId());
        assertEquals("Python Básico", dto.getNome());
        assertEquals(40, dto.getCargaHoraria());
    }
    
    @Test
    @DisplayName("CursoDetailResponseDTO deve construir com conversões e metadados")
    void cursoDetailResponseDTODeveConstruirComConversoesEMetadados() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        String[] prerequisitos = {"JavaScript", "React"};
        
        // Act
        CursoDetailResponseDTO dto = CursoDetailResponseDTO.builder()
            .id("id-456")
            .nome("React Native")
            .descricao("Desenvolvimento mobile")
            .cargaHoraria(50)
            .cargaHorariaEmDias(7.0)
            .cargaHorariaEmSemanas(2.0)
            .prerequisitos(prerequisitos)
            .criadoEm(now)
            .atualizadoEm(now)
            .build();
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-456", dto.getId());
        assertEquals(50, dto.getCargaHoraria());
        assertEquals(7.0, dto.getCargaHorariaEmDias());
        assertEquals(2.0, dto.getCargaHorariaEmSemanas());
        assertArrayEquals(prerequisitos, dto.getPrerequisitos());
        assertEquals(now, dto.getCriadoEm());
        assertEquals(now, dto.getAtualizadoEm());
    }
}
