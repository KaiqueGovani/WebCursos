package com.morangosdoamor.WebCursos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para GlobalExceptionHandler
 * Garante tratamento correto de exceções
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Testes de Tratamento de Exceções")
class GlobalExceptionHandlerTest {
    
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    @Test
    @DisplayName("Deve retornar 404 para ResourceNotFoundException")
    void deveRetornar404ParaResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Aluno", "id-123");
        
        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Aluno"));
    }
    
    @Test
    @DisplayName("Deve retornar 400 para BusinessException")
    void deveRetornar400ParaBusinessException() {
        // Arrange
        BusinessException exception = new BusinessException("Email já cadastrado");
        
        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Email já cadastrado", response.getBody().getMessage());
    }
    
    @Test
    @DisplayName("Deve retornar 400 para IllegalArgumentException")
    void deveRetornar400ParaIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");
        
        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Argumento inválido", response.getBody().getMessage());
    }
    
    @Test
    @DisplayName("Deve retornar 400 com detalhes para MethodArgumentNotValidException")
    void deveRetornar400ComDetalhesParaMethodArgumentNotValidException() {
        // Arrange
        FieldError fieldError = new FieldError("alunoRequestDTO", "email", "Email inválido");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        
        // Act
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleValidationException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors().containsKey("email"));
        assertEquals("Email inválido", response.getBody().getErrors().get("email"));
    }
    
    @Test
    @DisplayName("Deve retornar 500 para Exception genérica")
    void deveRetornar500ParaExceptionGenerica() {
        // Arrange
        Exception exception = new Exception("Erro interno");
        
        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}
