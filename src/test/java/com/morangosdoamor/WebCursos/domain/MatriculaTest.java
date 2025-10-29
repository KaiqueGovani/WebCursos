package com.morangosdoamor.WebCursos.domain;

import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Value Object Matricula
 * 
 * Vantagens de Testes Unitários vs Integração:
 * - Feedback imediato (milissegundos vs segundos)
 * - Identificam exatamente onde está o problema
 * - Não precisam de configuração de banco/contexto Spring
 * - Podem ser executados individualmente
 */
@DisplayName("Matricula Value Object - Testes Unitários")
class MatriculaTest {
    
    @Test
    @DisplayName("Deve criar matrícula válida")
    void deveCriarMatriculaValida() {
        // Arrange & Act
        Matricula matricula = new Matricula("MAT12345");
        
        // Assert
        assertNotNull(matricula);
        assertEquals("MAT12345", matricula.getMatricula());
    }
    
    @Test
    @DisplayName("Deve remover espaços em branco")
    void deveRemoverEspacosEmBranco() {
        // Arrange & Act
        Matricula matricula = new Matricula("  MAT12345  ");
        
        // Assert
        assertEquals("MAT12345", matricula.getMatricula());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para matrícula nula")
    void deveLancarExcecaoParaMatriculaNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Matricula(null)
        );
        
        assertEquals("Matrícula não pode ser nula ou vazia", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para matrícula vazia")
    void deveLancarExcecaoParaMatriculaVazia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Matricula("")
        );
        
        assertEquals("Matrícula não pode ser nula ou vazia", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para matrícula em branco")
    void deveLancarExcecaoParaMatriculaEmBranco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Matricula("   ")
        );
        
        assertEquals("Matrícula não pode ser nula ou vazia", exception.getMessage());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"1234", "123"})
    @DisplayName("Deve lançar exceção para matrícula muito curta")
    void deveLancarExcecaoParaMatriculaMuitoCurta(String matriculaCurta) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Matricula(matriculaCurta)
        );
        
        assertTrue(exception.getMessage().contains("entre 5 e 20 caracteres"));
    }
    
    @Test
    @DisplayName("Deve lançar exceção para matrícula muito longa")
    void deveLancarExcecaoParaMatriculaMuitoLonga() {
        // Arrange
        String matriculaLonga = "MAT123456789012345678901";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Matricula(matriculaLonga)
        );
        
        assertTrue(exception.getMessage().contains("entre 5 e 20 caracteres"));
    }
    
    @Test
    @DisplayName("Deve aceitar matrícula com tamanho mínimo")
    void deveAceitarMatriculaComTamanhoMinimo() {
        // Arrange & Act
        Matricula matricula = new Matricula("12345");
        
        // Assert
        assertEquals("12345", matricula.getMatricula());
    }
    
    @Test
    @DisplayName("Deve aceitar matrícula com tamanho máximo")
    void deveAceitarMatriculaComTamanhoMaximo() {
        // Arrange
        String mat = "12345678901234567890"; // 20 caracteres
        
        // Act
        Matricula matricula = new Matricula(mat);
        
        // Assert
        assertEquals(mat, matricula.getMatricula());
    }
    
    @Test
    @DisplayName("Deve considerar matrículas iguais")
    void deveConsiderarMatriculasIguais() {
        // Arrange
        Matricula mat1 = new Matricula("MAT12345");
        Matricula mat2 = new Matricula("MAT12345");
        
        // Act & Assert
        assertEquals(mat1, mat2);
        assertEquals(mat1.hashCode(), mat2.hashCode());
    }
    
    @Test
    @DisplayName("Deve aceitar matrícula alfanumérica")
    void deveAceitarMatriculaAlfanumerica() {
        // Arrange & Act
        Matricula matricula = new Matricula("ABC123XYZ");
        
        // Assert
        assertEquals("ABC123XYZ", matricula.getMatricula());
    }
}
