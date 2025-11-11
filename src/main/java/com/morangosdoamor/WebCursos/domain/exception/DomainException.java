package com.morangosdoamor.WebCursos.domain.exception;

/**
 * Exceção base do domínio.
 * Representa erros relacionados a regras de negócio ou violações de invariantes do domínio.
 * 
 * Todas as exceções específicas do domínio devem estender esta classe.
 * É convertida para resposta HTTP 400 Bad Request pelo RestExceptionHandler.
 */
public class DomainException extends RuntimeException {

    /**
     * Constrói uma exceção de domínio com a mensagem especificada.
     * 
     * @param message Mensagem descritiva do erro
     */
    public DomainException(String message) {
        super(message);
    }
}
