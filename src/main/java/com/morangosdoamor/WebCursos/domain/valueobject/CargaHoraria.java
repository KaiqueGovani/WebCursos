package com.morangosdoamor.WebCursos.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value Object que encapsula o conceito de carga horária de um curso.
 * Justificativa: Carga horária possui regras de negócio (deve ser positiva, limites mínimos/máximos)
 * e representa um conceito do domínio que pode ter operações específicas.
 */
@Embeddable
@Getter
@NoArgsConstructor // Requerido pelo JPA
@EqualsAndHashCode
public class CargaHoraria {
    private static final int MINIMO_HORAS = 1;
    private static final int MAXIMO_HORAS = 1000;
    
    private int cargaHoraria;
    
    public CargaHoraria(int cargaHoraria) {
        if (cargaHoraria < MINIMO_HORAS) {
            throw new IllegalArgumentException(
                "Carga horária deve ser de pelo menos " + MINIMO_HORAS + " hora(s)"
            );
        }
        
        if (cargaHoraria > MAXIMO_HORAS) {
            throw new IllegalArgumentException(
                "Carga horária não pode exceder " + MAXIMO_HORAS + " horas"
            );
        }
        
        this.cargaHoraria = cargaHoraria;
    }
    
    public int getHoras() {
        return cargaHoraria;
    }
    
    /**
     * Converte a carga horária para dias úteis (considerando 8 horas/dia).
     */
    public int emDias() {
        return (int) Math.ceil(cargaHoraria / 8.0);
    }
    
    /**
     * Converte a carga horária para semanas (considerando 40 horas/semana).
     */
    public int emSemanas() {
        return (int) Math.ceil(cargaHoraria / 40.0);
    }
    
    @Override
    public String toString() {
        return cargaHoraria + " hora(s)";
    }
}
