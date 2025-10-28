package com.morangosdoamor.WebCursos.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.regex.Pattern;

/**
 * Value Object que encapsula o conceito de email.
 * Justificativa: Email possui regras de validação específicas (formato, presença de @, domínio)
 * e deve ser sempre válido no domínio da aplicação.
 */
@Getter
@EqualsAndHashCode
@ToString
public class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private final String valor;
    
    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        
        String emailNormalizado = valor.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new IllegalArgumentException("Email inválido: " + valor);
        }
        
        this.valor = emailNormalizado;
    }
}
