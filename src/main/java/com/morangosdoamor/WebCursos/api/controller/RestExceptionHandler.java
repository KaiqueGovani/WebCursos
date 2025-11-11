package com.morangosdoamor.WebCursos.api.controller;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.morangosdoamor.WebCursos.api.dto.ErrorResponse;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.DomainException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handler global para tratamento centralizado de exceções.
 * 
 * Clean Architecture: camada de interface lida com erros HTTP.
 * Converte exceções de domínio em respostas HTTP apropriadas.
 * 
 * Exceções tratadas:
 * - ResourceNotFoundException -> 404 Not Found
 * - BusinessRuleException -> 422 Unprocessable Entity
 * - MethodArgumentNotValidException -> 400 Bad Request (Bean Validation)
 * - DomainException -> 400 Bad Request
 * - Exception -> 500 Internal Server Error
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Trata ResourceNotFoundException (recurso não encontrado).
     * Retorna 404 Not Found.
     * 
     * @param exception Exceção lançada quando recurso não é encontrado
     * @param request Requisição HTTP que originou a exceção
     * @return Resposta HTTP 404 com detalhes do erro
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    /**
     * Trata BusinessRuleException (violação de regra de negócio).
     * Retorna 422 Unprocessable Entity.
     * 
     * @param exception Exceção lançada quando regra de negócio é violada
     * @param request Requisição HTTP que originou a exceção
     * @return Resposta HTTP 422 com detalhes do erro
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessRuleException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), request.getRequestURI());
    }

    /**
     * Trata erros de validação Bean Validation (@Valid).
     * Retorna 400 Bad Request com mensagens de validação concatenadas.
     * 
     * @param exception Exceção lançada quando validação de dados falha
     * @param request Requisição HTTP que originou a exceção
     * @return Resposta HTTP 400 com mensagens de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    /**
     * Trata DomainException (exceções genéricas do domínio).
     * Retorna 400 Bad Request.
     * 
     * @param exception Exceção genérica do domínio
     * @param request Requisição HTTP que originou a exceção
     * @return Resposta HTTP 400 com detalhes do erro
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    /**
     * Trata exceções genéricas não previstas.
     * Retorna 500 Internal Server Error.
     * Último handler na cadeia, captura qualquer exceção não tratada anteriormente.
     * 
     * @param exception Exceção genérica não prevista
     * @param request Requisição HTTP que originou a exceção
     * @return Resposta HTTP 500 com mensagem de erro genérica
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefault(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request.getRequestURI());
    }

    /**
     * Constrói resposta HTTP padronizada de erro.
     * Método auxiliar para criar respostas consistentes em todos os handlers.
     * 
     * @param status Status HTTP a ser retornado
     * @param message Mensagem de erro descritiva
     * @param path Caminho da requisição que originou o erro
     * @return Resposta HTTP com ErrorResponse preenchido
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path
        );
        return ResponseEntity.status(status).body(response);
    }
}
