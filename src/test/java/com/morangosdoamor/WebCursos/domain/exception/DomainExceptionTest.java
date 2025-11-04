package com.morangosdoamor.WebCursos.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void deveExporMensagemDeNegocio() {
        BusinessRuleException exception = new BusinessRuleException("erro de negocio");
        assertThat(exception.getMessage()).contains("erro de negocio");
    }

    @Test
    void deveExporMensagemDeRecursoNaoEncontrado() {
        ResourceNotFoundException exception = new ResourceNotFoundException("nao encontrado");
        assertThat(exception.getMessage()).contains("nao encontrado");
    }
}
