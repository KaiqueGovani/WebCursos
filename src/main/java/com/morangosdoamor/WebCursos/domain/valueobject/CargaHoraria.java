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
    
    /**
     * Construtor que cria uma instância de CargaHoraria com validação.
     * Valida se a carga horária está dentro dos limites permitidos (1 a 1000 horas).
     * 
     * @param cargaHoraria Carga horária em horas (deve estar entre 1 e 1000)
     * @throws IllegalArgumentException se a carga horária estiver fora dos limites permitidos
     */
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
    
    /**
     * Retorna a carga horária em horas.
     * 
     * @return Carga horária em horas
     */
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
    
    /**
     * Retorna representação textual da carga horária.
     * 
     * @return String no formato "X hora(s)"
     */
    @Override
    public String toString() {
        return cargaHoraria + " hora(s)";
    }
}

