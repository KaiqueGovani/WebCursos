package com.morangosdoamor.WebCursos.domain.enums;

/**
 * Enum que representa os possíveis status de uma matrícula.
 * 
 * Estados do ciclo de vida da matrícula:
 * - MATRICULADO: Aluno está matriculado no curso, mas ainda não concluiu
 * - CONCLUIDO: Aluno concluiu o curso (com ou sem aprovação)
 * 
 * DDD: Representa um conceito do domínio que encapsula estados válidos da entidade Matricula.
 */
public enum MatriculaStatus {
    /**
     * Status inicial da matrícula.
     * Aluno está matriculado no curso, mas ainda não finalizou todas as atividades.
     */
    MATRICULADO,
    
    /**
     * Status final da matrícula.
     * Aluno concluiu o curso e teve sua nota final registrada.
     * A aprovação (nota ≥ 7.0) é verificada pelo método estaAprovado() da entidade Matricula.
     */
    CONCLUIDO
}
