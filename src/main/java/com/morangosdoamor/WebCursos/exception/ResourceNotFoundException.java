package com.morangosdoamor.WebCursos.exception;

/**
 * Exception lançada quando um recurso não é encontrado
 * Clean Architecture: exceções de domínio independentes de infraestrutura
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s não encontrado(a) com id: %s", resource, id));
    }
}
