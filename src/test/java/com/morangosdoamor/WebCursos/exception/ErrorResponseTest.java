package com.morangosdoamor.WebCursos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para classes de resposta de erro
 */
@DisplayName("Error Response Classes - Testes")
class ErrorResponseTest {
    
    @Test
    @DisplayName("ResourceNotFoundException deve ser criada corretamente")
    void resourceNotFoundExceptionDeveSerCriadaCorretamente() {
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Aluno", "id-123");
        
        // Assert
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Aluno"));
        assertTrue(exception.getMessage().contains("id-123"));
    }
    
    @Test
    @DisplayName("ResourceNotFoundException com mensagem customizada")
    void resourceNotFoundExceptionComMensagemCustomizada() {
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Mensagem customizada");
        
        // Assert
        assertEquals("Mensagem customizada", exception.getMessage());
    }
    
    @Test
    @DisplayName("BusinessException deve ser criada corretamente")
    void businessExceptionDeveSerCriadaCorretamente() {
        // Act
        BusinessException exception = new BusinessException("Email já cadastrado");
        
        // Assert
        assertNotNull(exception);
        assertEquals("Email já cadastrado", exception.getMessage());
    }
    
    @Test
    @DisplayName("ErrorResponse deve construir corretamente")
    void errorResponseDeveConstruirCorretamente() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // Act
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(now)
            .status(404)
            .error("Not Found")
            .message("Recurso não encontrado")
            .build();
        
        // Assert
        assertNotNull(response);
        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Recurso não encontrado", response.getMessage());
    }
    
    @Test
    @DisplayName("ErrorResponse com construtor padrão")
    void errorResponseComConstrutorPadrao() {
        // Act
        ErrorResponse response = new ErrorResponse();
        response.setStatus(400);
        response.setError("Bad Request");
        response.setMessage("Dados inválidos");
        
        // Assert
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Dados inválidos", response.getMessage());
    }
    
    @Test
    @DisplayName("ErrorResponse com construtor completo")
    void errorResponseComConstrutorCompleto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // Act
        ErrorResponse response = new ErrorResponse(now, 500, "Internal Server Error", "Erro interno");
        
        // Assert
        assertEquals(now, response.getTimestamp());
        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertEquals("Erro interno", response.getMessage());
    }
    
    @Test
    @DisplayName("ValidationErrorResponse deve construir com erros")
    void validationErrorResponseDeveConstruirComErros() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Email inválido");
        errors.put("nome", "Nome não pode ser vazio");
        
        // Act
        ValidationErrorResponse response = ValidationErrorResponse.builder()
            .timestamp(now)
            .status(400)
            .error("Validation Failed")
            .message("Dados inválidos")
            .errors(errors)
            .build();
        
        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatus());
        assertEquals(2, response.getErrors().size());
        assertTrue(response.getErrors().containsKey("email"));
        assertTrue(response.getErrors().containsKey("nome"));
    }
    
    @Test
    @DisplayName("ValidationErrorResponse com construtor padrão")
    void validationErrorResponseComConstrutorPadrao() {
        // Act
        ValidationErrorResponse response = new ValidationErrorResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("campo", "erro");
        response.setErrors(errors);
        
        // Assert
        assertNotNull(response.getErrors());
        assertEquals(1, response.getErrors().size());
    }
    
    @Test
    @DisplayName("ValidationErrorResponse com construtor completo")
    void validationErrorResponseComConstrutorCompleto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("matricula", "Matrícula inválida");
        
        // Act
        ValidationErrorResponse response = new ValidationErrorResponse(
            now, 400, "Validation Failed", "Dados inválidos", errors
        );
        
        // Assert
        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Validation Failed", response.getError());
        assertEquals("Dados inválidos", response.getMessage());
        assertEquals(1, response.getErrors().size());
    }
}
