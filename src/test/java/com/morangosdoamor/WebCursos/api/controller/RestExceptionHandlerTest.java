package com.morangosdoamor.WebCursos.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.morangosdoamor.WebCursos.api.dto.ErrorResponse;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.DomainException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;

class RestExceptionHandlerTest {

    private RestExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/teste");
    }

    @Test
    void deveTratarRecursoNaoEncontrado() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new ResourceNotFoundException("não encontrado"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message()).contains("não encontrado");
    }

    @Test
    void deveTratarErroDeNegocio() {
        ResponseEntity<ErrorResponse> response = handler.handleBusiness(new BusinessRuleException("regra"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void deveTratarErroDeValidacao() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objeto");
        bindingResult.addError(new FieldError("objeto", "campo", "mensagem"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).contains("mensagem");
    }

    @Test
    void deveTratarErroDeDominioGenerico() {
        ResponseEntity<ErrorResponse> response = handler.handleDomain(new DomainException("dominio"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deveTratarErroGenerico() {
        ResponseEntity<ErrorResponse> response = handler.handleDefault(new RuntimeException("erro"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
