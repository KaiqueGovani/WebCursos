package com.morangosdoamor.WebCursos.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Value Object que encapsula o conceito de matrícula do aluno.
 * Justificativa: Matrícula é um identificador com formato e regras específicas,
 * deve seguir padrão definido e ser único no contexto do negócio.
 */
@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Matricula {
    private static final int TAMANHO_MINIMO = 5;
    private static final int TAMANHO_MAXIMO = 20;
    
    private String matricula;
    
    public Matricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Matrícula não pode ser nula ou vazia");
        }
        
        String matriculaNormalizada = matricula.trim();
        
        if (matriculaNormalizada.length() < TAMANHO_MINIMO || 
            matriculaNormalizada.length() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException(
                String.format("Matrícula deve ter entre %d e %d caracteres", 
                    TAMANHO_MINIMO, TAMANHO_MAXIMO)
            );
        }
        
        this.matricula = matriculaNormalizada;
    }
}
