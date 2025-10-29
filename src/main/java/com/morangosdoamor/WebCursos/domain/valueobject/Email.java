package com.morangosdoamor.WebCursos.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.regex.Pattern;

/**
 * Value Object que encapsula o conceito de email.
 * Justificativa: Email possui regras de validação específicas (formato, presença de @, domínio)
 * e deve ser sempre válido no domínio da aplicação.
 */
@Embeddable
@Getter
@NoArgsConstructor // Requerido pelo JPA
@EqualsAndHashCode
@ToString
public class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private String email;
    
    public Email(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        
        String emailNormalizado = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
        
        this.email = emailNormalizado;
    }
}
