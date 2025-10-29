package com.morangosdoamor.WebCursos.exception;

/**
 * Exception para violações de regras de negócio
 * DDD: exceções expressam invariantes do domínio
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
