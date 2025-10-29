package com.morangosdoamor.WebCursos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta detalhada de Curso
 * Inclui todos os campos, incluindo pré-requisitos e metadados
 * Usado em consultas específicas (GET /cursos/{id})
 * DDD: conversão explícita de Value Objects para tipos primitivos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoDetailResponseDTO {
    
    private String id;
    private String nome;
    private String descricao;
    private Integer cargaHoraria;
    private String[] prerequisitos;
    
    // Informações derivadas do Value Object CargaHoraria
    private Double cargaHorariaEmDias;
    private Double cargaHorariaEmSemanas;
    
    // Metadados para auditoria futura
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
