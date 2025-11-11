package com.morangosdoamor.WebCursos.domain.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * 
 * Exemplos de uso:
 * - Matrícula duplicada
 * - Código de curso já cadastrado
 * - Email já cadastrado para outro aluno
 * - Nota fora do intervalo válido
 * 
 * É convertida para resposta HTTP 422 Unprocessable Entity pelo RestExceptionHandler.
 */
public class BusinessRuleException extends DomainException {

    /**
     * Constrói uma exceção de regra de negócio com a mensagem especificada.
     * 
     * @param message Mensagem descritiva da violação da regra de negócio
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
