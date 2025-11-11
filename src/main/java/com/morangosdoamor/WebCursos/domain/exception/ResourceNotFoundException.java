package com.morangosdoamor.WebCursos.domain.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * 
 * Exemplos de uso:
 * - Aluno não encontrado por ID
 * - Curso não encontrado por código
 * - Matrícula não encontrada
 * 
 * É convertida para resposta HTTP 404 Not Found pelo RestExceptionHandler.
 */
public class ResourceNotFoundException extends DomainException {

    /**
     * Constrói uma exceção de recurso não encontrado com a mensagem especificada.
     * 
     * @param message Mensagem descritiva indicando qual recurso não foi encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
