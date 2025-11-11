package com.morangosdoamor.WebCursos.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value Object que encapsula o conceito de email de um aluno.
 * Justificativa: Email possui regras de negócio (formato válido, obrigatório, único)
 * e representa um conceito do domínio que pode ter validações específicas.
 * 
 * Validações aplicadas via Bean Validation:
 * - @Email: valida formato de email
 * - @NotBlank: garante que não seja nulo ou vazio
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Email {

    @jakarta.validation.constraints.Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String value;

    /**
     * Retorna o valor do email como String.
     * 
     * @return Endereço de email
     */
    @Override
    public String toString() {
        return value;
    }
}
